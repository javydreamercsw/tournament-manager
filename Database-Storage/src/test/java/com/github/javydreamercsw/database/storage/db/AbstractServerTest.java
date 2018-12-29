package com.github.javydreamercsw.database.storage.db;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.GameService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class AbstractServerTest
{

  public AbstractServerTest()
  {
  }

  @BeforeClass
  public void setup() throws NonexistentEntityException, IllegalOrphanException,
          Exception
  {
    DataBaseManager.setPersistenceUnitName("TestTMPU");
    cleanDB();
    DataBaseManager.load();
  }

  @AfterClass
  protected void cleanDB() throws NonexistentEntityException, IllegalOrphanException
  {
    for (MatchEntry match : MatchService.getInstance().getAll())
    {
      MatchService.getInstance().deleteMatch(match);
    }
    for (Tournament tournament : TournamentService.getInstance().getAll())
    {
      TournamentService.getInstance().deleteTournament(tournament);
    }
    TeamService.getInstance().getAll().forEach(team
            -> TeamService.getInstance().deleteTeam(team));
    PlayerService.getInstance().getAll().forEach(player
            -> PlayerService.getInstance().deletePlayer(player));
    FormatService.getInstance().getAll().forEach(format
            -> FormatService.getInstance().deleteFormat(format));
    GameService.getInstance().getAll().forEach(game
            -> GameService.getInstance().deleteGame(game));
  }
}
