package net.sourceforge.javydreamercsw.tournament.manager.api.standing;

import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RankingProvider
{
  /**
   * Add a match into the rankings.
   *
   * @param title Match title.
   * @param teams Teams participating on the match.
   * @throws java.lang.Exception
   */
  void addMatch(String title, TeamInterface... teams) throws Exception;
}
