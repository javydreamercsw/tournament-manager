package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentServerTest extends AbstractServerTest {

    public TournamentServerTest() {
    }

    /**
     * Test of write2DB method, of class TournamentServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        TournamentServer instance = new TournamentServer("Test");
        assertTrue(instance.write2DB() > 0);
        assertEquals(0, instance.getRoundList().size());
        assertEquals(0, instance.getTournamentHasTeamList().size());
    }
}
