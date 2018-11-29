package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;

import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;

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
    System.out.println("loadDemoData");
    //Load demo data
    DataBaseManager.loadDemoData();

    assertFalse(PlayerService.getInstance().getAll().isEmpty());
    assertFalse(TournamentService.getInstance().getAll().isEmpty());
    assertFalse(MatchService.getInstance().getAll().isEmpty());
    assertFalse(TeamService.getInstance().getAll().isEmpty());
    assertFalse(RecordService.getInstance().getAll().isEmpty());

    MatchService.getInstance().getAll().forEach(me ->
    {
      assertEquals(me.getMatchHasTeamList().size(), 2);
      assertNotNull(me.getFormat());
    });
  }
}
