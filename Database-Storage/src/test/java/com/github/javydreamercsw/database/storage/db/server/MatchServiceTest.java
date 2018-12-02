package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Random;

import org.openide.util.Exceptions;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchServiceTest extends AbstractServerTest
{
  /**
   * Test of write2DB method, of class MatchServer.
   *
   * @throws
   * com.github.javydreamercsw.tournament.manager.api.TournamentException
   */
  @Test
  public void testMatchService() throws TournamentException, Exception
  {
    Tournament t = new Tournament("Test 1");
    TournamentService.getInstance().saveTournament(t);
    TournamentService.getInstance().addRound(t);

    Game game = new Game("Test Game");
    GameService.getInstance().saveGame(game);

    Format format = new Format("Default");
    format.setGame(game);
    FormatService.getInstance().saveFormat(format);

    MatchEntry me = new MatchEntry();
    me.setFormat(format);
    me.setRound(t.getRoundList().get(0));

    MatchService.getInstance().saveMatch(me);

    MatchEntry match
            = MatchService.getInstance().findMatch(me.getMatchEntryPK());
    assertNotNull(me.getFormat());
    assertNotNull(match.getFormat());

    // Add teams
    Player player = new Player("Player 1");
    PlayerService.getInstance().savePlayer(player);

    Player player2 = new Player("Player 2");
    PlayerService.getInstance().savePlayer(player2);

    TeamService.getInstance().getAll().forEach(team ->
    {
      try
      {
        MatchService.getInstance().addTeam(me, team);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
        fail();
      }
    });

    match = MatchService.getInstance().findMatch(me.getMatchEntryPK());
    assertEquals(match.getMatchHasTeamList().size(), 2);
    assertNotNull(match.getFormat());

    Random random = new Random();
    List<MatchResultType> resultTypes = MatchService.getInstance().getResultTypes();
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      MatchService.getInstance().setResult(mht,
              resultTypes.get(random.nextInt(resultTypes.size())));
    }

    List<Object> results = DataBaseManager.namedQuery("MatchEntry.findAll");
    assertTrue(results.size() > 0);

    results.forEach(result ->
    {
      MatchEntry m = (MatchEntry) result;
      assertEquals(m.getMatchHasTeamList().size(), 2);
      assertNotNull(m.getFormat());
    });

    assertTrue(match.getMatchHasTeamList().size() > 0);
    //Check that the record is updated accordingly.
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      MatchResult result = mht.getMatchResult();
      MatchService.getInstance().lockMatchResult(result);

      assertTrue(mht.getTeam().getPlayerList().size() > 0);
      mht.getTeam().getPlayerList().forEach(p ->
      {
        switch (result.getMatchResultType().getType())
        {
          case "result.loss":
            assertEquals(p.getRecordList().get(0).getWins(), 0);
            assertEquals(p.getRecordList().get(0).getLoses(), 1);
            assertEquals(p.getRecordList().get(0).getDraws(), 0);
            break;
          case "result.draw":
            assertEquals(p.getRecordList().get(0).getWins(), 0);
            assertEquals(p.getRecordList().get(0).getLoses(), 0);
            assertEquals(p.getRecordList().get(0).getDraws(), 1);
            break;
          //Various reasons leading to a win.
          case "result.win":
          //Fall thru
          case "result.forfeit":
          //Fall thru
          case "result.no_show":
            assertEquals(p.getRecordList().get(0).getWins(), 1);
            assertEquals(p.getRecordList().get(0).getLoses(), 0);
            assertEquals(p.getRecordList().get(0).getDraws(), 0);
            break;
        }
      });
    }

    // All players must hava a non-zero record.
    assertFalse(PlayerService.getInstance().getAll().isEmpty());
    PlayerService.getInstance().getAll().forEach(p ->
    {
      assertTrue(p.getRecordList().size() == 1);
      assertTrue(p.getRecordList().get(0).getDraws()
              + p.getRecordList().get(0).getLoses()
              + p.getRecordList().get(0).getWins() > 0);
    });
  }
}
