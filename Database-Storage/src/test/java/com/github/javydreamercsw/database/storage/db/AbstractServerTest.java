package com.github.javydreamercsw.database.storage.db;

import org.testng.annotations.BeforeClass;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.GameService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;

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
  public void setup()
  {
    DataBaseManager.setPersistenceUnitName("TestTMPU");
    MatchService.getInstance().getAll().forEach(match
            -> MatchService.getInstance().deleteMatch(match));
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
