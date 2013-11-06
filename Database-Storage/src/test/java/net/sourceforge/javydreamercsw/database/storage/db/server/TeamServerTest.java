package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.TestHelper;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamServerTest extends AbstractServerTest {

    public TeamServerTest() {
    }

    /**
     * Test of write2DB method, of class TeamServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        List<Player> players = new ArrayList<>();
        for (int i = 1; i < 3; i++) {
            PlayerServer temp = TestHelper.createPlayer("Test " + i);
            temp.write2DB();
            players.add(temp.getEntity());
        }
        TeamServer instance = new TeamServer("Test", players);
        assertTrue(instance.write2DB() > 0);
    }
}
