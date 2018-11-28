package com.github.javydreamercsw.database.storage.db.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;

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
