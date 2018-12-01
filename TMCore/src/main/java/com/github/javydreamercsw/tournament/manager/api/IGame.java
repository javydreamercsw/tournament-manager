/*
 * This represents a game to be used by tournament manager.
 */
package com.github.javydreamercsw.tournament.manager.api;

import java.util.List;

public interface IGame
{
  /**
   * Get game's name.
   *
   * @return IGame's name
   */
  String getName();

  /**
   * Get a list of formats for this game.
   *
   * @return IGame formats.
   */
  List<GameFormat> gameFormats();
}
