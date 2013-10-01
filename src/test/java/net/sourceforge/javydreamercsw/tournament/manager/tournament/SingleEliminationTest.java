package net.sourceforge.javydreamercsw.tournament.manager.tournament;

import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class SingleEliminationTest {

    public SingleEliminationTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getName method, of class SingleElimination.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        SingleElimination instance = new SingleElimination();
        assertFalse(instance.getName().trim().isEmpty());
    }

    /**
     * Test of getPairings method, of class SingleElimination.
     */
    @Test
    public void testGetPairings() {
        System.out.println("getPairings");
        //Even entries
        System.out.println("Even amount of entries -----------------------");
        SingleElimination instance = new SingleElimination();
        int limit = new Random().nextInt(1000) + 100;
        if (limit % 2 != 0) {
            //Not even, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            Player temp = new Player("Player #" + i);
            try {
                instance.addPlayer(temp);
            } catch (TournamentSignupException ex) {
                Logger.getLogger(SingleEliminationTest.class.getName()).log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Amount of registered players: "
                + instance.getAmountOfPlayers());
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            Logger.getLogger(SingleEliminationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        Map<Integer, Encounter> result = instance.getPairings();
        System.out.println("Amount of pairings: "
                + result.size());
        assertEquals(instance.getAmountOfPlayers() / 2, result.size());
        instance.showPairings();
        //Redo with odd entries
        System.out.println("Odd amount of entries -----------------------");
        instance = new SingleElimination();
        limit = new Random().nextInt(1000) + 100;
        if (limit % 2 == 0) {
            //Not odd, add one
            limit++;
        }
        for (int i = 0; i < limit; i++) {
            Player temp = new Player("Player #" + i);
            try {
                instance.addPlayer(temp);
            } catch (TournamentSignupException ex) {
                Logger.getLogger(SingleEliminationTest.class.getName()).log(Level.SEVERE, null, ex);
                fail();
            }
        }
        System.out.println("Amount of registered players: "
                + instance.getAmountOfPlayers());
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            Logger.getLogger(SingleEliminationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        result = instance.getPairings();
        System.out.println("Amount of pairings: "
                + result.size());
        assertEquals(instance.getAmountOfPlayers() / 2 + 1, result.size());
        instance.showPairings();
        int players = instance.getAmountOfPlayers();
        instance.updateEncounterResult(1, 0, EncounterResult.WIN);
        //Make sure loosers are removed.
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            Logger.getLogger(SingleEliminationTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        assertEquals(players - 1, instance.getAmountOfPlayers());
    }
}
