package com.github.javydreamercsw.tournament.manager.web.backend;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import net.sourceforge.javydreamercsw.database.storage.db.Player;

public class PlayerServiceNGTest extends BaseTestCase
{
  @BeforeClass
  @Override
  public void setup()
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
