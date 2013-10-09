package net.sourceforge.javydreamercsw.tournament.manager;

import java.text.MessageFormat;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractTournamentTest extends TestCase {

    public AbstractTournamentTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of getRound method, of class AbstractTournament.
     */
    public void testGetRound() {
        System.out.println("getRound");
        AbstractTournament instance = new AbstractTournamentImpl();
        int expResult = 0;
        int result = instance.getRound();
        assertEquals(expResult, result);
        try {
            instance.nextRound();
        } catch (TournamentException ex) {
            Logger.getLogger(AbstractTournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        result = instance.getRound();
        assertEquals(expResult + 1, result);
    }

    /**
     * Test of addTeam method, of class AbstractTournament.
     */
    public void testAddPlayer() {
        System.out.println("addPlayer");
        TeamInterface team = new Team(new Player("Test"));
        AbstractTournament instance = new AbstractTournamentImpl();
        try {
            instance.addTeam(team);
        } catch (TournamentSignupException ex) {
            Logger.getLogger(AbstractTournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        boolean failure = false;
        try {
            instance.addTeam(team);
        } catch (TournamentSignupException ex) {
            //Fails as expected
            failure = true;
        }
        assertTrue(failure);
    }

    /**
     * Test of removeTeam method, of class AbstractTournament.
     */
    public void testRemovePlayer() {
        System.out.println("removePlayer");
        TeamInterface team = new Team(new Player("Test"));
        AbstractTournament instance = new AbstractTournamentImpl();
        boolean failure = false;
        try {
            instance.removeTeam(team);
        } catch (TournamentSignupException ex) {
            //As expected
            failure = true;
        }
        assertTrue(failure);
        try {
            instance.addTeam(team);
        } catch (TournamentSignupException ex) {
            Logger.getLogger(AbstractTournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
        try {
            instance.removeTeam(team);
        } catch (TournamentSignupException ex) {
            Logger.getLogger(AbstractTournamentTest.class.getName()).log(Level.SEVERE, null, ex);
            fail();
        }
    }

    /**
     * Test of getRandomWithExclusion method, of class AbstractTournament.
     */
    public void testGetRandomWithExclusion() {
        System.out.println("getRandomWithExclusion");
        Random rnd = new Random();
        int start = 1;
        int end = 100;
        int tries = end * 100;
        int[] exclude = null;
        AbstractTournament instance = new AbstractTournamentImpl();
        int result = instance.getRandomWithExclusion(rnd, start, end, exclude);
        System.out.println(MessageFormat.format("Result: {0}", result));
        assertTrue(result >= start);
        assertTrue(result <= end);
        exclude = ArrayUtils.add(exclude, result);
        System.out.println(MessageFormat.format("{0} tries excluding: {1}", tries, printArray(exclude)));
        for (int i = 0; i < tries; i++) {
            result = instance.getRandomWithExclusion(rnd, start, end, exclude);
            System.out.println(MessageFormat.format("Result: {0}", result));
            assertTrue(result != exclude[0]);
            assertTrue(result >= start);
            assertTrue(result <= end);
        }
        exclude = ArrayUtils.add(exclude, result);
        System.out.println(MessageFormat.format("{0} tries excluding: {1}", tries, printArray(exclude)));
        for (int i = 0; i < tries; i++) {
            result = instance.getRandomWithExclusion(rnd, start, end, exclude);
            System.out.println(MessageFormat.format("Result: {0}", result));
            assertTrue(result >= start);
            assertTrue(result <= end);
            assertTrue(result != exclude[0]);
            assertTrue(result != exclude[1]);
        }
    }

    public class AbstractTournamentImpl extends AbstractTournament {

        public AbstractTournamentImpl() {
            super(3, 0, 1);
        }

        @Override
        public String getName() {
            return "Test";
        }

        @Override
        public void showPairings() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void updateResults(int encounterID, TeamInterface team, EncounterResult result) throws TournamentException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public int getMinimumAmountOfRounds() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    private String printArray(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (Object o : array) {
            if (!sb.toString().trim().isEmpty()) {
                sb.append(",");
            }
            sb.append(o.toString());
        }
        return sb.toString();
    }
}
