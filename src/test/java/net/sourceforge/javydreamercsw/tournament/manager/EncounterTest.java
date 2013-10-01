package net.sourceforge.javydreamercsw.tournament.manager;

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
        System.out.println("updateResult");
        int team = 0;
        Encounter instance = new Encounter(
                new Team(new Player("Player 1")),
                new Team(new Player("Player 2")));
        instance.updateResult(team, EncounterResult.WIN);
        assertEquals(1, instance.getTeams().get(team).getTeamMembers().get(0).getWins());
        assertEquals(1, instance.getTeams().get(team + 1).getTeamMembers().get(0).getLosses());
        instance.updateResult(team, EncounterResult.LOSS);
        assertEquals(1, instance.getTeams().get(team + 1).getTeamMembers().get(0).getWins());
        assertEquals(1, instance.getTeams().get(team).getTeamMembers().get(0).getLosses());
        instance.updateResult(team, EncounterResult.DRAW);
        assertEquals(1, instance.getTeams().get(team + 1).getTeamMembers().get(0).getDraws());
        assertEquals(1, instance.getTeams().get(team).getTeamMembers().get(0).getDraws());
    }
}
