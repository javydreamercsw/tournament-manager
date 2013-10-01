package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.List;
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
public class TeamTest {

    public TeamTest() {
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
     * Test of getTeamMembers method, of class Team.
     */
    @Test
    public void testGetTeamMembers() {
        System.out.println("getTeamMembers");
        Team instance = new Team(new Player("Test"));
        List<Player> result = instance.getTeamMembers();
        assertEquals(1, result.size());
        instance.getTeamMembers().add(new Player("Player 2"));
        result = instance.getTeamMembers();
        assertEquals(2, result.size());
    }

    /**
     * Test of toString method, of class Team.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        List<Player> players = new ArrayList<Player>();
        players.add(new Player("Test 1"));
        players.add(new Player("Test 2"));
        Team instance = new Team(players.get(0));
        assertEquals(players.get(0).toString(), instance.toString());
        instance.getTeamMembers().add(players.get(1));
        assertEquals(players.get(0).toString() + ", "
                + players.get(1).toString(), instance.toString());
        instance = new Team("Test Team", players);
        assertEquals("Team " + instance.getName() + "(" + players.get(0).toString() + ", "
                + players.get(1).toString() + ")", instance.toString());
    }

}
