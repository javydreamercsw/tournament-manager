package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.ResultListener;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.openide.util.Lookup;
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
        synchronized (teamsCopy) {
            if (pairingHistory.get(getRound()) == null) {
                //Remove teams with loses from tournament
                List<TeamInterface> toRemove
                        = new ArrayList<>();
                for (TeamInterface team : teamsCopy) {
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
                super.getPairings();
            }
        }
        return pairingHistory.get(getRound());
    }

    @Override
    public void showPairings() {
        Map<Integer, Encounter> pairings = getPairings();
        for (Entry<Integer, Encounter> entry : pairings.entrySet()) {
            System.out.println(MessageFormat.format("{0} vs. {1}",
                    entry.getValue().getEncounterSummary().keySet().toArray(
                            new Team[]{})[0].getTeamMembers().get(0).toString(),
                    entry.getValue().getEncounterSummary().keySet().toArray(
                            new Team[]{})[1].getTeamMembers().get(0).toString()));
        }
    }

    @Override
    public void updateResults(int encounterId,
            TeamInterface team, EncounterResult result)
            throws TournamentException {
        //Normal processing of results
        for (Map.Entry<Integer, Map<Integer, Encounter>> entry : pairingHistory.entrySet()) {
            if (entry.getValue().containsKey(encounterId)) {
                //Found round
                Encounter encounter = entry.getValue().get(encounterId);
                Map<TeamInterface, EncounterResult> encounterSummary
                        = encounter.getEncounterSummary();
                for (Map.Entry<TeamInterface, EncounterResult> entry2 : encounterSummary.entrySet()) {
                    if (entry2.getKey().hasMember(team.getTeamMembers().get(0))) {
                        //Apply result to this team
                        encounter.updateResult(entry2.getKey(), result);
                        for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class)) {
                            listener.updateResults(encounter);
                        }
                    } else {
                        //Depending on the result for the target team assign result for the others
                        switch (result) {
                            case WIN:
                                //All others are losers
                                encounter.updateResult(entry2.getKey(), EncounterResult.LOSS);
                                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class)) {
                                    listener.updateResults(encounter);
                                }
                                break;
                            case NO_SHOW:
                            //Fall thru
                            case LOSS:
                                //All others are winners
                                encounter.updateResult(entry2.getKey(), EncounterResult.WIN);
                                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class)) {
                                    listener.updateResults(encounter);
                                }
                                break;
                            case DRAW:
                                //Everyone drew
                                encounter.updateResult(entry2.getKey(), EncounterResult.DRAW);
                                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class)) {
                                    listener.updateResults(encounter);
                                }
                                break;
                            default:
                                throw new TournamentException("Unhandled result: " + result);
                        }
                    }
                }
            }
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

    private int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    public int getPoints(TournamentPlayerInterface player) {
        return player.getWins() * 3 + player.getDraws();
    }
}
