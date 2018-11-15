package net.sourceforge.javydreamercsw.tournament.manager;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Test;
import org.openide.util.Exceptions;

import junit.framework.TestCase;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.signup.TournamentSignupException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class AbstractTournamentTester extends TestCase
{

  private final TournamentInterface tournament;
  private static final Logger LOG
          = Logger.getLogger(AbstractTournamentTester.class.getSimpleName());

  public AbstractTournamentTester(TournamentInterface tournament)
  {
    this.tournament = tournament;
  }

  public AbstractTournamentTester()
  {
    this.tournament = generateRandomTournament();
  }

  /**
   * Test the peculiarities of this tournament.
   */
  public abstract void testTournament();
  
  @Test
  public void TestTournament(){
    TestTournament();
  }

  /**
   * Test of getName method, of class Elimination.
   */
  @Test
  public void testGetName()
  {
    LOG.info("getName");
    assertFalse(tournament.getName().trim().isEmpty());
  }

  /**
   * Test of getPairings method, of class Elimination.
   */
  @Test
  public void testGetPairings()
  {
    LOG.info("getPairings");
    //Even entries
    LOG.info("Even amount of entries -----------------------");
    TournamentInterface instance;
    int limit = new Random().nextInt(1000) + 100;
    if (limit % 2 != 0)
    {
      //Not even, add one
      limit++;
    }
    try
    {
      instance = tournament.getClass().newInstance();
      Map<Integer, Encounter> result = instance.getPairings();
      printPairings(result);
      LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
      assertEquals(instance.getAmountOfTeams() / 2, result.size());
      Encounter e = result.values().toArray(new Encounter[0])[(instance.getAmountOfTeams() / 2) - 1];
      TeamInterface t
              = e.getEncounterSummary().keySet().toArray(new TeamInterface[0])[0];
      try
      {
        LOG.log(Level.INFO, "Updating result for: {0} encounter id: {1}",
                new Object[]
                {
                  t.getName(), e.getId()
                });
        instance.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
      }
      catch (TournamentException ex)
      {
        //Expected failure
      }

      try
      {
        //update the result
        instance.updateResults(e.getId(), t, EncounterResult.WIN);
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
      }
      //Make sure loosers are removed.
      try
      {
        instance.nextRound();
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
        fail();
      }
      //Redo with odd entries
      LOG.info("Odd amount of entries -----------------------");
      try
      {
        instance = tournament.getClass().newInstance();
      }
      catch (InstantiationException | IllegalAccessException ex)
      {
        Exceptions.printStackTrace(ex);
        fail();
      }
      limit = new Random().nextInt(1000) + 100;
      if (limit % 2 == 0)
      {
        //Not odd, add one
        limit++;
      }
      for (int i = 0; i < limit; i++)
      {
        try
        {
          instance.addTeam(new Team(new UIPlayer(MessageFormat.format("Player #{0}", i), i)));
        }
        catch (TournamentSignupException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      LOG.log(Level.INFO, "Amount of registered players: {0}",
              instance.getAmountOfTeams());
      result = instance.getPairings();
      LOG.log(Level.INFO, "Amount of pairings: {0}", result.size());
      assertEquals(instance.getAmountOfTeams() / 2 + 1, result.size());
      e = result.values().toArray(new Encounter[]
      {
      })[instance.getAmountOfTeams() / 2 - 1];
      t = e.getEncounterSummary().keySet().toArray(new TeamInterface[]
      {
      })[0];
      try
      {
        instance.updateResults(e.getId(), t, EncounterResult.UNDECIDED);
      }
      catch (TournamentException ex)
      {
        //Expected failure
      }
      try
      {
        //update the result
        instance.updateResults(e.getId(), t, EncounterResult.WIN);
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
      }
      //Make sure loosers are removed.
      try
      {
        instance.nextRound();
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
        fail();
      }
    }
    catch (InstantiationException | IllegalAccessException ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
  }

  private void printPairings(Map<Integer, Encounter> result)
  {
    result.entrySet().forEach((entry) ->
    {
      Encounter encounter = entry.getValue();
      TeamInterface[] teams
              = encounter.getEncounterSummary().keySet()
                      .toArray(new TeamInterface[]
                      {
              });
      LOG.log(Level.INFO, "{0}: {1} vs. {2}", new Object[]
      {
        entry.getKey(),
        teams[0].toString(), teams[1].toString()
      });
    });
  }

  /**
   * Test of tournament simulation.
   */
  @Test
  public void testSimulateTournament()
  {
    LOG.info("Simulate tournament");
    Random random = new Random();
    for (int i = 0; i < 100; i++)
    {
      LOG.log(Level.INFO, "Simulation #{0}", (i + 1));
      TournamentInterface instance = generateRandomTournament();
      int limit = random.nextInt(100);
      for (int y = 0; y < limit; y++)
      {
        try
        {
          instance.addTeam(new Team(
                  new UIPlayer(MessageFormat.format("Player #{0}", (y + 1)), y)));
        }
        catch (TournamentSignupException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      LOG.log(Level.INFO, "Amount of registered players: {0}",
              instance.getAmountOfTeams());
      LOG.log(Level.INFO, "Amount of expected rounds: {0}",
              instance.getMinimumAmountOfRounds());
      boolean ignore = false;
      while (instance.getAmountOfTeams() > 1)
      {
        try
        {
          instance.nextRound();
          if (instance.getActiveTeams().size() > 1)
          {
            LOG.log(Level.INFO, "Round {0}", instance.getRound());
            LOG.info("Pairings...");
            assertFalse(instance.roundComplete());
            LOG.info("Simulating results...");
            for (Map.Entry<Integer, Encounter> entry
                    : instance.getPairings().entrySet())
            {
              Encounter encounter = entry.getValue();
              TeamInterface player1
                      = encounter.getEncounterSummary().keySet().toArray(
                              new TeamInterface[]
                              {
                              })[0];
              TeamInterface player2
                      = encounter.getEncounterSummary().keySet().toArray(
                              new TeamInterface[]
                              {
                              })[1];
              //Make sure is not paired against BYE
              if (!player1.equals(TournamentInterface.BYE)
                      && !player2.equals(TournamentInterface.BYE))
              {
                //Random Result
                int range = EncounterResult.values().length - 1;
                int result = random.nextInt(range);
                instance.updateResults(encounter.getId(), player1,
                        EncounterResult.values()[result]);
              }
              if (player1.equals(TournamentInterface.BYE)
                      || player2.equals(TournamentInterface.BYE) && instance.getActiveTeams().size() == 1)
              {
                //Only one player left, we got a winner!
                ignore = true;
                break;
              }
            }
          }
        }
        catch (TournamentException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      if (!ignore)
      {
        assertTrue(instance.roundComplete());
      }
      //Random player drop
      if (instance.getActiveTeams().size() > 1 && random.nextBoolean())
      {
        TeamInterface toDrop
                = instance.getActiveTeams().get(random.nextInt(instance.getAmountOfTeams()));
        LOG.log(Level.INFO, "Player: {0} dropped!", toDrop.getName());
        try
        {
          instance.removeTeam(toDrop);
        }
        catch (TournamentSignupException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
          fail();
        }
      }
      if (instance.getActiveTeams().size() == 1)
      {
        TeamInterface winner = instance.getActiveTeams().get(0);
        LOG.log(Level.INFO, "Tournament winner: {0}", winner);
      }
      else
      {
        //They drew in the finals
        LOG.log(Level.INFO, "Tournament winner: None (draw)");
      }
      instance.displayRankings();
      //To store the amount of points on each ranking spot.
      List<Integer> points = new ArrayList<>();
      instance.getRankings().entrySet().stream().filter((rankings)
              -> (rankings.getValue().size() > 0)).map((rankings) ->
      {
        int max = -1;
        for (TeamInterface team : rankings.getValue())
        {
          //Everyone tied has same amount of points
          assertTrue(max == -1 || instance.getPoints(team) == max);
          max = instance.getPoints(team);
        }
        return max;
      }).forEachOrdered((max) ->
      {
        points.add(max);
      });
      for (int x = points.size() - 1; x > 0; x--)
      {
        assertTrue(points.get(x) < points.get(x - 1));
      }
    }
  }

  /**
   * Allows to generate random tournament with different settings. Used during
   * the tournament simulations.
   *
   * @return
   */
  public abstract TournamentInterface generateRandomTournament();
}
