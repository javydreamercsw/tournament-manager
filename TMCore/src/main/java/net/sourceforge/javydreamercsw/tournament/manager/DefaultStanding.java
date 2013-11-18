package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.StandingInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.StandingSlot;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageInterface;
import org.openide.util.Lookup;

public class DefaultStanding implements StandingInterface {

    @Override
    public List<StandingSlot> getSlots(int tier) {
        List<StandingSlot> slots = new ArrayList<>();
        StorageInterface storage
                = Lookup.getDefault().lookup(StorageInterface.class);

        return slots;
    }

    @Override
    public String getName() {
        return "Normal";
    }
}
