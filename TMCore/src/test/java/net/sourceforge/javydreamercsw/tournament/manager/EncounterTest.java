package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
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
            TeamInterface team1 = new Team(new Player("Player 1"));
            TeamInterface team2 = new Team(new Player("Player 2"));
            Encounter instance = new Encounter(1, team1, team2);
            instance.updateResult(team1, EncounterResult.WIN);
            instance.updateResult(team2, EncounterResult.LOSS);
            assertEquals(1, team1.getTeamMembers().get(0).getRecord().getWins());
            assertEquals(1, team2.getTeamMembers().get(0).getRecord().getLosses());
            instance.updateResult(team1, EncounterResult.LOSS);
            instance.updateResult(team2, EncounterResult.WIN);
            assertEquals(1, team2.getTeamMembers().get(0).getRecord().getWins());
            assertEquals(1, team1.getTeamMembers().get(0).getRecord().getLosses());
            instance.updateResult(team1, EncounterResult.DRAW);
            instance.updateResult(team2, EncounterResult.DRAW);
            assertEquals(1, team1.getTeamMembers().get(0).getRecord().getDraws());
            assertEquals(1, team2.getTeamMembers().get(0).getRecord().getDraws());
        } catch (TournamentException ex) {
            Logger.getLogger(EncounterTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }
}
