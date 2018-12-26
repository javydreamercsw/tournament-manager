package com.github.javydreamercsw.tournament.manager.api.standing;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RankingProvider
{
  /**
   * Add teams into the rankings.
   *
   * @param teams Teams participating on the match.
   * @throws java.lang.Exception
   */
  void addTeam(TeamInterface... teams) throws Exception;
}
