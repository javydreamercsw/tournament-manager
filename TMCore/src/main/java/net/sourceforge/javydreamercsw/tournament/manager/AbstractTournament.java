/*
 * Abstract tournament implementation.
 */
package net.sourceforge.javydreamercsw.tournament.manager;

import java.text.MessageFormat;
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
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
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
     * Teams that registered.
     */
    protected final List<TeamInterface> teams = new ArrayList<>();
    /**
     * Current list of active teams. This is an exact copy of teams before the
     * tournament starts. After it starts, teams that get eliminated or drop out
     * are no longer on this list.
     *
     * The last at the end only the winner(s) will be on the list.
     */
    private final List<TeamInterface> teamsCopy = new ArrayList<>();
    /**
     * Default BYE player.
     */
    public final TeamInterface bye = new Team(new Player("BYE"));
    /**
     * History of the pairings for the tournament.
     */
    protected final Map<Integer, Map<Integer, Encounter>> pairingHistory
            = new LinkedHashMap<>();
    private final static Logger LOG
            = Logger.getLogger(AbstractTournament.class.getSimpleName());
    
       /**
     * Amount of points for a win.
     */
    private final int winPoints;

    /**
     * Amount of points for a loss.
     */
    private final int lossPoints;

    /**
     * Amount of points for a draw.
     */
    private final int drawPoints;

    public AbstractTournament(int winPoints, int lossPoints, int drawPoints) {
        this.winPoints = winPoints;
        this.lossPoints = lossPoints;
        this.drawPoints = drawPoints;
    }

    @Override
    public int getRound() {
        return round;
    }

    @Override
    public void addTeam(TeamInterface team) throws TournamentSignupException {
        //Loop thru teamsCopy to check for name
        boolean found = false;
        for (TeamInterface t : getTeamsCopy()) {
            for (TournamentPlayerInterface tpi : t.getTeamMembers()) {
                for (TournamentPlayerInterface newtpi : team.getTeamMembers()) {
                    if (tpi.get(Variables.PLAYER_NAME.getDisplayName())
                            .equals(newtpi.get(Variables.PLAYER_NAME.getDisplayName()))) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (!found) {
            getTeamsCopy().add(team);
            teams.add(team);
        } else {
            throw new TournamentSignupException(
                    MessageFormat.format(
                            "Team already signed: {0}",
                            team));
        }
    }

    @Override
    public void removeTeam(TeamInterface team) throws TournamentSignupException {
        //Loop thru teamsCopy to check for name
        boolean found = false;
        List<TeamInterface> toRemove = new ArrayList<>();
        for (TeamInterface existingTeam : getTeamsCopy()) {
            for (TournamentPlayerInterface player : existingTeam.getTeamMembers()) {
                for (TournamentPlayerInterface newPlayer : team.getTeamMembers()) {
                    if (player.getName().equals(newPlayer.getName())) {
                        toRemove.add(existingTeam);
                        found = true;
                        break;
                    }
                }
            }
        }
        for (TeamInterface remove : toRemove) {
            getTeamsCopy().remove(remove);
            //If we haven't started remove it from the overall list as well.
            if (round == 0) {
                teams.remove(remove);
            }
        }
        if (!found) {
            throw new TournamentSignupException(
                    MessageFormat.format(
                            "Unable to remove Team. Team not found: {0}", team));
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

    @Override
    public void nextRound() throws TournamentException {
        //Increase round
        round++;
        //Calculate pairings
        getPairings();
    }

    @Override
    public int getAmountOfTeams() {
        return getTeamsCopy().size();
    }

    @Override
    public boolean roundComplete() {
        boolean result = true;
        //Check that all encounters have a set value.
        for (Encounter e : getPairings().values()) {
            for (Map.Entry<TeamInterface, EncounterResult> entry
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

    @Override
    public Map<Integer, List<TeamInterface>> getRankings() {
        Map<Integer, List<TeamInterface>> rankings
                = new TreeMap<>(new Comparator<Integer>() {

                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return o2.compareTo(o1);
                    }
                });
        for (TeamInterface player : teams) {
            int points = getPoints(player);
            if (rankings.get(points) == null) {
                List<TeamInterface> list = new ArrayList<>();
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
        for (Entry<Integer, List<TeamInterface>> entry : getRankings().entrySet()) {
            List<TeamInterface> tied = entry.getValue();
            String value;
            StringBuilder sb = new StringBuilder();
            if (tied.size() > 3) {
                value = MessageFormat.format("{0} tied", tied.size());
            } else {
                for (TeamInterface t : tied) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(t.toString());
                }
                value = sb.toString();
            }
            System.out.println(MessageFormat.format("{0}. ({1}) {2}", i,
                    entry.getKey(), value));
            i++;
        }
    }

    @Override
    public Map<Integer, Encounter> getPairings() {
        synchronized (getTeamsCopy()) {
            if (pairingHistory.get(getRound()) == null) {
                Map<Integer, Encounter> pairings
                        = new HashMap<>();
                int[] exclude = new int[]{};
                Random rnd = new Random();
                while (exclude.length < getTeamsCopy().size() && getTeamsCopy().size() > 1) {
                    int player1
                            = getRandomWithExclusion(rnd, 0,
                                    getTeamsCopy().size() - 1, exclude);
                    exclude = ArrayUtils.add(exclude, player1);
                    if (exclude.length == getTeamsCopy().size()) {
                        //Only one player left, pair with Bye
                        LOG.log(Level.FINE, "Pairing {0} vs. BYE",
                                getTeamsCopy().get(player1).getName());
                        pairings.put(encounterCount,
                                new Encounter(encounterCount,
                                        getTeamsCopy().get(player1), bye));
                        try {
                            //Assign the win already, BYE always losses
                            pairings.get(encounterCount)
                                    .updateResult(getTeamsCopy().get(player1),
                                            EncounterResult.WIN);
                        } catch (TournamentException ex) {
                            LOG.log(Level.SEVERE, null, ex);
                        }
                    } else {
                        int player2 = getRandomWithExclusion(rnd, 0,
                                getTeamsCopy().size() - 1, exclude);
                        pairings.put(encounterCount,
                                new Encounter(encounterCount, getTeamsCopy().get(player1),
                                        getTeamsCopy().get(player2)));
                        exclude = ArrayUtils.add(exclude, player2);
                    }
                    encounterCount++;
                }
                pairingHistory.put(getRound(), pairings);
            }
        }
        return pairingHistory.get(getRound());
    }

    @Override
    public TeamInterface getWinnerTeam() {
        TeamInterface winner = null;
        if (round > 1) {
            winner = (TeamInterface) getTeamsCopy().toArray()[0];
        }
        return winner;
    }
    
    @Override
    public int getWinPoints() {
        return winPoints;
    }

    @Override
    public int getLossPoints() {
        return lossPoints;
    }

    @Override
    public int getDrawPoints() {
        return drawPoints;
    }
    
    @Override
    public int getPoints(TeamInterface team) {
        return team.getTeamMembers().get(0).getWins() * getWinPoints()
                + team.getTeamMembers().get(0).getDraws() * getDrawPoints();
    }

    /**
     * @return the teamsCopy
     */
    public List<TeamInterface> getTeamsCopy() {
        return teamsCopy;
    }
}
