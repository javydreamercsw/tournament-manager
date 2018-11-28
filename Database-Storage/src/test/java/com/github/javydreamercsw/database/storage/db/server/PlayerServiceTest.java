package com.github.javydreamercsw.database.storage.db.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Player;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class PlayerServiceTest extends AbstractServerTest
{
  
   @BeforeClass
  @Override
  public void setup()
  {
    try
    {
      super.setup();
      System.out.println("Creating players...");
      PlayerService.getInstance().findPlayers("").forEach(player ->
      {
        PlayerService.getInstance().deletePlayer(player);
      });
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
   * Test of findPlayers method, of class PlayerService.
   */
  @Test
  public void testFindPlayers()
  {
    System.out.println("findPlayers");
    assertEquals(2,
            PlayerService.getInstance().findPlayers("").size());
  }

  /**
   * Test of findPlayerByName method, of class PlayerService.
   */
  @Test
  public void testFindPlayersByName()
  {
    System.out.println("findPlayerByName");
    assertNotNull(PlayerService.getInstance().findPlayerByName("Player 1").get());
    assertFalse(PlayerService.getInstance().findPlayerByName("Player").isPresent());
  }

  /**
   * Test of findNameOrThrow method, of class PlayerService.
   */
  @Test
  public void testFindNameOrThrow()
  {
    System.out.println("findNameOrThrow");
    assertNotNull(PlayerService.getInstance().findNameOrThrow("Player 1"));
    try
    {
      assertNotNull(PlayerService.getInstance().findNameOrThrow("Player"));
      fail("Expected failure!");
    }
    catch (IllegalStateException ex)
    {
      //Expected
    }
  }

  /**
   * Test of findPlayerById method, of class PlayerService.
   */
  @Test
  public void testFindPlayerById()
  {
    System.out.println("findPlayerById");
    Player p = PlayerService.getInstance().findPlayerByName("Player 1").get();
    assertNotNull(p);
    assertNotNull(PlayerService.getInstance().findPlayerById(p.getId()).get());

    assertFalse(PlayerService.getInstance().findPlayerById(100).isPresent());
  }
}
