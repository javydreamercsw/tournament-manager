package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.javydreamercsw.tournament.manager.api.standing.StandingInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.StandingSlot;

public class DefaultStanding implements StandingInterface {

    @Override
    public List<StandingSlot> getSlots(int tier) {
        List<StandingSlot> slots = new ArrayList<>();

        return slots;
    }

    @Override
    public String getName() {
        return "Normal";
    }
}
