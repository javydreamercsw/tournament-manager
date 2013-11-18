package net.sourceforge.javydreamercsw.database.storage.db;

import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;
import org.junit.Before;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractServerTest {

    public AbstractServerTest() {
    }

    @Before
    public void setUp() {
        DataBaseManager.setPersistenceUnitName("TestTMPU");
    }
}
