package com.github.javydreamercsw.database.storage.db.server;

import com.github.javydreamercsw.database.storage.db.server.PlayerServer;
import com.github.javydreamercsw.database.storage.db.server.TeamServer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;

import com.github.javydreamercsw.database.storage.db.TestHelper;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServerTest extends AbstractServerTest
{

  public PlayerServerTest()
  {
  }

  /**
   * Test of write2DB method, of class PlayerServer.
   */
  @Test
  public void testWrite2DB()
  {
    System.out.println("write2DB");
    PlayerServer instance = TestHelper.createPlayer("Test");
    assertTrue(instance.write2DB() > 0);
    assertEquals(0, instance.getTeamList().size());
    assertEquals(1, instance.getRecordList().size());
    List<Player> players = new ArrayList<>();
    players.add(new PlayerServer(new Player("Test 1")).getEntity());
    players.add(new PlayerServer(new Player("Test 2")).getEntity());
    TeamServer team = new TeamServer("Test", players);
    team.write2DB();
    instance.getTeamList().add(team.getEntity());
    instance.write2DB();
    assertEquals(1, instance.getTeamList().size());
  }
}
