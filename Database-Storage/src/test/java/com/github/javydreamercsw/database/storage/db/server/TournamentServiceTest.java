package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentServiceTest extends AbstractServerTest
{
  /**
   * Test of write2DB method, of class TournamentHasTeamServer.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testWrite2DB() throws Exception
  {
    Tournament t = new Tournament("Test");
    TournamentService.getInstance().saveTournament(t);

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
  }
}
