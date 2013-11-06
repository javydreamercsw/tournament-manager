package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.TestHelper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServerTest extends AbstractServerTest {

    public PlayerServerTest() {
    }

    /**
     * Test of write2DB method, of class PlayerServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        DataBaseManager.setPU("TestTMPU");
        PlayerServer instance = TestHelper.createPlayer("Test");
        assertTrue(instance.write2DB() > 0);
        assertEquals(0, instance.getRecordList().size());
        assertEquals(0, instance.getTeamList().size());
        instance.getRecordList().add(new Record());
        instance.write2DB();
        assertEquals(1, instance.getRecordList().size());
        instance.getTeamList().add(new Team());
        instance.write2DB();
        assertEquals(1, instance.getTeamList().size());
    }
}
