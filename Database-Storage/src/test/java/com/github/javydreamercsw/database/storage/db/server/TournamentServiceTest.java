package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentPK;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;

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
}
