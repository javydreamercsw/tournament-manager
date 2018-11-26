package com.github.javydreamercsw.database.storage.db.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamServiceTest extends AbstractServerTest
{

  public TeamServiceTest()
  {
  }

  /**
   * Test of write2DB method, of class TeamServer.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testTeamService() throws Exception
  {
    System.out.println("write2DB");
    List<Player> players = new ArrayList<>();
    for (int i = 1; i < 3; i++)
    {
      Player p = new Player("Test " + i);
      PlayerService.getInstance().savePlayer(p);
      players.add(p);
    }
    Team team = new Team("Test Team");
    TeamService.getInstance().saveTeam(team);
    TeamService.getInstance().addMembers(team, players);
    assertEquals(team.getPlayerList().size(), 2);
  }
}
