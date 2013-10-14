package net.sourceforge.javydreamercsw.tournament.manager.elimination.tournament;

import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournament;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
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
                super.getPairings();
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
}
