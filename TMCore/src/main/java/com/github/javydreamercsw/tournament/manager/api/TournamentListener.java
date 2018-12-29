package com.github.javydreamercsw.tournament.manager.api;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentListener
{
  /**
   * Round started!
   *
   * @param round Round number started.
   */
  public void roundStart(int round);

  /**
   * Round time is up!
   */
  public void roundTimeOver();

  /**
   * Round ended.
   * @param round Round number ended.
   */
  public void roundOver(int round);

  /**
   * No show time is up!
   */
  public void noshow();
}
