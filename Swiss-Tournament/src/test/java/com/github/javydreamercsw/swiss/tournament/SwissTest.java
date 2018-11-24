package com.github.javydreamercsw.swiss.tournament;

import com.github.javydreamercsw.tournament.manager.AbstractTournamentTester;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SwissTest extends AbstractTournamentTester
{

  @Override
  public TournamentInterface generateRandomTournament()
  {
    return new Swiss();
  }

  @Override
  public void testTournament()
  {
    //TODO
  }
}
