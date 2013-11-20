package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
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
        PlayerServer instance = TestHelper.createPlayer("Test");
        assertTrue(instance.write2DB() > 0);
        assertEquals(0, instance.getRecordList().size());
        assertEquals(0, instance.getTeamList().size());
        instance.getRecordList().add(new RecordServer(0, 0, 0).getEntity());
        instance.write2DB();
        assertEquals(1, instance.getRecordList().size());
        List<Player> players = new ArrayList<>();
        players.add(new PlayerServer(new net.sourceforge.javydreamercsw.tournament.manager.Player("Test 1")).getEntity());
        players.add(new PlayerServer(new net.sourceforge.javydreamercsw.tournament.manager.Player("Test 2")).getEntity());
        instance.getTeamList().add(new TeamServer("Test", players).getEntity());
        instance.write2DB();
        assertEquals(1, instance.getTeamList().size());
    }
}
