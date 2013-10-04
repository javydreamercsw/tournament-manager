package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SingleEliminationTest {

    private static final Logger LOG
            = Logger.getLogger(SingleEliminationTest.class.getName());

    public SingleEliminationTest() {
    }

    /**
     * Test of getName method, of class SingleElimination.
     */
    @Test
    public void testGetName() {
        LOG.info("getName");
        SingleElimination instance = new SingleElimination();
        assertFalse(instance.getName().trim().isEmpty());
    }

    /**
     * Test of getPairings method, of class SingleElimination.
     */
    @Test
    public void testGetPairings() {
        LOG.info("getPairings");
        //Even entries
        LOG.info("Even amount of entries -----------------------");
        SingleElimination instance = new SingleElimination();
        int limit = 5;//new Random().nextInt(1000) + 100;
        if (limit % 2 != 0) {
            //Not even, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            try {
                instance.addPlayer(new Player("Player #" + i));
            } catch (TournamentSignupException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        LOG.log(Level.INFO, "Amount of registered players: {0}",
                instance.getAmountOfPlayers());
        Map<Integer, Encounter> result = instance.getPairings();
        printPairings(result);
        LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
        assertEquals(instance.getAmountOfPlayers() / 2, result.size());
        int players = instance.getAmountOfPlayers();
        Encounter e = result.values().toArray(new Encounter[]{})[instance.getAmountOfPlayers() / 2 - 1];
        TournamentPlayerInterface t
                = e.getEncounterSummary().keySet().toArray(new Team[]{})[0].getTeamMembers().get(0);
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
        assertEquals(players - 1, instance.getAmountOfPlayers());
        //Redo with odd entries
        LOG.info("Odd amount of entries -----------------------");
        instance = new SingleElimination();
        limit = new Random().nextInt(1000) + 100;
        if (limit % 2 == 0) {
            //Not odd, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            try {
                instance.addPlayer(new Player("Player #" + i));
            } catch (TournamentSignupException ex) {
                LOG.log(Level.SEVERE, null, ex);
                fail();
            }
        }
        LOG.log(Level.INFO, "Amount of registered players: {0}",
                instance.getAmountOfPlayers());
        result = instance.getPairings();
        LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
        assertEquals(instance.getAmountOfPlayers() / 2 + 1, result.size());
        players = instance.getAmountOfPlayers();
        e = result.values().toArray(new Encounter[]{})[instance.getAmountOfPlayers() / 2 - 1];
        t = e.getEncounterSummary().keySet().toArray(new Team[]{})[0].getTeamMembers().get(0);
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
        assertEquals(players - 1, instance.getAmountOfPlayers());
    }

    private void printPairings(Map<Integer, Encounter> result) {
        for (Entry<Integer, Encounter> entry : result.entrySet()) {
            Encounter encounter = entry.getValue();
            Team[] teams = encounter.getEncounterSummary().keySet().toArray(new Team[]{});
            LOG.log(Level.INFO, "{0}: {1} vs. {2}", new Object[]{entry.getKey(),
                teams[0].toString(), teams[1].toString()});
        }
    }
}
