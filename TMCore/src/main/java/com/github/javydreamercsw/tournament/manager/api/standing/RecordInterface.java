package com.github.javydreamercsw.tournament.manager.api.standing;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface RecordInterface {

    /**
     * Update record to add a draw.
     */
    void draw();

    /**
     * Get amount of draws.
     *
     * @return amount of draws
     */
    int getDraws();

    /**
     * Set amount of draws.
     *
     * @param draws
     */
    void setDraws(int draws);

    /**
     * Get amount of losses.
     *
     * @return amount of losses
     */
    int getLosses();

    /**
     * Set amount of losses.
     *
     * @param losses
     */
    void setLosses(int losses);

    /**
     * Get amount of wins.
     *
     * @return amount winds
     */
    int getWins();

    /**
     * Set amount of wins.
     *
     * @param wins
     */
    void setWins(int wins);

    /**
     * Update record to add a loss.
     */
    void loss();

    /**
     * Update record to add a win.
     */
    void win();

    /**
     * Create a new instance.
     *
     * @return new instance
     */
    RecordInterface getNewInstance();
}
