package net.sourceforge.javydreamercsw.database.storage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

import net.sourceforge.javydreamercsw.database.storage.db.AbstractServerTest;
import net.sourceforge.javydreamercsw.tournament.manager.AbstractTournament;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageException;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DatabaseStorageTest extends AbstractServerTest
{

  public DatabaseStorageTest()
  {
  }

  /**
   * Test of saveTournament method, of class DatabaseStorage.
   */
  @Test
  public void testSaveTournament()
  {
    System.out.println("saveTournament (new)");
    TournamentInterface ti = new TournamentImpl(3, 0, 1);
    DatabaseStorage instance = new DatabaseStorage();
    assertFalse(instance.isInitialized());
    try
    {
      ti.addTeam(new Team(new Player("Player 1")));
      ti.addTeam(new Team(new Player("Player 2")));
      ti.getPairings();
      instance.saveTournament(ti);
    }
    catch (StorageException | TournamentSignupException ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
    assertTrue(instance.isInitialized());
    try
    {
      TournamentImpl ti2 = new TournamentImpl(3, 0, 1);
      instance.loadTournament(ti.getId());
      assertEquals(ti.getDrawPoints(), ti2.getDrawPoints());
      assertEquals(ti.getWinPoints(), ti2.getWinPoints());
      assertEquals(ti.getLossPoints(), ti2.getLossPoints());
      assertEquals(ti.getRound(), ti2.getRound());
    }
    catch (StorageException ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
  }

  @ServiceProvider(service = TournamentInterface.class)
  public static class TournamentImpl extends AbstractTournament
  {

    public TournamentImpl()
    {
      super(0, 0, 0, true);
    }

    public TournamentImpl(int winPoints, int lossPoints, int drawPoints,
            boolean pairAlikeRecords)
    {
      super(winPoints, lossPoints, drawPoints, pairAlikeRecords);
    }

    public TournamentImpl(int winPoints, int lossPoints, int drawPoints)
    {
      super(winPoints, lossPoints, drawPoints);
    }

    @Override
    public String getName()
    {
      return "Test";
    }

    @Override
    public int getMinimumAmountOfRounds()
    {
      return 1;
    }

    @Override
    public TournamentInterface createTournament(List<TeamInterface> teams,
            int winPoints, int lossPoints, int drawPoints)
    {
      TournamentImpl t = new TournamentImpl(winPoints, lossPoints,
              drawPoints, pairAlikeRecords);
      t.teams.addAll(teams);
      return t;
    }
  }
}
