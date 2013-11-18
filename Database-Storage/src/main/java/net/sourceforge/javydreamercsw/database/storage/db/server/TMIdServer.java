package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.database.storage.db.TmId;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TmIdJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageException;

/**
 *
 * @author Javier A. Ortiz Bultron<javier.ortiz.78@gmail.com>
 */
public final class TMIdServer extends TmId implements DatabaseEntity<TmId> {

    private static final long serialVersionUID = 1L;

    public TMIdServer(Integer id) throws StorageException {
        TmIdJpaController controller = new TmIdJpaController(
                DataBaseManager.getEntityManagerFactory());
        TmId vmId = controller.findTmId(id);
        if (vmId != null) {
            update((TMIdServer) this, vmId);
        } else {
            throw new StorageException("VMId with id: " + id + " not found!");
        }
    }

    public TMIdServer(String tablename, int lastId) {
        super(tablename, lastId);
        setId(0);
    }

    //write to db
    @Override
    public int write2DB() throws StorageException {
        try {
            TmIdJpaController controller = new TmIdJpaController(
                    DataBaseManager.getEntityManagerFactory());
            TmId vmId;
            if (getId() > 0) {
                vmId = controller.findTmId(getId());
                vmId.setId(getId());
                vmId.setLastId(getLastId());
                vmId.setTableName(getTableName());
                controller.edit(vmId);
            } else {
                vmId = new TmId();
                vmId.setId(getId());
                vmId.setLastId(getLastId());
                vmId.setTableName(getTableName());
                controller.create(vmId);
            }
            return getId();
        } catch (Exception ex) {
            Logger.getLogger(TMIdServer.class.getSimpleName()).log(Level.SEVERE, null, ex);
            throw new StorageException(ex);
        }
    }

    public static int deleteFromDB(TmId id) throws StorageException {
        TmIdJpaController controller = new TmIdJpaController(
                DataBaseManager.getEntityManagerFactory());
        if (id != null) {
            try {
                controller.destroy(id.getId());
            } catch (NonexistentEntityException ex) {
                throw new StorageException(ex);
            }
        }
        return 0;
    }

    public static TMIdServer getVMId(String table) throws StorageException {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("tableName", table);
        List<Object> result = DataBaseManager.namedQuery("VmId.findByTableName", parameters);
        if (!result.isEmpty()) {
            return new TMIdServer(((TmId) result.get(0)).getId());
        } else {
            throw new StorageException("Unable to find VM id for: " + table);
        }
    }

    public static int getNextId(String table) throws StorageException {
        TMIdServer vmId = getVMId(table);
        vmId.setLastId(vmId.getLastId() + 1);
        vmId.write2DB();
        return vmId.getLastId();
    }

    public static List<TMIdServer> getIds() throws StorageException {
        ArrayList<TMIdServer> ids = new ArrayList<>();
        List<Object> result = DataBaseManager.namedQuery("VMId.findAll");
        if (!result.isEmpty()) {
            for (Object o : result) {
                ids.add(new TMIdServer(((TmId) o).getId()));
            }
        } else {
            throw new StorageException("No ids found!");
        }
        return ids;
    }

    @Override
    public TmId getEntity() {
        return new TmIdJpaController(
                DataBaseManager.getEntityManagerFactory()).findTmId(getId());
    }

    @Override
    public void update(TmId target, TmId source) {
        target.setId(source.getId());
        target.setLastId(source.getLastId());
        target.setTableName(source.getTableName());
    }
}
