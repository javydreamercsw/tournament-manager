/*
 * This represents a game to be used by tournament manager.
 */
package com.github.javydreamercsw.tournament.manager.api;

import java.util.List;

public interface Game
{
  /**
   * Get game's name.
   *
   * @return Game's name
   */
  String getName();

  /**
   * Get a list of formats for this game.
   *
   * @return Game formats.
   */
  List<GameFormat> gameFormats();
  
  
}
