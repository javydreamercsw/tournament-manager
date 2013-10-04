package net.sourceforge.javydreamercsw.tournament.manager;

import net.sourceforge.javydreamercsw.tournament.manager.api.Player;
import net.sourceforge.javydreamercsw.tournament.manager.api.Variables;
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
public class PlayerTest {

    public PlayerTest() {
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
     * Test of win method, of class Player.
     */
    @Test
    public void testWin() {
        System.out.println("win");
        Player instance = new Player("Test");
        assertEquals(0, instance.get(Variables.WINS.getDisplayName()));
        instance.win();
        assertEquals(1, instance.get(Variables.WINS.getDisplayName()));
    }

    /**
     * Test of loss method, of class Player.
     */
    @Test
    public void testLoss() {
        System.out.println("loss");
        Player instance = new Player("Test");
        assertEquals(0, instance.get(Variables.LOSSES.getDisplayName()));
        instance.loss();
        assertEquals(1, instance.get(Variables.LOSSES.getDisplayName()));
    }

    /**
     * Test of draw method, of class Player.
     */
    @Test
    public void testDraw() {
        System.out.println("draw");
        Player instance = new Player("Test");
        assertEquals(0, instance.get(Variables.DRAWS.getDisplayName()));
        instance.draw();
        assertEquals(1, instance.get(Variables.DRAWS.getDisplayName()));
    }

}
