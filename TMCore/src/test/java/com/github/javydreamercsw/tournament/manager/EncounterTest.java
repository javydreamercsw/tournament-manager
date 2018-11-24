package com.github.javydreamercsw.tournament.manager;

import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.Team;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;

import com.github.javydreamercsw.tournament.manager.api.Encounter;
import com.github.javydreamercsw.tournament.manager.api.EncounterResult;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;

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
      TeamInterface team1 = new Team(new UIPlayer("Player 1", 1));
      TeamInterface team2 = new Team(new UIPlayer("Player 2", 2));
      Encounter instance = new Encounter(1, 1, team1, team2);
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
