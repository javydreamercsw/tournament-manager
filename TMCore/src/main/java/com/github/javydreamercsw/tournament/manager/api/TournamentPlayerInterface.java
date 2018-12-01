package com.github.javydreamercsw.tournament.manager.api;

import de.gesundkrank.jskills.IPlayer;

import com.github.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentPlayerInterface extends IPlayer
{

  /**
   * Get the value for the specified key.
   *
   * @param key Key to look for
   * @return value for the key provided or null if not found.
   */
  Object get(String key);

  /**
   * Get player name.
   *
   * @return player name
   */
  String getName();

  /**
   * Set the player name.
   *
   * @param name New name to set.
   */
  void setName(String name);

  /**
   * Get the player ID.
   *
   * @return player ID
   */
  int getID();

  /**
   * Get the player's record.
   *
   * @return
   */
  RecordInterface getRecord();

  /**
   * Create an instance of this player interface.
   *
   * @param name player name
   * @param wins amount of wins
   * @param loses amount of loses
   * @param draws amount of draws
   * @return instance
   */
  TournamentPlayerInterface createInstance(String name, int wins,
          int loses, int draws);

  /**
   * Create an instance of this player interface.
   *
   * @param name player name
   * @param id player id
   * @param wins amount of wins
   * @param loses amount of loses
   * @param draws amount of draws
   * @return instance
   */
  TournamentPlayerInterface createInstance(String name, int id, int wins,
          int loses, int draws);

  /**
   * Create an instance of this player interface with a clean record
   *
   * @param name player name
   * @param id player id
   * @return instance
   */
  TournamentPlayerInterface createInstance(String name, int id);
}
