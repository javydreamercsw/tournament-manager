package com.github.javydreamercsw.database.storage.db.server;


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.TestHelper;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchServerTest extends AbstractServerTest
{

  public MatchServerTest()
  {
  }

  /**
   * Test of write2DB method, of class MatchServer.
   *
   * @throws
   * com.github.javydreamercsw.tournament.manager.api.TournamentException
   */
  @Test
  public void testWrite2DB() throws TournamentException
  {
    System.out.println("write2DB");
    TournamentServer tm = TestHelper.createTournament("Test 1");
    tm.write2DB();
    RoundServer r = TestHelper.createRound(tm.getEntity());
    r.write2DB();
    FormatServer f = TestHelper.createFormat("Default");
    f.write2DB();
    MatchServer instance = new MatchServer(r.getEntity(), f.getEntity());
    assertTrue(instance.write2DB() > 0);
  }
}
