package net.sourceforge.javydreamercsw.tournament.manager.api;

import java.util.List;
import java.util.Map;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;

/**
 * This is the interface for the tournament.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentInterface {

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
     * Add player.
     *
     * @throws TournamentSignupException
     *
     * @param team TournamentPlayerInterface to add
     */
    public void addTeam(TeamInterface team)
            throws TournamentSignupException;

    /**
     * Remove a player.
     *
     * @param team TournamentPlayerInterface to remove.
     * @throws TournamentSignupException
     */
    public void removeTeam(TeamInterface team)
            throws TournamentSignupException;

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
    public Map<Integer, List<TeamInterface>> getRankings();

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
}
