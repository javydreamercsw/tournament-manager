package com.github.javydreamercsw.tournament.manager;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import com.github.javydreamercsw.tournament.manager.api.Encounter;
import com.github.javydreamercsw.tournament.manager.api.EncounterResult;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.testng.annotations.Test;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractTournamentTester {
  private static final Logger LOG =
      Logger.getLogger(AbstractTournamentTester.class.getSimpleName());

  /** Test the peculiarities of this tournament. */
  public abstract void testTournament();

  @Test
  public void testCustomTournament() {
    testTournament();
  }

  /** Test of getName method, of class Elimination. */
  @Test
  public void testGetName() {
    LOG.info("getName");
    assertFalse(generateRandomTournament().getName().trim().isEmpty());
  }

  /**
   * Test of getPairings method, of class Elimination.
   *
   * @throws TournamentSignupException
   */
  @Test
  public void testGetPairings() throws TournamentSignupException {
    LOG.info("getPairings");
    TournamentInterface tournament = generateRandomTournament();
    // Even entries
    LOG.info("Even amount of entries -----------------------");
    int limit = new Random().nextInt(1000) + 100;
    if (limit % 2 != 0) {
      // Not even, add one
      limit++;
    }
    for (int i = 0; i < limit; i++) {
      try {
        tournament.addTeam(new Team(i, new UIPlayer(MessageFormat.format("Player #{0}", i), i)));
      } catch (TournamentSignupException ex) {
        LOG.log(Level.SEVERE, null, ex);
        fail();
      }
    }
    Map<Integer, Encounter> result = tournament.getPairings();
    printPairings(result);
    LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
    assertTrue(result.size() > 0);
    assertEquals(tournament.getAmountOfTeams() / 2, result.size());
    Encounter e =
        result.values().toArray(new Encounter[0])[(tournament.getAmountOfTeams() / 2) - 1];
    TeamInterface t = e.getEncounterSummary().keySet().toArray(new TeamInterface[0])[0];
    try {
      LOG.log(
          Level.INFO,
          "Updating result for: {0} encounter id: {1}",
          new Object[] {t.getName(), e.getId()});
      tournament.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
    } catch (TournamentException ex) {
      // Expected failure
    }

    try {
      // update the result
      tournament.updateResults(e.getId(), t, EncounterResult.WIN);
    } catch (TournamentException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    // Make sure loosers are removed.
    try {
      tournament.nextRound();
    } catch (TournamentException ex) {
      LOG.log(Level.SEVERE, null, ex);
      fail();
    }
    // Redo with odd entries
    LOG.info("Odd amount of entries -----------------------");
    tournament = generateRandomTournament();
    limit = new Random().nextInt(1000) + 100;
    if (limit % 2 == 0) {
      // Not odd, add one
      limit++;
    }
    for (int i = 0; i < limit; i++) {
      try {
        tournament.addTeam(new Team(i, new UIPlayer(MessageFormat.format("Player #{0}", i), i)));
      } catch (TournamentSignupException ex) {
        LOG.log(Level.SEVERE, null, ex);
        fail();
      }
    }
    LOG.log(Level.INFO, "Amount of registered players: {0}", tournament.getAmountOfTeams());
    result = tournament.getPairings();
    LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
    assertEquals(tournament.getAmountOfTeams() / 2 + 1, result.size());
    e = result.values().toArray(new Encounter[] {})[tournament.getAmountOfTeams() / 2 - 1];
    t = e.getEncounterSummary().keySet().toArray(new TeamInterface[] {})[0];
    try {
      tournament.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
    } catch (TournamentException ex) {
      // Expected failure
    }
    try {
      // update the result
      tournament.updateResults(e.getId(), t, EncounterResult.WIN);
    } catch (TournamentException ex) {
      LOG.log(Level.SEVERE, null, ex);
    }
    // Make sure loosers are removed.
    try {
      tournament.nextRound();
    } catch (TournamentException ex) {
      LOG.log(Level.SEVERE, null, ex);
      fail();
    }
  }

  private void printPairings(Map<Integer, Encounter> result) {
    result
        .entrySet()
        .forEach(
            (entry) -> {
              Encounter encounter = entry.getValue();
              TeamInterface[] teams =
                  encounter.getEncounterSummary().keySet().toArray(new TeamInterface[] {});
              LOG.log(
                  Level.INFO,
                  "{0}: {1} vs. {2}",
                  new Object[] {entry.getKey(), teams[0].toString(), teams[1].toString()});
            });
  }

  /** Test of tournament simulation. */
  @Test
  public void testSimulateTournament() {
    LOG.info("Simulate tournament");
    Random random = new Random();
    for (int i = 0; i < 100; i++) {
      LOG.log(Level.INFO, "Simulation #{0}", (i + 1));
      TournamentInterface tournament = generateRandomTournament();
      int limit = random.nextInt(100);
      for (int y = 0; y < limit; y++) {
        try {
          tournament.addTeam(
              new Team(y, new UIPlayer(MessageFormat.format("Player #{0}", (y + 1)), y)));
        } catch (TournamentSignupException ex) {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      LOG.log(Level.INFO, "Amount of registered players: {0}", tournament.getAmountOfTeams());
      LOG.log(Level.INFO, "Amount of expected rounds: {0}", tournament.getMinimumAmountOfRounds());
      boolean ignore = false;
      while (tournament.getAmountOfTeams() > 1) {
        // Random player drop
        if (tournament.getActiveTeams().size() > 1 && random.nextBoolean()) {
          TeamInterface toDrop =
              tournament.getActiveTeams().get(random.nextInt(tournament.getAmountOfTeams()));
          LOG.log(Level.INFO, "Player: {0} dropped!", toDrop.toString());
          try {
            tournament.removeTeam(toDrop);
          } catch (TournamentSignupException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
          } catch (TournamentException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
          }
        }
        try {
          tournament.nextRound();
          if (tournament.getActiveTeams().size() > 1) {
            LOG.log(Level.INFO, "Round {0}", tournament.getRound());
            LOG.info("Pairings...");
            LOG.info("Simulating results...");
            for (Map.Entry<Integer, Encounter> entry : tournament.getPairings().entrySet()) {
              Encounter encounter = entry.getValue();
              TeamInterface player1 =
                  encounter.getEncounterSummary().keySet().toArray(new TeamInterface[] {})[0];
              TeamInterface player2 =
                  encounter.getEncounterSummary().keySet().toArray(new TeamInterface[] {})[1];
              // Make sure is not paired against BYE
              if (!player1.equals(TournamentInterface.BYE)
                  && !player2.equals(TournamentInterface.BYE)) {
                // Random Result
                int range = EncounterResult.values().length - 1;
                int result = random.nextInt(range);
                tournament.updateResults(
                    encounter.getId(), player1, EncounterResult.values()[result]);
              }
              if (player1.equals(TournamentInterface.BYE)
                  || player2.equals(TournamentInterface.BYE)
                      && tournament.getActiveTeams().size() == 1) {
                // Only one player left, we got a winner!
                ignore = true;
                break;
              }
            }
          }
        } catch (TournamentException ex) {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      if (!ignore) {
        tournament.showPairings();
        assertTrue(tournament.roundComplete());
      }
      if (tournament.getActiveTeams().size() == 1) {
        TeamInterface winner = tournament.getActiveTeams().get(0);
        LOG.log(Level.INFO, "Tournament winner: {0}", winner);
      } else {
        // They drew in the finals
        LOG.log(Level.INFO, "Tournament winner: None (draw)");
      }
      tournament.displayRankings();
      // To store the amount of points on each ranking spot.
      List<Integer> points = new ArrayList<>();
      tournament.getRankings().entrySet().stream()
          .filter((rankings) -> (rankings.getValue().size() > 0))
          .map(
              (rankings) -> {
                int max = -1;
                for (TeamInterface team : rankings.getValue()) {
                  // Everyone tied has same amount of points
                  assertTrue(max == -1 || tournament.getPoints(team) == max);
                  max = tournament.getPoints(team);
                }
                return max;
              })
          .forEachOrdered(
              (max) -> {
                points.add(max);
              });
      for (int x = points.size() - 1; x > 0; x--) {
        assertTrue(points.get(x) < points.get(x - 1));
      }
    }
  }

  /**
   * Allows to generate random tournament with different settings. Used during the tournament
   * simulations.
   *
   * @return
   */
  public abstract TournamentInterface generateRandomTournament();
}
