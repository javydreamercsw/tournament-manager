package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;

public class RankingManagerNGTest extends AbstractServerTest
{

  private Game game;
  private Tournament t = new Tournament("Test 1");

  @BeforeClass
  @Override
  public void setup() throws NonexistentEntityException, IllegalOrphanException,
          Exception
  {
    super.setup();
    game = GameService.getInstance().findGameByName(Lookup.getDefault()
            .lookup(IGame.class).getName()).get();
    t.setTournamentFormat(TournamentService.getInstance()
            .findFormat(Lookup.getDefault().lookup(TournamentInterface.class)
                    .getName()));
    t.setFormat(FormatService.getInstance().getAll().get(0));
    TournamentService.getInstance().saveTournament(t);
    TournamentService.getInstance().addRound(t);

    GameService.getInstance().saveGame(game);
  }

  /**
   * Test of getRankings method, of class RankingManager.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetRankings() throws Exception
  {
    Random random = new Random();

    // Add teams
    for (int i = 1; i <= 4; i++)
    {
      Player player = new Player("Player " + i);
      PlayerService.getInstance().savePlayer(player);
    }

    TreeMap<Integer, MatchEntry> matches = new TreeMap<>();

    for (int i = 0; i < 100; i++)
    {
      TeamService.getInstance().getAll().forEach(team ->
      {
        boolean create = matches.lastEntry() == null
                || matches.lastEntry().getValue().getMatchHasTeamList().size() == 2;
        if (create)
        {
          MatchEntry me = new MatchEntry();
          me.setFormat(game.getFormatList().get(0));
          me.setRound(t.getRoundList().get(0));

          try
          {
            MatchService.getInstance().saveMatch(me);
          }
          catch (Exception ex)
          {
            Exceptions.printStackTrace(ex);
            fail();
          }

          matches.put(matches.size() + 1, me);
        }
        try
        {
          MatchEntry me = matches.lastEntry().getValue();
          MatchService.getInstance().addTeam(me, team);

          boolean firstWins = random.nextBoolean();
          if (matches.lastEntry().getValue().getMatchHasTeamList().size() == 2)
          {
            MatchService.getInstance().setResult(me.getMatchHasTeamList().get(0),
                    firstWins ? MatchService.getInstance().getResultType("result.win").get()
                            : MatchService.getInstance().getResultType("result.loss").get());
            MatchService.getInstance().setResult(me.getMatchHasTeamList().get(1),
                    !firstWins ? MatchService.getInstance().getResultType("result.win").get()
                            : MatchService.getInstance().getResultType("result.loss").get());

            MatchService.getInstance().saveMatch(me);

            MatchService.getInstance().lockMatchResult(me);

            MatchService.getInstance().updateRankings(me);
          }
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
          fail();
        }
      });
    }

    // Check the rankings
    validateRankings(RankingManager.getRankings(FormatService.getInstance().getAll().get(0)));
  }

  /**
   * Test of getRankings method, of class RankingManager. Some edge cases.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testGetRankingsEdges() throws Exception
  {
    // Create ficticious rankings with equal results
    List<TeamHasFormatRecord> items = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < 10; i++)
    {
      TeamHasFormatRecord thfr = new TeamHasFormatRecord();
      if (i < 2)
      {
        thfr.setPoints(100);
        thfr.setMean(10.0);
        thfr.setStandardDeviation(10.0);
      }
      else
      {
        thfr.setPoints(random.nextInt(90));
        thfr.setMean(random.nextDouble() * 100.0);
        thfr.setStandardDeviation(random.nextDouble() * 100.0);
      }
      items.add(thfr);
    }
    
    validateRankings(RankingManager.getRankings(items));
  }

  private void validateRankings(Map<Integer, List<TeamHasFormatRecord>> rankings)
  {
    TeamHasFormatRecord last = null;
    for (Entry<Integer, List<TeamHasFormatRecord>> entry : rankings.entrySet())
    {
      // All should have the same ranking
      if (entry.getValue().size() > 1)
      {
        TeamHasFormatRecord temp = entry.getValue().get(0);
        entry.getValue().forEach((item) ->
        {
          assertEquals(RankingManager.compare(temp, item), 0);
        });
      }
      if (last == null)
      {
        last = entry.getValue().get(0);
      }
      else
      {
        for (TeamHasFormatRecord item : entry.getValue())
        {
          assertTrue(RankingManager.compare(last, item) > 0);
        }
      }
    }
  }
}
