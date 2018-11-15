package net.sourceforge.javydreamercsw.swiss.tournament;

import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournamentTester;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SwissTest extends AbstractTournamentTester {

    public SwissTest() {
        super(new Swiss());
    }

    @Override
    public TournamentInterface generateRandomTournament() {
        return new Swiss();
    }

  @Override
  public void testTournament()
  {
    //TODO
  }
}
