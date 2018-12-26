package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openide.util.Lookup;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.tournament.manager.api.IGame;

public class DataBaseManagerTest extends AbstractServerTest
{
  /**
   * Test of loadDemoData method, of class DataBaseManager.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testLoadDemoData() throws Exception
  {
    cleanDB();
    //Load demo data
    DataBaseManager.loadDemoData();

    assertFalse(PlayerService.getInstance().getAll().isEmpty());
    assertFalse(TournamentService.getInstance().getAll().isEmpty());
    assertFalse(TournamentService.getInstance().getAllFormats().isEmpty());
    assertFalse(FormatService.getInstance().getAll().isEmpty());
    assertFalse(MatchService.getInstance().getAll().isEmpty());
    assertFalse(TeamService.getInstance().getAll().isEmpty());
    assertFalse(RecordService.getInstance().getAll().isEmpty());

    MatchService.getInstance().getAll().forEach(me ->
    {
      assertEquals(me.getMatchHasTeamList().size(), 2);
      assertNotNull(me.getFormat());
    });

    List<Object> results = DataBaseManager.namedQuery("MatchEntry.findAll");
    assertTrue(results.size() > 0);

    results.forEach(result ->
    {
      MatchEntry m = (MatchEntry) result;
      assertEquals(m.getMatchHasTeamList().size(), 2);
      assertNotNull(m.getFormat());
    });

    results = DataBaseManager.namedQuery("TeamHasFormatRecord.findAll");
    assertTrue(results.size() > 0);

    results.forEach(result ->
    {
      TeamHasFormatRecord m = (TeamHasFormatRecord) result;
      assertNotNull(m.getTeam());
      assertNotNull(m.getFormat());
    });

    FormatService.getInstance().getAll().forEach(format ->
    {
      if (format.getMatchEntryList().size() > 0)
      {
        assertTrue(format.getTeamHasFormatRecordList().size() > 0);
      }
    });
  }

  @Test
  public void testNamedQuery()
  {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("name", Lookup.getDefault().lookup(IGame.class).getName());

    assertFalse(DataBaseManager.namedQuery("Game.findByName", parameters).isEmpty());
  }
}
