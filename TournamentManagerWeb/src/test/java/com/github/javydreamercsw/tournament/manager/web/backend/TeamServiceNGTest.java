package com.github.javydreamercsw.tournament.manager.web.backend;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;

public class TeamServiceNGTest extends BaseTestCase
{
  @BeforeClass
  @Override
  public void setup()
  {
    super.setup();
    Player p1 = new Player("Player 1");
    PlayerService.getInstance().savePlayer(p1);
    Player p2 = new Player("Player 2");
    PlayerService.getInstance().savePlayer(p2);
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
