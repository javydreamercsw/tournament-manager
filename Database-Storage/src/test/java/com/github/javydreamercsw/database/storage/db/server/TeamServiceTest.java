package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamServiceTest extends AbstractServerTest
{
  @BeforeClass
  @Override
  public void setup()
  {
    try
    {
      super.setup();
      Player p1 = new Player("Player 1");
      PlayerService.getInstance().savePlayer(p1);
      Player p2 = new Player("Player 2");
      PlayerService.getInstance().savePlayer(p2);
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
  }

  /**
   * Test of findTeams method, of class TeamService.
   */
  @Test
  public void testFindTeams()
  {
    assertEquals(TeamService.getInstance().findTeams("Player 1").size(), 1);
    assertEquals(TeamService.getInstance().findTeams("Player 3").size(), 0);
    assertEquals(TeamService.getInstance().findTeams("").size(), 2);
  }

  /**
   * Test of findTeam method, of class TeamService.
   */
  @Test
  public void testFindTeam()
  {
    assertNotNull(TeamService.getInstance().findTeam(TeamService.getInstance()
            .findTeams("Player 1").get(0).getId()));
  }

  /**
   * Test of saveTeam method, of class TeamService.
   */
  @Test
  public void testSaveTeam()
  {
    Team team = new Team();
    team.setName("The Players");
    TeamService.getInstance().saveTeam(team);
    assertNotNull(team.getId());

    assertEquals(TeamService.getInstance().findTeam(team.getId())
            .getPlayerList().size(), 0);
    List<Player> players = new ArrayList<>();
    
    players.add(PlayerService.getInstance().findPlayerByName("Player 1").get());
    players.add(PlayerService.getInstance().findPlayerByName("Player 2").get());

    TeamService.getInstance().addMembers(team,players);

    assertEquals(TeamService.getInstance().findTeam(team.getId())
            .getPlayerList().size(), 2);
    
    TeamService.getInstance().deleteTeam(team);
    
    assertNull(TeamService.getInstance().findTeam(team.getId()));
  }
}
