package net.sourceforge.javydreamercsw.database.storage.db.server;

import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationState;
import com.googlecode.flyway.core.exception.FlywayException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TableGenerator;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.sql.DataSource;
import net.sourceforge.javydreamercsw.database.storage.db.TmId;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TmIdJpaController;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageException;
import org.h2.jdbcx.JdbcDataSource;

public class DataBaseManager {

    private static String PU = "TMPU";
    private static EntityManagerFactory emf = null;
    private static EntityManager em;
    private static Map<String, Object> properties;
    private static final Logger LOG
            = Logger.getLogger(DataBaseManager.class.getSimpleName());
    private static boolean dbError = false;
    private static DBState state;

    public DataBaseManager() {
        try {
            state = DBState.START_UP;
            reload();
        } catch (StorageException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory(PU);
        }
        return emf;
    }

    /**
     * Get the current persistence unit name
     *
     * @return current persistence unit name
     */
    public static String getPersistenceUnitName() {
        return PU;
    }

    /**
     * @param aPU the PU to set
     */
    public static void setPersistenceUnitName(String aPU) {
        PU = aPU;
        LOG.log(Level.FINE, "Changed persistence unit name to: {0}", PU);
        //Set it to null so it's recreated with new Persistence Unit next time is requested.
        emf = null;
        em = null;
        dbError = false;
        try {
            reload();
        } catch (StorageException ex) {
            LOG.log(Level.SEVERE, null, ex);
            dbError = true;
        }
    }

    public static void reload() throws StorageException {
        reload(false);
    }

    public static void reload(boolean close) throws StorageException {
        if (close) {
            close();
        }
        getEntityManager();
        updateDBState();
        generateIDs();
    }

    private static void generateIDs() {
        if (!dbError) {
            LOG.log(Level.FINE,
                    "Creating ids to work around eclipse issue "
                    + "https://bugs.eclipse.org/bugs/show_bug.cgi?id=366852");
            for (EmbeddableType et : getEntityManager().getMetamodel().getEmbeddables()) {
                processFields(et.getJavaType().getDeclaredFields());
            }
            for (EntityType et : getEntityManager().getMetamodel().getEntities()) {
                processFields(et.getBindableJavaType().getDeclaredFields());
            }
            LOG.log(Level.FINE, "Done!");
        }
    }

    @SuppressWarnings("unchecked")
    private static void processFields(Field[] fields) {
        try {
            for (Field field : fields) {
                if (field.isAnnotationPresent(TableGenerator.class)) {
                    field.setAccessible(true);
                    TableGenerator annotation = 
                            field.getAnnotation(TableGenerator.class);
                    field.setAccessible(false);
                    Map<String, Object> parameters = new HashMap<>();
                    String tableName = annotation.pkColumnValue();
                    parameters.put("tableName", tableName);
                    if (DataBaseManager.namedQuery("TmId.findByTableName",
                            parameters, false).isEmpty()) {
                        LOG.log(Level.FINE, "Adding: {0}: {1}",
                                new Object[]{tableName, 
                                    annotation.initialValue() - 1});
                        TMIdServer temp = new TMIdServer(tableName, 
                                annotation.initialValue() - 1);
                        temp.write2DB();
                        LOG.log(Level.FINE, "Added: {0}: {1}",
                                new Object[]{tableName, 
                                    annotation.initialValue() - 1});
                    }
                }
            }
        } catch (StorageException ex1) {
            LOG.log(Level.SEVERE, null, ex1);
            dbError = true;
        } finally {
            if (LOG.isLoggable(Level.CONFIG)) {
                TmIdJpaController controller = new TmIdJpaController(
                        DataBaseManager.getEntityManagerFactory());
                for (TmId next : controller.findTmIdEntities()) {
                    LOG.log(Level.CONFIG, "{0}, {1}, {2}", 
                            new Object[]{next.getId(),
                        next.getTableName(), next.getLastId()});
                }
            }
        }
    }

    public static void updateDBState() {
        DataSource ds = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            ds = (javax.sql.DataSource) 
                    new InitialContext().lookup("java:comp/env/jdbc/VMDB");
            conn = ds.getConnection();
        } catch (NamingException ne) {
            LOG.log(Level.FINE, null, ne);
            if (emf == null) {
                try {
                    //It might be the tests, use an H2 Database
                    ds = new JdbcDataSource();
                    ((JdbcDataSource) ds).setPassword("");
                    ((JdbcDataSource) ds).setUser("tm_user");
                    ((JdbcDataSource) ds).setURL(
                            "jdbc:h2:file:data/tournament-manager;AUTO_SERVER=TRUE");
                    //Load the H2 driver
                    Class.forName("org.h2.Driver");
                    conn = ds.getConnection();
                } catch (ClassNotFoundException | SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    dbError = true;
                }
            } else {
                EntityTransaction transaction = getEntityManager().getTransaction();
                transaction.begin();
                conn = getEntityManager().unwrap(java.sql.Connection.class);
                transaction.commit();
            }
        } catch (SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            dbError = true;
        }
        if (conn != null) {
            try {
                stmt = conn.prepareStatement("select * from match_result_type");
                rs = stmt.executeQuery();
                if (!rs.next()) {
                    //Tables there but empty? Not safe to proceed
                    setState(DBState.NEED_MANUAL_UPDATE);
                }
            } catch (SQLException ex) {
                LOG.log(Level.FINE, null, ex);
                //Need INIT, probably nothing there
                setState(DBState.NEED_INIT);
                //Create the database
                getEntityManager();
            } finally {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
                try {
                    if (rs != null) {
                        rs.close();
                    }
                } catch (SQLException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                }
            }
        }
        if (ds != null) {
            //Initialize flyway
            initializeFlyway(ds);
            updateDatabase(ds);
        } else {
            state = DBState.ERROR;
        }

        if (state != DBState.VALID) {
            waitForDB();
        }
    }

    public static void waitForDB() {
        while (DataBaseManager.getState() != DBState.VALID
                && DataBaseManager.getState() != DBState.UPDATED
                && DataBaseManager.getState() != DBState.ERROR) {
            LOG.log(Level.INFO,
                    "Waiting for DB initialization. Current state: {0}",
                    (DataBaseManager.getState() != null ? 
                            DataBaseManager.getState().name() : null));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
        LOG.log(Level.INFO, "DB ready, resuming...");
    }

    /**
     * @return the state
     */
    public static DBState getState() {
        return state;
    }

    private static void initializeFlyway(DataSource dataSource) {
        assert dataSource != null;
        setState(DBState.START_UP);
        Flyway flyway = new Flyway();
        flyway.setDataSource(dataSource);
        MigrationInfo status = flyway.info().current();
        if (status == null) {
            setState(DBState.NEED_INIT);
            LOG.info("Initialize the metadata...");
            try {
                flyway.init();
                LOG.info("Done!");
            } catch (FlywayException fe) {
                LOG.log(Level.SEVERE, "Unable to initialize database", fe);
                setState(DBState.ERROR);
            }
        } else {
            LOG.info("Database has Flyway metadata already...");
            displayDBStatus(status);
        }
    }

    private static void displayDBStatus(MigrationInfo status) {
        LOG.log(Level.INFO, "Description: {0}\nState: {1}\nVersion: {2}",
                new Object[]{status.getDescription(), status.getState(), 
                    status.getVersion()});
    }

    protected static void setState(DBState newState) {
        state = newState;
    }

    private static void updateDatabase(DataSource dataSource) {
        Flyway flyway = new Flyway();
        try {
            flyway.setDataSource(dataSource);
            flyway.setLocations("db.migration");
            LOG.info("Starting migration...");
            flyway.migrate();
            LOG.info("Done!");
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to migrate data", fe);
            setState(DBState.ERROR);
        }
        try {
            LOG.info("Validating migration...");
            flyway.validate();
            LOG.info("Done!");
            setState(flyway.info().current().getState() == 
                    MigrationState.SUCCESS ? DBState.VALID : DBState.ERROR);
        } catch (FlywayException fe) {
            LOG.log(Level.SEVERE, "Unable to validate", fe);
            setState(DBState.ERROR);
        }
    }

    public static EntityManager getEntityManager() {
        if (em == null) {
            em = getEntityManagerFactory().createEntityManager();
            LOG.log(Level.FINE,
                    "Creating EntityManager from: {0}", PU);
            properties = em.getProperties();
        }
        return em;
    }

    /**
     * @return the properties
     */
    public static Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @return query result
     */
    public static List<Object> namedQuery(String query) {
        return namedQuery(query, null, false);
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     * @param parameters query parameters
     */
    public static void namedUpdateQuery(String query, 
            Map<String, Object> parameters) {
        namedQuery(query, parameters, true);
    }

    /**
     * Named query (not for updates)
     *
     * @param query query to execute
     * @param parameters query parameters
     * @return query result
     */
    public static List<Object> namedQuery(String query, 
            Map<String, Object> parameters) {
        return namedQuery(query, parameters, false);
    }

    @SuppressWarnings("unchecked")
    private static List<Object> namedQuery(String query, 
            Map<String, Object> parameters, boolean change) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (change) {
            transaction.begin();
        }
        Query q = getEntityManager().createNamedQuery(query);
        if (parameters != null) {
            Iterator<Map.Entry<String, Object>> entries = 
                    parameters.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, Object> e = entries.next();
                q.setParameter(e.getKey(), e.getValue());
            }
        }
        if (change) {
            transaction.commit();
        }
        return q.getResultList();
    }

    public static void close() {
        getEntityManager().close();
        getEntityManagerFactory().close();
    }

    public static EntityTransaction getTransaction() {
        return getEntityManager().getTransaction();
    }

    /**
     * Named query that will modify the database
     *
     * @param query query to execute
     */
    public static void namedUpdateQuery(String query) {
        namedQuery(query, null, true);
    }

    public static List<Object> nativeQuery(String query) {
        EntityTransaction transaction = getEntityManager().getTransaction();
        transaction.begin();
        List<Object> resultList = 
                getEntityManager().createNativeQuery(query).getResultList();
        transaction.commit();
        return resultList;
    }

    public static void nativeUpdateQuery(String query) {
        boolean atomic = false;
        EntityTransaction transaction = getEntityManager().getTransaction();
        if (!getEntityManager().getTransaction().isActive()) {
            transaction.begin();
            atomic = true;
        }
        getEntityManager().createNativeQuery(query).executeUpdate();
        if (atomic) {
            transaction.commit();
        }
    }
}
