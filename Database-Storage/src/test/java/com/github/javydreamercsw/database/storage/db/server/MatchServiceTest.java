package com.github.javydreamercsw.database.storage.db.server;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchServiceTest extends AbstractServerTest
{

  public MatchServiceTest()
  {
  }

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
    
    assertNotNull(me.getFormat());
    assertNotNull(MatchService.getInstance().findMatches().get(0).getFormat());

    // Add teams
    Player player = new Player("Player 1");
    PlayerService.getInstance().savePlayer(player);

    Player player2 = new Player("Player 2");
    PlayerService.getInstance().savePlayer(player2);

    TeamService.getInstance().findTeams("").forEach(team ->
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
  }
}
