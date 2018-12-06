package com.github.javydreamercsw.tournament.manager;

import static org.testng.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.testng.annotations.Test;

import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamTest
{

  /**
   * Test of getTeamMembers method, of class Team.
   */
  @Test
  public void testGetTeamMembers()
  {
    System.out.println("getTeamMembers");
    Team instance = new Team(1, new UIPlayer("Test", 1));
    List<TournamentPlayerInterface> result = instance.getTeamMembers();
    assertEquals(1, result.size());
    instance = new Team(2, Arrays.asList(new UIPlayer("Test", 1),
            new UIPlayer("Player 2", 2)));
    result = instance.getTeamMembers();
    assertEquals(2, result.size());
  }
}
