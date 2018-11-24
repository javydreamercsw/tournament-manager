package com.github.javydreamercsw.tournament.manager.web.backend;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;

public class BaseTestCase
{
  @BeforeClass
  public void setup()
  {
    DataBaseManager.setPersistenceUnitName("TestTMPU");
    cleanup();
  }
  
  @AfterClass
  public void cleanup()
  {
    MatchService.getInstance().findMatches().forEach(match ->
    {
      MatchService.getInstance().deleteMatch(match);
    });
    TeamService.getInstance().findTeams("").forEach(team ->
    {
      TeamService.getInstance().deleteTeam(team);
    });
    PlayerService.getInstance().findPlayers("").forEach(player ->
    {
      PlayerService.getInstance().deletePlayer(player);
    });
    FormatService.getInstance().findFormats("").forEach(format ->
    {
      FormatService.getInstance().deleteFormat(format);
    });
  }
}
