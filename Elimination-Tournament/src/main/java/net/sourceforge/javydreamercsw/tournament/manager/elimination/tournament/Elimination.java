package net.sourceforge.javydreamercsw.tournament.manager.elimination.tournament;

import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournament;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = TournamentInterface.class)
public class Elimination extends AbstractTournament
        implements TournamentInterface {

    private static final Logger LOG
            = Logger.getLogger(Elimination.class.getName());
    private final int eliminations;
    private final boolean pairAlikeRecords = false;

    public Elimination(int eliminations) {
        super(3, 0, 1);
        this.eliminations = eliminations;
    }

    public Elimination() {
        super(3, 0, 1);
        this.eliminations = 1;
    }

    public Elimination(int eliminations, int winPoints, int lossPoints,
            int drawPoints) {
        super(winPoints, lossPoints, drawPoints);
        this.eliminations = eliminations;
    }

    @Override
    public String getName() {
        return "Single Elimination";
    }

    @Override
    public Map<Integer, Encounter> getPairings() {
        synchronized (getTeamsCopy()) {
            if (pairingHistory.get(getRound()) == null) {
                //Remove teams with loses from tournament
                List<TeamInterface> toRemove
                        = new ArrayList<>();
                for (TeamInterface team : getTeamsCopy()) {
                    //Loss or draw gets you eliminated
                    if (team.getTeamMembers().get(0).getLosses()
                            + team.getTeamMembers().get(0).getDraws() >= eliminations) {
                        toRemove.add(team);
                    }
                }
                for (TeamInterface t : toRemove) {
                    try {
                        LOG.log(Level.FINE, "Removing player: {0}", t.toString());
                        removeTeam(t);
                    } catch (TournamentSignupException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                if (pairAlikeRecords) {
                    Map<Integer, Encounter> pairings
                            = new HashMap<>();
                    Integer[] exclude;
                    Random rnd = new Random();
                    //This will hold the reminder unpaired player due to odd number of players with same record.
                    TeamInterface pending = null;
                    for (Entry<Integer, List<TeamInterface>> rankings : getRankings().entrySet()) {
                        //Pair all people with same ranking together
                        exclude = new Integer[]{};
                        List<TeamInterface> players = rankings.getValue();
                        if (pending != null) {
                            //We got someone pending from previous level, pair with him
                            //Pair them
                            TeamInterface opp = null;
                            int lucky;
                            while (opp == null && exclude.length < players.size()) {
                                lucky = rnd.nextInt(players.size());
                                opp = players.get(lucky);
                                //Exclude the unlucky one from the rest of processing
                                exclude = ArrayUtils.add(exclude, lucky);
                                if (isTeamActive(opp)) {
                                    addPairing(pairings, pending, opp);
                                    LOG.log(Level.INFO, "Pairing {0} from higher level with {1}",
                                            new Object[]{pending, opp});
                                    pending = null;
                                } else {
                                    opp = null;
                                }
                            }
                        }

                        if (players.size() % 2 != 0) {
                            //Someone will pair with someone in a lower level, lucky...
                            int lucky = rnd.nextInt(players.size());
                            pending = players.get(lucky);
                            //Exclude the lucky one from the rest of processing
                            exclude = ArrayUtils.add(exclude, lucky);
                            LOG.log(Level.INFO, "Pairing {0} with lower level", pending);
                        }
                        //We have an even number, pair them together
                        while (exclude.length < players.size()) {
                            int player1
                                    = getRandomWithExclusion(rnd, 0,
                                            players.size() - 1, exclude);
                            exclude = ArrayUtils.add(exclude, player1);
                            int player2
                                    = getRandomWithExclusion(rnd, 0,
                                            players.size() - 1, exclude);
                            exclude = ArrayUtils.add(exclude, player2);
                            TeamInterface team1 = players.get(player1);
                            TeamInterface team2 = players.get(player2);
                            //Pair them
                            if (isTeamActive(team1) && isTeamActive(team2)) {
                                addPairing(pairings, team1, team2);
                            }
                        }
                    }
                    if (pending != null) {
                        if (getTeamsCopy().size() == 1) {
                            //Got our winner
                        } else {
                            //We got someone pending. Pair with him BYE
                            addPairing(pairings, pending, bye);
                            LOG.log(Level.INFO, "Pairing {0} with BYE", pending);
                        }
                    }
                    pairingHistory.put(getRound(), pairings);
                } else {
                    super.getPairings();
                }
            }
        }

        return pairingHistory.get(getRound());
    }

    @Override
    public void showPairings() {
        Map<Integer, Encounter> pairings = getPairings();
        for (Entry<Integer, Encounter> entry : pairings.entrySet()) {
            LOG.info(MessageFormat.format("{0} vs. {1}",
                    entry.getValue().getEncounterSummary().keySet().toArray(
                            new TeamInterface[]{})[0].getTeamMembers().get(0).toString(),
                    entry.getValue().getEncounterSummary().keySet().toArray(
                            new TeamInterface[]{})[1].getTeamMembers().get(0).toString()));
        }
    }

    @Override
    public int getMinimumAmountOfRounds() {
        /**
         * Assuming two competitors per match, if there are n competitors, there
         * will be r = log {2} n rounds required, or if there are r rounds,
         * there will be n= 2^r competitors. In the opening round, 2^r - n
         * competitors will get a bye.
         */
        return log(teams.size(), 2);
    }

    protected boolean isTeamActive(TeamInterface team) {
        return getTeamsCopy().contains(team);
    }
}
