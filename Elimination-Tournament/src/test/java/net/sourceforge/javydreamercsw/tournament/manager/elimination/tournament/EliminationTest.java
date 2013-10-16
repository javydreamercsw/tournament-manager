package net.sourceforge.javydreamercsw.tournament.manager.elimination.tournament;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.junit.Test;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EliminationTest {

    private static final Logger LOG
            = Logger.getLogger(EliminationTest.class.getName());

    public EliminationTest() {
    }

    /**
     * Test of getName method, of class Elimination.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        Elimination instance = new Elimination();
        assertFalse(instance.getName().trim().isEmpty());
    }

    /**
     * Test of getPairings method, of class Elimination.
     */
    @Test
    public void testGetPairings() {
        LOG.info("getPairings");
        //Even entries
        LOG.info("Even amount of entries -----------------------");
        Elimination instance = new Elimination();
        int limit = new Random().nextInt(1000) + 100;
        if (limit % 2 != 0) {
            //Not even, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            try {
                instance.addTeam(new Team(new Player(MessageFormat.format("Player #{0}", i))));
            } catch (TournamentSignupException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        LOG.log(Level.INFO, "Amount of registered teams: {0}",
                instance.getAmountOfTeams());
        Map<Integer, Encounter> result = instance.getPairings();
        printPairings(result);
        LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
        assertEquals(instance.getAmountOfTeams() / 2, result.size());
        int players = instance.getAmountOfTeams();
        Encounter e = result.values().toArray(new Encounter[]{})[instance.getAmountOfTeams() / 2 - 1];
        TeamInterface t
                = e.getEncounterSummary().keySet().toArray(new TeamInterface[]{})[0];
        try {
            LOG.log(Level.INFO, "Updating result for: {0} encounter id: {1}",
                    new Object[]{t.getName(), e.getId()});
            instance.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
        } catch (TournamentException ex) {
            //Expected failure
        }

        try {
            //update the result
            instance.updateResults(e.getId(), t, EncounterResult.WIN);
        } catch (TournamentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        //Make sure loosers are removed.
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertEquals(players - 1, instance.getAmountOfTeams());
        //Redo with odd entries
        LOG.info("Odd amount of entries -----------------------");
        instance = new Elimination();
        limit = new Random().nextInt(1000) + 100;
        if (limit % 2 == 0) {
            //Not odd, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            try {
                instance.addTeam(new Team(new Player(MessageFormat.format("Player #{0}", i))));
            } catch (TournamentSignupException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        LOG.log(Level.INFO, "Amount of registered players: {0}",
                instance.getAmountOfTeams());
        result = instance.getPairings();
        LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
        assertEquals(instance.getAmountOfTeams() / 2 + 1, result.size());
        players = instance.getAmountOfTeams();
        e = result.values().toArray(new Encounter[]{})[instance.getAmountOfTeams() / 2 - 1];
        t = e.getEncounterSummary().keySet().toArray(new TeamInterface[]{})[0];
        try {
            instance.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
        } catch (TournamentException ex) {
            //Expected failure
        }
        try {
            //update the result
            instance.updateResults(e.getId(), t, EncounterResult.WIN);
        } catch (TournamentException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        //Make sure loosers are removed.
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            LOG.log(Level.SEVERE, null, ex);
            fail();
        }
        assertEquals(players - 1, instance.getAmountOfTeams());
    }

    private void printPairings(Map<Integer, Encounter> result) {
        for (Entry<Integer, Encounter> entry : result.entrySet()) {
            Encounter encounter = entry.getValue();
            TeamInterface[] teams = encounter.getEncounterSummary().keySet().toArray(new TeamInterface[]{});
            LOG.log(Level.INFO, "{0}: {1} vs. {2}", new Object[]{entry.getKey(),
                teams[0].toString(), teams[1].toString()});
        }
    }

    /**
     * Test of tournament simulation.
     */
    @Test
    public void testSimulateTournament() {
        LOG.info("Simulate tournament");
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            LOG.log(Level.INFO, "Simulation #{0}", (i + 1));
            int eliminations = random.nextInt(2) + 1;
            LOG.log(Level.INFO, "Eliminations: {0}", eliminations);
            Elimination instance = new Elimination(eliminations,
                    random.nextBoolean());
            int limit = new Random().nextInt(1000) + 100;
            for (int y = 0; y < limit; y++) {
                try {
                    instance.addTeam(new Team(
                            new Player(MessageFormat.format("Player #{0}", (y + 1)))));
                } catch (TournamentSignupException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            LOG.log(Level.INFO, "Amount of registered players: {0}",
                    instance.getAmountOfTeams());
            LOG.log(Level.INFO, "Amount of expected rounds: {0}",
                    instance.getMinimumAmountOfRounds());
            boolean ignore = false;
            while (instance.getAmountOfTeams() > 1) {
                try {
                    instance.nextRound();
                    if (instance.getTeamsCopy().size() > 1) {
                        LOG.log(Level.INFO, "Round {0}", instance.getRound());
                        LOG.info("Pairings...");
                        assertFalse(instance.roundComplete());
                        LOG.info("Simulating results...");
                        for (Entry<Integer, Encounter> entry : instance.getPairings().entrySet()) {
                            Encounter encounter = entry.getValue();
                            TeamInterface player1
                                    = encounter.getEncounterSummary().keySet().toArray(
                                            new TeamInterface[]{})[0];
                            TeamInterface player2
                                    = encounter.getEncounterSummary().keySet().toArray(
                                            new Team[]{})[1];
                            //Make sure is not paired against BYE
                            if (!player1.equals(instance.bye) && !player2.equals(instance.bye)) {
                                //Random Result
                                int range = EncounterResult.values().length - 1;
                                int result = random.nextInt(range);
                                instance.updateResults(encounter.getId(), player1,
                                        EncounterResult.values()[result]);
                            }
                            if (player1.equals(instance.bye) || player2.equals(instance.bye)
                                    && instance.getTeamsCopy().size() == 1) {
                                //Only one player left, we got a winner!
                                ignore = true;
                                break;
                            }
                        }
                    }
                } catch (TournamentException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            if (!ignore) {
                assertTrue(instance.roundComplete());
            }
            //Random player drop
            if (instance.getTeamsCopy().size() > 1 && random.nextBoolean()) {
                TeamInterface toDrop
                        = instance.getTeamsCopy().get(random.nextInt(instance.getAmountOfTeams()));
                LOG.log(Level.INFO, "Player: {0} dropped!", toDrop.getName());
                try {
                    instance.removeTeam(toDrop);
                } catch (TournamentSignupException ex) {
                    LOG.log(Level.SEVERE, null, ex);
                    fail();
                }
            }
            if (instance.getTeamsCopy().size() > 0) {
                TeamInterface winner = instance.getTeamsCopy().get(0);
                assertTrue(winner.getTeamMembers().get(0).getLosses()
                        + winner.getTeamMembers().get(0).getDraws() < eliminations);
                LOG.log(Level.INFO, "Tournament winner: {0}", winner);
            } else {
                //They drew in the finals
                LOG.log(Level.INFO, "Tournament winner: None (draw)");
            }
            instance.displayRankings();
            //To store the amount of points on each ranking spot.
            List<Integer> points = new ArrayList<>();
            for (Entry<Integer, List<TeamInterface>> rankings : instance.getRankings().entrySet()) {
                if (rankings.getValue().size() > 0) {
                    int max = -1;
                    for (TeamInterface team : rankings.getValue()) {
                        //Everyone tied has same amount of points
                        assertTrue(max == -1 || instance.getPoints(team) == max);
                        max = instance.getPoints(team);
                    }
                    points.add(max);
                }
            }
            for (int x = points.size() - 1; x > 0; x--) {
                assertTrue(points.get(x) < points.get(x - 1));
            }
        }
    }
}
