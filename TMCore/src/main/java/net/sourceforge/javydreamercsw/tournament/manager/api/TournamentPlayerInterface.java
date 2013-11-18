package net.sourceforge.javydreamercsw.tournament.manager.api;

import net.sourceforge.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentPlayerInterface {

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
     * @param wins
     * @param loses
     * @param draws
     * @return instance
     */
    TournamentPlayerInterface createInstance(String name, int wins,
            int loses, int draws);
}
