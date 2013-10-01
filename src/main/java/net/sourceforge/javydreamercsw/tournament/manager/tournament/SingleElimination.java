package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournament;
import net.sourceforge.javydreamercsw.tournament.manager.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = TournamentInterface.class)
public class SingleElimination extends AbstractTournament implements TournamentInterface {

    public String getName() {
        return "Single Elimination";
    }

    public Map<Integer, Encounter> getPairings() {
        if (pairingHistory.get(getRound()) == null) {
            //Remove teams with loses from tournament
            for (Player p : players) {
                if (p.getLosses() > 0) {
                    try {
                        removePlayer(p);
                    } catch (TournamentSignupException ex) {
                        Logger.getLogger(SingleElimination.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            Map<Integer, Encounter> pairings = new HashMap<Integer, Encounter>();
            int[] exclude = new int[]{};
            Random rnd = new Random();
            while (exclude.length < players.size()) {
                int player1 = getRandomWithExclusion(rnd, 0, players.size() - 1, exclude);
                exclude = ArrayUtils.add(exclude, player1);
                if (exclude.length == players.size()) {
                    //Only one player left, pair with Bye
                    pairings.put(encounterCount, new Encounter(players.get(player1), bye));
                } else {
                    int player2 = getRandomWithExclusion(rnd, 0, players.size() - 1, exclude);
                    pairings.put(encounterCount, new Encounter(players.get(player1), players.get(player2)));
                    exclude = ArrayUtils.add(exclude, player2);
                }
                encounterCount++;
            }
            pairingHistory.put(getRound(), pairings);
        }
        return pairingHistory.get(getRound());
    }

    public void showPairings() {
        Map<Integer, Encounter> pairings = getPairings();
        for (Entry<Integer, Encounter> entry : pairings.entrySet()) {
            System.out.println(entry.getValue().getTeams().get(0).toString()
                    + " vs. " + entry.getValue().getTeams().get(1).toString());
        }
    }
}
