package com.github.javydreamercsw.tournament.manager.api;

import com.github.javydreamercsw.tournament.manager.Team;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is the interface for the tournament.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentInterface {

  /** Default BYE player. */
  TeamInterface BYE = new Team(-1, new UIPlayer("BYE", -1));

  /**
   * Get tournament name.
   *
   * @return tournament name
   */
  String getName();

  /**
   * Get the pairings for the current round.
   *
   * @return pairings for the current round. Null if there's a clear winner.
   */
  Map<Integer, Encounter> getPairings();

  /**
   * Get current round number.
   *
   * @return current round number
   */
  int getRound();

  /**
   * Get specific round.
   *
   * @param round round to look for
   * @return round or null if not found.
   */
  Map<Integer, Encounter> getRound(int round);

  /**
   * Get specific round.
   *
   * @param round round to look for
   * @param encounters encounters to set to this round
   */
  void setRound(int round, Map<Integer, Encounter> encounters);

  /**
   * Add teams.
   *
   * @throws TournamentSignupException
   * @param teams Teams to add
   */
  void addTeams(List<TeamInterface> teams) throws TournamentSignupException;

  /**
   * Add team.
   *
   * @throws TournamentSignupException
   * @param team TeamInterface to add
   */
  void addTeam(TeamInterface team) throws TournamentSignupException;

  /**
   * Remove a player.
   *
   * @param team TournamentPlayerInterface to remove.
   * @throws TournamentSignupException
   * @throws TournamentException
   */
  void removeTeam(TeamInterface team) throws TournamentSignupException, TournamentException;

  /**
   * Advance the tournament to next round.
   *
   * @throws TournamentException if there can't be a next round.
   */
  void nextRound() throws TournamentException;

  /**
   * Amount of active teams.
   *
   * @return active teams
   */
  int getAmountOfTeams();

  /** Display pairings in text. */
  void showPairings();

  /**
   * Status of current round.
   *
   * @return true if complete (All encounters have a result)
   */
  boolean roundComplete();

  /**
   * Update results.
   *
   * @param encounterID encounter id to update results for
   * @param team TournamentPlayerInterface
   * @param result Encounter
   * @throws TournamentException
   */
  void updateResults(int encounterID, TeamInterface team, EncounterResult result)
      throws TournamentException;

  /**
   * Get the current rankings.
   *
   * @return current rankings
   */
  TreeMap<Integer, List<TeamInterface>> getRankings();

  /**
   * If no one drops, the amount of minimum rounds expected based on entries.
   *
   * @return amount of minimum rounds expected based on entries
   */
  int getMinimumAmountOfRounds();

  /**
   * Amount of points in the tournament.
   *
   * @param team player to get points from.
   * @return points in the tournament
   */
  int getPoints(TeamInterface team);

  /**
   * @return the winPoints
   */
  int getWinPoints();

  /**
   * @return the lossPoints
   */
  int getLossPoints();

  /**
   * @return the drawPoints
   */
  int getDrawPoints();

  /**
   * Get the winning team.
   *
   * @return winning team
   */
  TeamInterface getWinnerTeam();

  /**
   * Set the time allowed before no shows (milliseconds from pairings).
   *
   * @param time Date of no show.
   */
  void setNoShowTime(long time);

  /**
   * Get the no show time.
   *
   * @return time allowed before no shows (milliseconds from pairings)
   */
  long getNoShowTime();

  /**
   * Set the time for the round to end (milliseconds from pairings).
   *
   * @param time round end time.
   */
  void setRoundTime(long time);

  /**
   * Get the round time.
   *
   * @return time for the round to end (milliseconds from pairings)
   */
  long getRoundTime();

  /**
   * Add a round time listener.
   *
   * @param tl round time listener
   */
  void addTournamentListener(TournamentListener tl);

  /**
   * Remove a round time listener.
   *
   * @param tl round time listener
   */
  void removeTournamentListener(TournamentListener tl);

  /**
   * Get listeners.
   *
   * @return Unmodifiable list of registered listeners.
   */
  List<TournamentListener> getListeners();

  /**
   * Check if the team has not been eliminated/dropped
   *
   * @param t team to check
   * @return true if still active
   */
  boolean isTeamActive(TeamInterface t);

  /**
   * List of teams still active.
   *
   * @return teams still active
   */
  List<TeamInterface> getActiveTeams();

  /** Display rankings. */
  void displayRankings();

  /**
   * Return the ID for this tournament.
   *
   * @return ID for this tournament
   */
  int getId();

  /**
   * Set the ID for this tournament.
   *
   * @param id ID for this tournament
   */
  void setId(int id);

  /**
   * Create a tournament.
   *
   * @param teams Teams to add
   * @param winPoints Points per win
   * @param lossPoints Poinst per loss
   * @param drawPoints Points per draw
   * @return Created tournament
   * @throws TournamentSignupException if something goes wrong adding players.
   */
  TournamentInterface createTournament(
      List<TeamInterface> teams, int winPoints, int lossPoints, int drawPoints)
      throws TournamentSignupException;

  /**
   * Get teams.
   *
   * @return Unmodifiable list of teams in this tournament.
   */
  List<TeamInterface> getTeams();

  /**
   * Process the round. Any eliminations and other changes to the pool of players must be done here.
   *
   * @param round Round to process.
   */
  void processRound(int round);

  /**
   * Get the amount of non-wins to be eliminated from the tournament.
   *
   * @return amount of non-wins to be eliminated from the tournament
   */
  int getEliminations();
}
