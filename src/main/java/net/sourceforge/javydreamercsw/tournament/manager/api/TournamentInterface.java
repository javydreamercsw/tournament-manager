package net.sourceforge.javydreamercsw.tournament.manager.api;

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
     * @return pairings for the current round
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
     * @param player TournamentPlayerInterface to add
     */
    public void addPlayer(TournamentPlayerInterface player)
            throws TournamentSignupException;

    /**
     * Remove a player.
     *
     * @param player TournamentPlayerInterface to remove.
     * @throws TournamentSignupException
     */
    public void removePlayer(TournamentPlayerInterface player)
            throws TournamentSignupException;

    /**
     * Advance the tournament to next round.
     *
     * @throws TournamentException if there can't be a next round.
     */
    public void nextRound() throws TournamentException;

    /**
     * Amount of active players.
     *
     * @return active players
     */
    public int getAmountOfPlayers();

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
     * @param player TournamentPlayerInterface
     * @param result Encounter
     * @throws TournamentException
     */
    public void updateResults(int encounterID,
            TournamentPlayerInterface player, EncounterResult result)
            throws TournamentException;
}
