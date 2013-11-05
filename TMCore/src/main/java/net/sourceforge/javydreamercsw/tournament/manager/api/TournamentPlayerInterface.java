package net.sourceforge.javydreamercsw.tournament.manager.api;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentPlayerInterface {

    /**
     * Update record to add a draw.
     */
    void draw();

    /**
     * Get the value for the specified key.
     *
     * @param key Key to look for
     * @return value for the key provided or null if not found.
     */
    Object get(String key);

    /**
     * Get amount of draws.
     *
     * @return amount of draws
     */
    int getDraws();

    /**
     * Get amount of losses.
     *
     * @return amount of losses
     */
    int getLosses();

    /**
     * Get player name.
     *
     * @return player name
     */
    String getName();

    /**
     * Get amount of wins.
     *
     * @return amount winds
     */
    int getWins();

    /**
     * Update record to add a loss.
     */
    void loss();

    /**
     * Update record to add a win.
     */
    void win();

    /**
     * Get the player ID.
     *
     * @return player ID
     */
    int getID();
}
