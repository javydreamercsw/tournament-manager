package com.github.javydreamercsw.database.storage.db.server;

import com.github.javydreamercsw.database.storage.db.server.TournamentHasTeamServer;
import com.github.javydreamercsw.database.storage.db.server.PlayerServer;
import com.github.javydreamercsw.database.storage.db.server.TournamentServer;
import com.github.javydreamercsw.database.storage.db.server.TeamServer;

import java.util.ArrayList;
import java.util.List;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;

import com.github.javydreamercsw.database.storage.db.TestHelper;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentHasTeamServerTest extends AbstractServerTest {

    public TournamentHasTeamServerTest() {
    }

    /**
     * Test of write2DB method, of class TournamentHasTeamServer.
     */
    @Test
    public void testWrite2DB() {
        System.out.println("write2DB");
        TournamentServer tm = TestHelper.createTournament("Test");
        tm.write2DB();
        PlayerServer p1 = TestHelper.createPlayer("Player 1");
        p1.write2DB();
        PlayerServer p2 = TestHelper.createPlayer("Player 2");
        p2.write2DB();
        List<Player> players = new ArrayList<>();
        players.add(p2.getEntity());
        players.add(p1.getEntity());
        TeamServer ts = TestHelper.createTeam("Test Team", players);
        ts.write2DB();
        TournamentHasTeamServer instance
                = new TournamentHasTeamServer(tm.getEntity(), ts.getEntity());
        assertTrue(instance.write2DB() > 0);
    }
}
