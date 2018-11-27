package com.github.javydreamercsw.tournament.manager.web.backend;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.openide.util.Exceptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;

public class TeamServiceNGTest extends BaseTestCase
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
    System.out.println("findTeams");
    assertEquals(TeamService.getInstance().findTeams("Player 1").size(), 1);
    assertEquals(TeamService.getInstance().findTeams("Player 3").size(), 0);
  }

  /**
   * Test of findTeam method, of class TeamService.
   */
  @Test
  public void testFindTeam()
  {
    System.out.println("findTeam");
    assertNotNull(TeamService.getInstance().findTeam(TeamService.getInstance()
            .findTeams("Player 1").get(0).getId()));
  }

  /**
   * Test of saveTeam method, of class TeamService.
   */
  @Test
  public void testSaveTeam()
  {
    System.out.println("saveTeam");
    Team team = new Team();
    team.setName("The Players");
    TeamService.getInstance().saveTeam(team);
    assertNotNull(team.getId());

    TeamService.getInstance().addMembers(team,
            PlayerService.getInstance().findPlayerByName("Player 1").get(),
            PlayerService.getInstance().findPlayerByName("Player 2").get());
  }
}
