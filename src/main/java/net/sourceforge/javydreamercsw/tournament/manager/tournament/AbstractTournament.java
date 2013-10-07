/*
 * Abstract tournament implementation.
 */
package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.api.Variables;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.apache.commons.lang3.ArrayUtils;

public abstract class AbstractTournament implements TournamentInterface {

    /**
     * Encounter id
     */
    protected int encounterCount = 0;
    /**
     * Current round number
     */
    protected int round = 0;
    /**
     * Players that registered.
     */
    protected final List<TournamentPlayerInterface> players = new ArrayList<TournamentPlayerInterface>();
    /**
     * Current list of active players. This is an exact copy of players before
     * the tournament starts. After it starts, players that get eliminated or
     * drop out are no longer on this list.
     *
     * The last at the end only the winner(s) will be on the list.
     */
    protected final List<TournamentPlayerInterface> playersCopy = new ArrayList<TournamentPlayerInterface>();
    /**
     * Default BYE player.
     */
    protected final Player bye = new Player("BYE");
    /**
     * History of the pairings for the tournament.
     */
    protected final Map<Integer, Map<Integer, Encounter>> pairingHistory
            = new LinkedHashMap<Integer, Map<Integer, Encounter>>();
    private final static Logger LOG
            = Logger.getLogger(AbstractTournament.class.getSimpleName());

    public int getRound() {
        return round;
    }

    public void addPlayer(TournamentPlayerInterface player) throws TournamentSignupException {
        if (playersCopy.contains(player)) {
            throw new TournamentSignupException("TournamentPlayerInterface already signed: "
                    + player.get(Variables.PLAYER_NAME.getDisplayName()));
        } else {
            //Loop thru playersCopy to check for name
            boolean found = false;
            for (TournamentPlayerInterface p : playersCopy) {
                if (player.get(Variables.PLAYER_NAME.getDisplayName())
                        .equals(p.get(Variables.PLAYER_NAME.getDisplayName()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                playersCopy.add(player);
                players.add(player);
            }
        }
    }

    public void removePlayer(TournamentPlayerInterface player) throws TournamentSignupException {
        //Loop thru playersCopy to check for name
        boolean found = false;
        for (TournamentPlayerInterface p : playersCopy) {
            if (player.get(Variables.PLAYER_NAME.getDisplayName()).
                    equals(p.get(Variables.PLAYER_NAME.getDisplayName()))) {
                playersCopy.remove(p);
                //If we haven't started remove it from the overall list as well.
                if (round == 0) {
                    players.remove(p);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            throw new TournamentSignupException(
                    "Unable to remove player. TournamentPlayerInterface not found: "
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
        int range = end - start + 1
                - (exclude == null ? 0 : exclude.length);
        assert range > 0;
        int random = start + rnd.nextInt(range);
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
        return playersCopy.size();
    }

    public boolean roundComplete() {
        boolean result = true;
        //Check that all encounters have a set value.
        for (Encounter e : getPairings().values()) {
            for (Map.Entry<Team, EncounterResult> entry
                    : e.getEncounterSummary().entrySet()) {
                if (entry.getValue().equals(EncounterResult.UNDECIDED)) {
                    LOG.log(Level.FINE,
                            "{0} is still not resolved!", e.toString());
                    result = false;
                }
            }
        }
        return result;
    }

    public Map<Integer, List<TournamentPlayerInterface>> getRankings() {
        Map<Integer, List<TournamentPlayerInterface>> rankings
                = new TreeMap<Integer, List<TournamentPlayerInterface>>(new Comparator<Integer>() {

                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                });
        for (TournamentPlayerInterface player : players) {
            int points = getPoints(player);
            if (rankings.get(points) == null) {
                List<TournamentPlayerInterface> list
                        = new ArrayList<TournamentPlayerInterface>();
                list.add(player);
                rankings.put(points, list);
            } else {
                rankings.get(points).add(player);
            }
        }
        return rankings;
    }

    public void displayRankings() {
        int i = 1;
        for (Entry<Integer, List<TournamentPlayerInterface>> entry : getRankings().entrySet()) {
            List<TournamentPlayerInterface> tied = entry.getValue();
            String value;
            StringBuilder sb = new StringBuilder();
            if (tied.size() > 3) {
                value = tied.size() + " tied";
            } else {
                for (TournamentPlayerInterface p : tied) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(p.toString());
                }
                value = sb.toString();
            }
            System.out.println(i + ". " + "(" + entry.getKey() + ") " + value);
            i++;
        }
    }

    @Override
    public Map<Integer, Encounter> getPairings() {
        synchronized (playersCopy) {
            if (pairingHistory.get(getRound()) == null) {
                Map<Integer, Encounter> pairings
                        = new HashMap<Integer, Encounter>();
                int[] exclude = new int[]{};
                Random rnd = new Random();
                while (exclude.length < playersCopy.size() && playersCopy.size() > 1) {
                    int player1
                            = getRandomWithExclusion(rnd, 0,
                                    playersCopy.size() - 1, exclude);
                    exclude = ArrayUtils.add(exclude, player1);
                    if (exclude.length == playersCopy.size()) {
                        //Only one player left, pair with Bye
                        LOG.log(Level.FINE, "Pairing {0} vs. BYE",
                                playersCopy.get(player1).getName());
                        pairings.put(encounterCount,
                                new Encounter(encounterCount,
                                        playersCopy.get(player1), bye));
                        try {
                            //Assign the win already, BYE always losses
                            pairings.get(encounterCount)
                                    .updateResult(playersCopy.get(player1),
                                            EncounterResult.WIN);
                        } catch (TournamentException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    } else {
                        int player2 = getRandomWithExclusion(rnd, 0,
                                playersCopy.size() - 1, exclude);
                        pairings.put(encounterCount,
                                new Encounter(encounterCount, playersCopy.get(player1),
                                        playersCopy.get(player2)));
                        exclude = ArrayUtils.add(exclude, player2);
                    }
                    encounterCount++;
                }
                pairingHistory.put(getRound(), pairings);
            }
        }
        return pairingHistory.get(getRound());
    }
}
