package com.github.javydreamercsw.tournament.manager.ui;

import static com.github.javydreamercsw.database.storage.db.server.DataBaseManager.JNDI_DATASOURCE_NAME;
import static java.util.logging.Level.SEVERE;
import static java.util.logging.Logger.getLogger;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.sessions.JNDIConnector;
import org.eclipse.persistence.sessions.Session;

/**
 * See http://wiki.eclipse.org/Customizing_the_EclipseLink_Application_(ELUG)
 * Use for clients that would like to use a JTA SE pu instead of a
 * RESOURCE_LOCAL SE pu.
 */
public class JPAEclipseLinkSessionCustomizer implements SessionCustomizer {

    private static final Logger LOG
            = getLogger(JPAEclipseLinkSessionCustomizer.class.getSimpleName());

    /**
     * Get a dataSource connection and set it on the session with
     * lookupType=STRING_LOOKUP
     *
     * @param session Session to customize
     * @throws java.lang.Exception on error.
     */
    @Override
    public void customize(Session session) throws Exception {
        JNDIConnector connector;
        // Initialize session customizer
        DataSource dataSource;
        try {
            Context context = new InitialContext();
            if (null == context) {
                throw new Exception("Context is null");
            }
            connector = (JNDIConnector) session.getLogin().getConnector(); // possible CCE
            // Lookup this new dataSource
            dataSource = (DataSource) context.lookup(JNDI_DATASOURCE_NAME);
            connector.setDataSource(dataSource);

            // Set the new connection on the session
            session.getLogin().setConnector(connector);
        }
        catch (Exception e) {
            LOG.log(SEVERE, JNDI_DATASOURCE_NAME, e);
        }
    }
}
