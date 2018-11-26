package com.github.javydreamercsw.database.storage.db.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServiceTest extends AbstractServerTest
{

  public PlayerServiceTest()
  {
  }

  /**
   * Test of write2DB method, of class PlayerServer.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testPlayerService() throws Exception
  {
    System.out.println("write2DB");
    Player player = new Player("Test");
    PlayerService.getInstance().savePlayer(player);

    assertTrue(player.getId() > 0);
    assertEquals(0, player.getTeamList().size());
    assertEquals(1, player.getRecordList().size());

    Player player2 = new Player("Test 2");
    PlayerService.getInstance().savePlayer(player2);

    Team team = new Team("Test Team");
    TeamService.getInstance().saveTeam(team);
    TeamService.getInstance().addMembers(team, player, player2);

    assertEquals(1, player.getTeamList().size());
    assertEquals(1, player2.getTeamList().size());
  }
}
