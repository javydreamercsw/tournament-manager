package net.sourceforge.javydreamercsw.tournament.manager.elimination.tournament;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static junit.framework.Assert.assertEquals;
import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournamentTester;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class EliminationTest extends AbstractTournamentTester {

    private static final Logger LOG
            = Logger.getLogger(EliminationTest.class.getName());

    public EliminationTest() {
        super(new Elimination());
    }

    @Override
    public TournamentInterface generateRandomTournament() {
        int eliminations = new Random().nextInt(2) + 1;
        LOG.log(Level.INFO, "Eliminations: {0}", eliminations);
        return new Elimination(eliminations, new Random().nextBoolean());
    }
}
