package com.github.javydreamercsw.tournament.manager.api.standing;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;

/**
 * This represents the position of a team in the standings.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class StandingSlot {

  int points;
  TeamInterface team;

  public StandingSlot(int points, TeamInterface team) {
    this.points = points;
    this.team = team;
  }
}
