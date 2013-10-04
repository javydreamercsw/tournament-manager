package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
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
public class EncounterTest {

    public EncounterTest() {
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
     * Test of updateResult method, of class Encounter.
     */
    @Test
    public void testUpdateResult() {
        try {
            System.out.println("updateResult");
            Player player1 = new Player("Player 1");
            Player player2 = new Player("Player 2");
            Encounter instance = new Encounter(1,
                    new Team(player1),
                    new Team(player2));
            instance.updateResult(player1, EncounterResult.WIN);
            instance.updateResult(player2, EncounterResult.LOSS);
            assertEquals(1, player1.getWins());
            assertEquals(1, player2.getLosses());
            instance.updateResult(player1, EncounterResult.LOSS);
            instance.updateResult(player2, EncounterResult.WIN);
            assertEquals(1, player2.getWins());
            assertEquals(1, player1.getLosses());
            instance.updateResult(player1, EncounterResult.DRAW);
            instance.updateResult(player2, EncounterResult.DRAW);
            assertEquals(1, player1.getDraws());
            assertEquals(1, player2.getDraws());
        } catch (TournamentException ex) {
            Logger.getLogger(EncounterTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
