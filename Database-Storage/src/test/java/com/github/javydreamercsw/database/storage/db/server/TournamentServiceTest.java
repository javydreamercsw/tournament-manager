package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentListener;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentServiceTest extends AbstractServerTest
{
  private Tournament t;

  @BeforeClass
  @Override
  public void setup() throws NonexistentEntityException, IllegalOrphanException
  {
    try
    {
      super.setup();
      t = new Tournament("Test");
      t.setTournamentFormat(TournamentService.getInstance()
              .findFormat(Lookup.getDefault().lookup(TournamentInterface.class)
                      .getName()));
      t.setFormat(FormatService.getInstance().getAll().get(0));
      TournamentService.getInstance().saveTournament(t);
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
  }

  @Test
  public void testAddTeam() throws Exception
  {
    Player player = new Player("Player 1");
    PlayerService.getInstance().savePlayer(player);

    Player player2 = new Player("Player 2");
    PlayerService.getInstance().savePlayer(player2);

    Team team = new Team("Test Team");
    team.getPlayerList().add(player);
    team.getPlayerList().add(player2);
    TeamService.getInstance().saveTeam(team);

    TournamentService.getInstance().addTeam(t, team);

    assertEquals(t.getTournamentHasTeamList().size(), 1);

    TournamentService.getInstance().deleteTeamFromTournament(t
            .getTournamentHasTeamList().get(0));
  }

  @Test
  public void testFindTournament()
  {
    assertNotNull(TournamentService.getInstance().findTournament(t.getTournamentPK()));
    assertNull(TournamentService.getInstance().findTournament(new TournamentPK(2_000)));
  }

  @Test
  public void testFindTournaments()
  {
    assertEquals(TournamentService.getInstance().findTournaments(t.getName())
            .size(), 1);
    assertEquals(TournamentService.getInstance().findTournaments(t.getName()
            + "x").size(), 0);
  }

  @Test
  public void testSaveAndDelete() throws IllegalOrphanException,
          NonexistentEntityException, Exception
  {
    Tournament t2 = new Tournament("Test 2");
    t2.setTournamentFormat(TournamentService.getInstance()
            .findFormat(Lookup.getDefault().lookup(TournamentInterface.class)
                    .getName()));
    assertEquals(TournamentService.getInstance().findTournaments(t2.getName())
            .size(), 0);
    t2.setFormat(FormatService.getInstance().getAll().get(0));
    TournamentService.getInstance().saveTournament(t2);

    assertEquals(TournamentService.getInstance().findTournaments(t2.getName())
            .size(), 1);

    TournamentService.getInstance().deleteTournament(t2);

    assertEquals(TournamentService.getInstance().findTournaments(t2.getName())
            .size(), 0);
  }

  @Test
  public void testAddTeams() throws Exception
  {
    Tournament tournament = new Tournament("Add Team");
    tournament.setTournamentFormat(TournamentService.getInstance()
            .findFormat(Lookup.getDefault().lookup(TournamentInterface.class)
                    .getName()));
    tournament.setFormat(FormatService.getInstance().getAll().get(0));
    TournamentService.getInstance().saveTournament(tournament);

    Player player3 = new Player("Player 3");
    PlayerService.getInstance().savePlayer(player3);

    Player player4 = new Player("Player 4");
    PlayerService.getInstance().savePlayer(player4);

    Team team2 = new Team("Test Team 2");
    team2.getPlayerList().add(player3);
    team2.getPlayerList().add(player4);
    TeamService.getInstance().saveTeam(team2);
    Player player5 = new Player("Player 5");
    PlayerService.getInstance().savePlayer(player5);

    Player player6 = new Player("Player 6");
    PlayerService.getInstance().savePlayer(player6);

    Team team3 = new Team("Test Team");
    team3.getPlayerList().add(player3);
    team3.getPlayerList().add(player5);
    TeamService.getInstance().saveTeam(team3);

    List<Team> teams = new ArrayList<>();
    teams.add(team3);
    teams.add(team2);

    TournamentService.getInstance().addTeams(tournament, teams);

    assertEquals(TournamentService.getInstance().findTournament(tournament
            .getTournamentPK()).getTournamentHasTeamList().size(), 2);
  }

  @DataProvider
  public Object[][] getScenarios()
  {
    return new Object[][]
    {
      {
        4, 2, 1 // Two teams of 2 people. Should be done in one round.
      },
      {
        2, 1, 1 // Two teams of 1 people. Should be done in one round.
      },
      {
        8, 1, 3 // Eight teams of 1 people. Should be done in 3 rounds.
      },
            {
        16, 2, 3 // Eight teams of 1 people. Should be done in 3 rounds.
      }
    };
  }

  /**
   * This tests the whole workflow of the tournament.
   *
   * @param amountOfPlayers Players to test with.
   * @param playersPerTeam Amount of players in a team.
   * @param expectedRounds Expected amount of rounds.
   * @throws IllegalOrphanException
   */
  @Test(dataProvider = "getScenarios")
  public void testTournament(final int amountOfPlayers,
          final int playersPerTeam, final int expectedRounds)
          throws IllegalOrphanException, Exception
  {
    Tournament tournament = new Tournament("Workflow");
    tournament.setTournamentFormat(TournamentService.getInstance()
            .findFormat(Lookup.getDefault().lookup(TournamentInterface.class)
                    .getName()));
    tournament.setFormat(FormatService.getInstance().getAll().get(0));
    TournamentService.getInstance().saveTournament(tournament);

    int i = TournamentService.getInstance().getAll().size() + 1;

    Player last = null;
    for (int x = 1; x <= amountOfPlayers; x++)
    {
      Player player = new Player("Player " + i);
      PlayerService.getInstance().savePlayer(player);

      if (x % playersPerTeam == 0 && x >= 1)
      {
        // Create a team with previous player
        Team team = new Team("Test Team " + (x / playersPerTeam));
        if (last != null)
        {
          team.getPlayerList().add(last);
        }
        team.getPlayerList().add(player);
        TeamService.getInstance().saveTeam(team);
        TournamentService.getInstance().addTeam(tournament, team);
        last = null;
      }
      else
      {
        // Save for next team
        last = player;
      }
      i++;
    }

    assertEquals(tournament.getTournamentHasTeamList().size(),
            amountOfPlayers / playersPerTeam);

    assertEquals(tournament.getRoundList().size(), 0);

    assertFalse(TournamentService.getInstance().hasStarted(tournament));

    int currentRound = 0;
    List<Integer> roundStarted = new ArrayList<>();
    List<Integer> roundOver = new ArrayList<>();
    // Start the tournament
    TournamentService.getInstance().startTournament(tournament,
            new TournamentListener()
    {
      @Override
      public void roundStart(int round)
      {
        roundStarted.add(round);
      }

      @Override
      public void roundTimeOver()
      {
        // Do nothing
      }

      @Override
      public void roundOver(int round)
      {
        roundOver.add(round);
      }

      @Override
      public void noshow()
      {
        // Do nothing
      }
    });

    currentRound++;

    assertTrue(TournamentService.getInstance().hasStarted(tournament));

    assertEquals(currentRound, (int) roundStarted.get(roundStarted.size() - 1));

    assertEquals(tournament.getRoundList().size(), currentRound);

    assertEquals(tournament.getRoundList().get(0).getMatchEntryList().size(),
            amountOfPlayers / (playersPerTeam * 2));

    Round retrievedRound = TournamentService.getInstance()
            .getRound(tournament.getRoundList().get(0).getRoundPK());

    assertNotNull(retrievedRound);

    assertEquals(retrievedRound.getMatchEntryList().size(),
            amountOfPlayers / (playersPerTeam * 2));

    assertFalse(TournamentService.getInstance().isRoundOver(tournament,
            currentRound));

    Random random = new Random();

    //Now simulate matches and monitor the persistence of the round as it progresses.
    while (!TournamentService.getInstance().isOver(tournament))
    {
      // Simulate matches in the current round
      for (MatchEntry me : retrievedRound.getMatchEntryList())
      {
        int teams = me.getMatchHasTeamList().size();
        int winner = random.nextInt(teams);
        int count = 0;
        for (MatchHasTeam mht : me.getMatchHasTeamList())
        {
          if (count == winner)
          {
            TournamentService.getInstance().setResult(tournament, mht,
                    MatchService.getInstance().getResultType("result.win").get());
          }
          else
          {
            TournamentService.getInstance().setResult(tournament, mht,
                    MatchService.getInstance().getResultType("result.loss").get());
          }
          count++;
        }
      }

      // Round should be over
      assertTrue(TournamentService.getInstance().isRoundOver(tournament,
              currentRound));

      assertEquals((int) roundOver.get(roundOver.size() - 1), currentRound);

      TournamentService.getInstance().startNextRound(tournament);
      if (!TournamentService.getInstance().isOver(tournament))
      {
        currentRound++;

        Round r = tournament.getRoundList()
                .get(tournament.getRoundList().size() - 1);

        assertFalse(r.getMatchEntryList().isEmpty());

        retrievedRound = TournamentService.getInstance().getRound(r.getRoundPK());

        assertNotNull(retrievedRound);

        assertFalse(retrievedRound.getMatchEntryList().isEmpty());
      }
      else
      {

        assertEquals(currentRound, expectedRounds);
      }
      assertTrue(currentRound <= expectedRounds,
              "Unexpected amount of rounds.\nExpected: " + expectedRounds
              + "\nFound: " + currentRound + "\n");
    }
    TournamentService.getInstance().deleteTournament(tournament);
  }
}
