package com.github.javydreamercsw.tournament.manager.elimination.tournament;

import com.github.javydreamercsw.tournament.manager.AbstractTournamentTester;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EliminationTest extends AbstractTournamentTester {

  private static final Logger LOG = Logger.getLogger(EliminationTest.class.getName());

  @Override
  public TournamentInterface generateRandomTournament() {
    int eliminations = new Random().nextInt(2) + 1;
    LOG.log(Level.INFO, "Eliminations: {0}", eliminations);
    return new Elimination(eliminations, 3, 0, 1, new Random().nextBoolean()) {
      @Override
      public String getName() {
        return "Elimination: " + eliminations;
      }

      @Override
      public TournamentInterface createTournament(
          List<TeamInterface> teams, int winPoints, int lossPoints, int drawPoints) {
        return null;
      }
    };
  }

  @Override
  public void testTournament() {
    // TODO
  }
}
