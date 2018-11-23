package net.sourceforge.javydreamercsw.swiss.tournament;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openide.util.lookup.ServiceProvider;

import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournament;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 * Swiss tournament is an elimination tournament without eliminations. Just
 * finishes when there's one team above the rest by the win point amount or
 * more.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = TournamentInterface.class)
public class Swiss extends AbstractTournament implements TournamentInterface {

    private static final Logger LOG
            = Logger.getLogger(Swiss.class.getSimpleName());

    public Swiss() {
        super(3, 0, 1);
    }

    public Swiss(int winPoints, int lossPoints, int drawPoints) {
        super(winPoints, lossPoints, drawPoints);
    }

    @Override
    public String getName() {
        return "Swiss";
    }

    @Override
    public Map<Integer, Encounter> getPairings() {
        //Now make sure we don't have a winner already
        TreeMap<Integer, List<TeamInterface>> rankings = getRankings();
        if (rankings.size() >= 2 && round > 1) {
            Integer leader = rankings.firstKey();
            List<TeamInterface> highRank = rankings.get(leader);
            displayRankings();
            if (highRank != null && highRank.size() == 1) {
                //Only one leader, make sure has enough points
                TeamInterface potentialWinner = highRank.get(0);
                //Get the next ranking
                int second = leader - 1;
                for (; second >= 0; second--) {
                    if (rankings.get(second) != null) {
                        break;
                    }
                }
                if (leader - second >= getWinPoints()) {
                    LOG.log(Level.INFO, "We got a winner: {0}", 
                            potentialWinner.toString());
                    getActiveTeams().clear();
                    getActiveTeams().add(potentialWinner);
                    return new HashMap<>();
                }
            } else {
                LOG.log(Level.INFO, "Multiple teams tied at ranking 1: {0}",
                        highRank == null ? "null" : highRank.size());
            }
        }
        //Nothing special to process since no one is automatically eliminated
        //on this format.
        super.getPairings();
        return pairingHistory.get(getRound());
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

    @Override
    public TournamentInterface createTournament(List<TeamInterface> teams, 
            int winPoints, int lossPoints, int drawPoints) {
        Swiss swiss = new Swiss(winPoints, lossPoints,  drawPoints);
        swiss.teams.addAll(teams);
        return swiss;
    }
}
