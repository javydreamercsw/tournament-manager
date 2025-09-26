package com.github.javydreamercsw.tournament.manager.api;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentListener {
  /**
   * Round started!
   *
   * @param round Round number started.
   */
  void roundStart(int round);

  /** Round time is up! */
  void roundTimeOver();

  /**
   * Round ended.
   *
   * @param round Round number ended.
   */
  void roundOver(int round);

  /** No show time is up! */
  void noshow();
}
