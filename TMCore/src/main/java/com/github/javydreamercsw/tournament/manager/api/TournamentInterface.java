package com.github.javydreamercsw.tournament.manager.api;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.github.javydreamercsw.tournament.manager.Team;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;

/**
 * This is the interface for the tournament.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentInterface
{

  /**
   * Default BYE player.
   */
  public final TeamInterface BYE = new Team(-1, new UIPlayer("BYE", -1));

  /**
   * Get tournament name.
   *
   * @return tournament name
   */
  public String getName();

  /**
   * Get the pairings for the current round.
   *
   * @return pairings for the current round. Null if there's a clear winner.
   */
  public Map<Integer, Encounter> getPairings();

  /**
   * Get current round number.
   *
   * @return current round number
   */
  public int getRound();

  /**
   * Get specific round.
   *
   * @param round round to look for
   * @return round or null if not found.
   */
  public Map<Integer, Encounter> getRound(int round);

  /**
   * Get specific round.
   *
   * @param round round to look for
   * @param encounters encounters to set to this round
   */
  public void setRound(int round, Map<Integer, Encounter> encounters);

  /**
   * Add teams.
   *
   * @throws TournamentSignupException
   *
   * @param teams Teams to add
   */
  public void addTeams(List<TeamInterface> teams)
          throws TournamentSignupException;

  /**
   * Add team.
   *
   * @throws TournamentSignupException
   *
   * @param team TeamInterface to add
   */
  public void addTeam(TeamInterface team)
          throws TournamentSignupException;

  /**
   * Remove a player.
   *
   * @param team TournamentPlayerInterface to remove.
   * @throws TournamentSignupException
   * @throws TournamentException
   */
  public void removeTeam(TeamInterface team)
          throws TournamentSignupException, TournamentException;

  /**
   * Advance the tournament to next round.
   *
   * @throws TournamentException if there can't be a next round.
   */
  public void nextRound() throws TournamentException;

  /**
   * Amount of active teams.
   *
   * @return active teams
   */
  public int getAmountOfTeams();

  /**
   * Display pairings in text.
   */
  public void showPairings();

  /**
   * Status of current round.
   *
   * @return true if complete (All encounters have a result)
   */
  public boolean roundComplete();

  /**
   * Update results.
   *
   * @param encounterID encounter id to update results for
   * @param team TournamentPlayerInterface
   * @param result Encounter
   * @throws TournamentException
   */
  public void updateResults(int encounterID,
          TeamInterface team, EncounterResult result)
          throws TournamentException;

  /**
   * Get the current rankings.
   *
   * @return current rankings
   */
  public TreeMap<Integer, List<TeamInterface>> getRankings();

  /**
   * If no one drops, the amount of minimum rounds expected based on entries.
   *
   * @return amount of minimum rounds expected based on entries
   */
  public int getMinimumAmountOfRounds();

  /**
   * Amount of points in the tournament.
   *
   * @param team player to get points from.
   * @return points in the tournament
   */
  public int getPoints(TeamInterface team);

  /**
   * @return the winPoints
   */
  public int getWinPoints();

  /**
   * @return the lossPoints
   */
  public int getLossPoints();

  /**
   * @return the drawPoints
   */
  public int getDrawPoints();

  /**
   * Get the winning team.
   *
   * @return winning team
   */
  public TeamInterface getWinnerTeam();

  /**
   * Set the time allowed before no shows (milliseconds from pairings).
   *
   * @param time Date of no show.
   */
  public void setNoShowTime(long time);

  /**
   * Get the no show time.
   *
   * @return time allowed before no shows (milliseconds from pairings)
   */
  public long getNoShowTime();

  /**
   * Set the time for the round to end (milliseconds from pairings).
   *
   * @param time round end time.
   */
  public void setRoundTime(long time);

  /**
   * Get the round time.
   *
   * @return time for the round to end (milliseconds from pairings)
   */
  public long getRoundTime();

  /**
   * Add a round time listener.
   *
   * @param tl round time listener
   */
  public void addTournamentListener(TournamentListener tl);

  /**
   * Remove a round time listener.
   *
   * @param tl round time listener
   */
  public void removeTournamentListener(TournamentListener tl);

  /**
   * Get listeners.
   *
   * @return Unmodifiable list of registered listeners.
   */
  public List<TournamentListener> getListeners();

  /**
   * Check if the team has not been eliminated/dropped
   *
   * @param t team to check
   * @return true if still active
   */
  public boolean isTeamActive(TeamInterface t);

  /**
   * List of teams still active.
   *
   * @return teams still active
   */
  public List<TeamInterface> getActiveTeams();

  /**
   * Display rankings.
   */
  public void displayRankings();

  /**
   * Return the ID for this tournament.
   *
   * @return ID for this tournament
   */
  public int getId();

  /**
   * Set the ID for this tournament.
   *
   * @param id ID for this tournament
   */
  public void setId(int id);

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
  public TournamentInterface createTournament(List<TeamInterface> teams,
          int winPoints, int lossPoints, int drawPoints)
          throws TournamentSignupException;

  /**
   * Get teams.
   *
   * @return Unmodifiable list of teams in this tournament.
   */
  public List<TeamInterface> getTeams();

  /**
   * Process the round. Any eliminations and other changes to the pool of
   * players must be done here.
   *
   * @param round Round to process.
   */
  public void processRound(int round);
  
  /**
   * Get the amount of non-wins to be eliminated from the tournament.
   * @return amount of non-wins to be eliminated from the tournament
   */
  public int getEliminations();
}
