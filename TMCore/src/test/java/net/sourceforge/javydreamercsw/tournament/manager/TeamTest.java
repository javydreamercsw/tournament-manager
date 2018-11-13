package net.sourceforge.javydreamercsw.tournament.manager;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamTest
{

  public TeamTest()
  {
  }

  @BeforeClass
  public static void setUpClass()
  {
  }

  @AfterClass
  public static void tearDownClass()
  {
  }

  @Before
  public void setUp()
  {
  }

  @After
  public void tearDown()
  {
  }

  /**
   * Test of getTeamMembers method, of class Team.
   */
  @Test
  public void testGetTeamMembers()
  {
    System.out.println("getTeamMembers");
    Team instance = new Team(new UIPlayer("Test", 1));
    List<TournamentPlayerInterface> result = instance.getTeamMembers();
    assertEquals(1, result.size());
    instance = new Team(Arrays.asList(new UIPlayer("Test", 1),
            new UIPlayer("Player 2", 2)));
    result = instance.getTeamMembers();
    assertEquals(2, result.size());
  }
}
