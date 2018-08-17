package net.sourceforge.javydreamercsw.tournament.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EncounterTest
{

  /**
   * Test of updateResult method, of class Encounter.
   */
  @Test
  public void testUpdateResult()
  {
    try
    {
      System.out.println("updateResult");
      TeamInterface team1 = new Team(new Player("Player 1", 1));
      TeamInterface team2 = new Team(new Player("Player 2", 2));
      Encounter instance = new Encounter(1, team1, team2);
      instance.updateResult(team1, EncounterResult.WIN);
      instance.updateResult(team2, EncounterResult.LOSS);
      assertEquals(1, team1.getTeamMembers().get(0).getRecord().getWins());
      assertEquals(1, team2.getTeamMembers().get(0).getRecord().getLosses());
      instance.updateResult(team1, EncounterResult.LOSS);
      instance.updateResult(team2, EncounterResult.WIN);
      assertEquals(1, team2.getTeamMembers().get(0).getRecord().getWins());
      assertEquals(1, team1.getTeamMembers().get(0).getRecord().getLosses());
      instance.updateResult(team1, EncounterResult.DRAW);
      instance.updateResult(team2, EncounterResult.DRAW);
      assertEquals(1, team1.getTeamMembers().get(0).getRecord().getDraws());
      assertEquals(1, team2.getTeamMembers().get(0).getRecord().getDraws());
    }
    catch (TournamentException ex)
    {
      Logger.getLogger(EncounterTest.class.getName()).log(Level.SEVERE, null, ex);
      fail();
    }
  }
}
