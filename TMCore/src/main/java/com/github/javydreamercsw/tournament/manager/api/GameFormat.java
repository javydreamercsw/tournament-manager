/*
 * This represents a game format.
 */
package com.github.javydreamercsw.tournament.manager.api;

public interface GameFormat
{
  /**
   * Get format's name.
   *
   * @return Format's name
   */
  public String getName();

  /**
   * Get format's description.
   *
   * @return Format's description.
   */
  public String getDescription();
}
