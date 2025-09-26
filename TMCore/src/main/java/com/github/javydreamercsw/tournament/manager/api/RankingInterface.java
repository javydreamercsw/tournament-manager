package com.github.javydreamercsw.tournament.manager.api;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RankingInterface {

  /**
   * Get the amount of points for this team.
   *
   * @return amount of points for this team
   */
  int getPoints();

  /**
   * Get the team for this ranking.
   *
   * @return team for this ranking
   */
  TeamInterface getTeam();
}
