package net.sourceforge.javydreamercsw.tournament.manager.api;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;

/**
 * This is the interface for the tournament.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentInterface {

    /**
     * Default BYE player.
     */
    public final TeamInterface bye = new Team(new Player("BYE"));

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
     * Add a no show listener.
     *
     * @param nsl no show listener
     */
    public void addNoShowListener(NoShowListener nsl);

    /**
     * Remove a no show listener.
     *
     * @param nsl no show listener
     */
    public void removeNoShowListener(NoShowListener nsl);

    /**
     * Add a round time listener.
     *
     * @param rtl round time listener
     */
    public void addRoundTimeListener(RoundTimeListener rtl);

    /**
     * Remove a round time listener.
     *
     * @param rtl round time listener
     */
    public void removeRoundTimeListener(RoundTimeListener rtl);

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
}
