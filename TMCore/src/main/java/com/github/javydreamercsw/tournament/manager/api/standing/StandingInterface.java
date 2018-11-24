package com.github.javydreamercsw.tournament.manager.api.standing;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface StandingInterface {

    /**
     * Get standings
     *
     * @param tier standing tier.
     * @return standing slots for the specified tier
     */
    public List<StandingSlot> getSlots(int tier);

    /**
     * Standing name.
     *
     * @return name
     */
    public String getName();
}
