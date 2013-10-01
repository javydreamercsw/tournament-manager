/*
 * Abstract tournament implementation.
 */
package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;

public abstract class AbstractTournament implements TournamentInterface {

    protected int encounterCount = 0;
    protected int round = 0;
    protected List<Player> players = new ArrayList<Player>();
    protected final Player bye = new Player("BYE");
    protected final Map<Integer, Map<Integer, Encounter>> pairingHistory
            = new LinkedHashMap<Integer, Map<Integer, Encounter>>();
    private final static Logger LOG
            = Logger.getLogger(AbstractTournament.class.getSimpleName());

    public int getRound() {
        return round;
    }

    public void addPlayer(Player player) throws TournamentSignupException {
        if (players.contains(player)) {
            throw new TournamentSignupException("Player already signed: "
                    + player.get(Variables.PLAYER_NAME.getDisplayName()));
        } else {
            //Loop thru players to check for name
            boolean found = false;
            for (Player p : players) {
                if (player.get(Variables.PLAYER_NAME.getDisplayName())
                        .equals(p.get(Variables.PLAYER_NAME.getDisplayName()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                players.add(player);
            }
        }
    }

    public void removePlayer(Player player) throws TournamentSignupException {
        //Loop thru players to check for name
        boolean found = false;
        for (Player p : players) {
            if (player.get(Variables.PLAYER_NAME.getDisplayName()).
                    equals(p.get(Variables.PLAYER_NAME.getDisplayName()))) {
                players.remove(p);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new TournamentSignupException(
                    "Unable to remove player. Player not found: "
                    + player.get(Variables.PLAYER_NAME.getDisplayName()));
        }
    }

    /**
     * Pick a random number from start to end, excluding the specified numbers.
     *
     * @param rnd Random
     * @param start start number
     * @param end end number
     * @param exclude numbers to exclude.
     * @return random number.
     */
    public int getRandomWithExclusion(Random rnd, int start, int end, int[] exclude) {
        //Make sure exclude is sorted
        if (exclude != null) {
            Arrays.sort(exclude);
        }
        LOG.log(Level.FINE, "Start: {0}, End: {1}, Exclude: {2}",
                new Object[]{start, end,
                    exclude == null ? "null" : exclude.length});
        int random = start + rnd.nextInt(end - start + 1
                - (exclude == null ? 0 : exclude.length));
        if (exclude != null) {
            for (int ex : exclude) {
                if (random < ex) {
                    break;
                }
                random++;
            }
        }
        return random;
    }

    public void nextRound() throws TournamentException {
        //Increase round
        round++;
        //Calculate pairings
        getPairings();
    }

    public int getAmountOfPlayers() {
        return players.size();
    }

    public void updateEncounterResult(int i, int team, EncounterResult encounterResult) {
        for (Entry<Integer, Map<Integer, Encounter>> entry : pairingHistory.entrySet()) {
            if (entry.getValue().containsKey(i)) {
                //Found the round
                entry.getValue().get(i).updateResult(team, encounterResult);
            }
        }
    }
}
