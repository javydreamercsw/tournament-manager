package com.github.javydreamercsw.tournament.manager.web.backend;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.fail;

import java.util.List;

import org.openide.util.Exceptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;

public class MatchServiceNGTest extends BaseTestCase
{
  @BeforeClass
  @Override
  public void setup()
  {
    try
    {
      super.setup();
      System.out.println("Creating players...");
      Player p1 = new Player("Player 1");
      PlayerService.getInstance().savePlayer(p1);
      Player p2 = new Player("Player 2");
      PlayerService.getInstance().savePlayer(p2);
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
      fail();
    }
  }

  /**
   * Test of saveMatch method, of class MatchService.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testSaveMatch() throws Exception
  {
    System.out.println("saveMatch");
    List<Team> teams = TeamService.getInstance().findTeams("");
    assertEquals(teams.size(), 2);

    Tournament t = new Tournament("Test");
    TournamentService.getInstance().saveTournament(t);

    // Add the teams
    TournamentService.getInstance().addTeams(t, teams);

    // Add round
    TournamentService.getInstance().addRound(t);

    // Update
    TournamentService.getInstance().saveTournament(t);

    MatchEntry match = new MatchEntry();
    match.setFormat(FormatService.getInstance().findFormats("").get(0));
    match.setRound(t.getRoundList().get(0));
    MatchService.getInstance().saveMatch(match);

    System.out.println("findMatchesWithFormat");
    assertEquals(MatchService.getInstance().findMatchesWithFormat(FormatService
            .getInstance().findFormats("").get(0).getName()).size(), 1);
    assertEquals(MatchService.getInstance().findMatchesWithFormat(FormatService
            .getInstance().findFormats("").get(1).getName()).size(), 0);

    assertFalse(MatchService.getInstance().findMatch(match.getMatchEntryPK()).isEmpty());
  }
}
