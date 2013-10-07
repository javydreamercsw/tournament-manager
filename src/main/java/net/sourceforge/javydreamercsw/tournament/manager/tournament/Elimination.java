package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
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
        this.eliminations = eliminations;
    }

    public Elimination() {
        this.eliminations = 1;
    }

    public String getName() {
        return "Single Elimination";
    }

    public Map<Integer, Encounter> getPairings() {
        synchronized (playersCopy) {
            if (pairingHistory.get(getRound()) == null) {
                //Remove teams with loses from tournament
                List<TournamentPlayerInterface> toRemove
                        = new ArrayList<TournamentPlayerInterface>();
                for (TournamentPlayerInterface p : playersCopy) {
                    //Loss or draw gets you eliminated
                    if (p.getLosses() + p.getDraws() >= eliminations) {
                        toRemove.add(p);
                    }
                }
                for (TournamentPlayerInterface p : toRemove) {
                    try {
                        LOG.log(Level.FINE, "Removing player: {0}", p.toString());
                        removePlayer(p);
                    } catch (TournamentSignupException ex) {
                        LOG.log(Level.SEVERE, null, ex);
                    }
                }
                super.getPairings();
            }
        }
        return pairingHistory.get(getRound());
    }

    public void showPairings() {
        Map<Integer, Encounter> pairings = getPairings();
        for (Entry<Integer, Encounter> entry : pairings.entrySet()) {
            System.out.println(entry.getValue().getEncounterSummary().keySet().toArray(new Team[]{})[0].getTeamMembers().get(0).toString()
                    + " vs. " + entry.getValue().getEncounterSummary().keySet().toArray(new Team[]{})[1].getTeamMembers().get(0).toString()
            );
        }
    }

    public void updateResults(int encounterId,
            TournamentPlayerInterface player, EncounterResult result)
            throws TournamentException {
        //Normal processing of results
        for (Map.Entry<Integer, Map<Integer, Encounter>> entry : pairingHistory.entrySet()) {
            if (entry.getValue().containsKey(encounterId)) {
                //Found round
                Encounter encounter = entry.getValue().get(encounterId);
                Map<Team, EncounterResult> encounterSummary
                        = encounter.getEncounterSummary();
                for (Map.Entry<Team, EncounterResult> entry2 : encounterSummary.entrySet()) {
                    if (entry2.getKey().getTeamMembers().contains(player)) {
                        //Apply result to this team
                        for (TournamentPlayerInterface target : entry2.getKey().getTeamMembers()) {
                            encounter.updateResult(target, result);
                        }
                    } else {
                        //Depending on the result for the target team assign result for the others
                        switch (result) {
                            case WIN:
                                //All others are losers
                                for (TournamentPlayerInterface target : entry2.getKey().getTeamMembers()) {
                                    encounter.updateResult(target, EncounterResult.LOSS);
                                }
                                break;
                            case NO_SHOW:
                            //Fall thru
                            case LOSS:
                                //All others are winners
                                for (TournamentPlayerInterface target : entry2.getKey().getTeamMembers()) {
                                    encounter.updateResult(target, EncounterResult.WIN);
                                }
                                break;
                            case DRAW:
                                //Everyone drew
                                for (TournamentPlayerInterface target : entry2.getKey().getTeamMembers()) {
                                    encounter.updateResult(target, EncounterResult.DRAW);
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

    public int getMinimumAmountOfRounds() {
        /**
         * Assuming two competitors per match, if there are n competitors, there
         * will be r = log {2} n rounds required, or if there are r rounds,
         * there will be n= 2^r competitors. In the opening round, 2^r - n
         * competitors will get a bye.
         */
        return log(players.size(), 2);
    }

    private int log(int x, int base) {
        return (int) (Math.log(x) / Math.log(base));
    }

    public int getPoints(TournamentPlayerInterface player) {
        return player.getWins() * 3 + player.getDraws();
    }
}
