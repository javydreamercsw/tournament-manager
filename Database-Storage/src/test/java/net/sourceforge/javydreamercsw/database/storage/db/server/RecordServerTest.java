package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RecordServerTest extends AbstractServerTest {

    public RecordServerTest() {
    }

    /**
     * Test of write2DB method, of class RecordServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        RecordServer instance = new RecordServer();
        assertTrue(instance.write2DB() > 0);
        assertEquals(0, instance.getWins());
        assertEquals(0, instance.getLoses());
        assertEquals(0, instance.getDraws());
        instance = new RecordServer(1, 2, 3);
        instance.write2DB();
        assertEquals(1, instance.getWins());
        assertEquals(2, instance.getLoses());
        assertEquals(3, instance.getDraws());
    }
}
