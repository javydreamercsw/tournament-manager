package net.sourceforge.javydreamercsw.database.storage.db.server;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.database.storage.db.TestHelper;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchServerTest extends AbstractServerTest {

    public MatchServerTest() {
    }

    /**
     * Test of write2DB method, of class MatchServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        TournamentServer tm = TestHelper.createTournament("Test 1");
        tm.write2DB();
        RoundServer r = TestHelper.createRound(tm.getEntity());
        r.write2DB();
        MatchServer instance = new MatchServer(r.getEntity());
        assertTrue(instance.write2DB() > 0);
    }
}
