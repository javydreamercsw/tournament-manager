package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.*;

import java.util.List;
import java.util.Optional;

import org.openide.util.Lookup;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.IGame;

public class FormatServiceTest extends AbstractServerTest
{
  private Game game;

  @BeforeClass
  @Override
  public void setup() throws NonexistentEntityException, IllegalOrphanException, 
          Exception
  {
    super.setup();
    game = GameService.getInstance().findGameByName(Lookup.getDefault()
            .lookup(IGame.class).getName()).get();
  }

  /**
   * Test of findFormats method, of class FormatService.
   */
  @Test
  public void testFindFormats()
  {
    assertFalse(FormatService.getInstance().getAll().isEmpty());
  }

  /**
   * Test of findFormatByName method, of class FormatService.
   */
  @Test
  public void testFindFormatByName()
  {
    assertEquals(1, FormatService.getInstance()
            .findFormats(game.getFormatList().get(0).getName()).size());
    assertEquals(0, FormatService.getInstance()
            .findFormats(game.getFormatList().get(0).getName() + "2").size());
  }

  /**
   * Test of findFormatOrThrow method, of class FormatService.
   */
  @Test
  public void testFindFormatOrThrow()
  {
    assertNotNull(FormatService.getInstance()
            .findFormatOrThrow(game.getFormatList().get(0).getName()));
    try
    {
      FormatService.getInstance()
              .findFormatOrThrow(game.getFormatList().get(0).getName() + "2");
      fail("Expected exception!");
    }
    catch (IllegalStateException ex)
    {
      //Expected
    }
  }

  /**
   * Test of findFormatById method, of class FormatService.
   */
  @Test
  public void testFindFormatById()
  {
    assertNotNull(FormatService.getInstance()
            .findFormatById(FormatService.getInstance()
                    .findFormatByName(game.getFormatList().get(0).getName()).get()
                    .getFormatPK()));
  }

  /**
   * Test of deleteFormat method, of class FormatService.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testDeleteFormat() throws Exception
  {
    Format format = new Format("test");
    format.setGame(game);
    assertEquals(FormatService.getInstance().findFormats("test").size(), 0);
    FormatService.getInstance().saveFormat(format);
    assertEquals(FormatService.getInstance().findFormats("test").size(), 1);
    FormatService.getInstance().deleteFormat(FormatService.getInstance()
            .findFormatByName(format.getName()).get());
    assertEquals(FormatService.getInstance().findFormats("test").size(), 0);
  }

  /**
   * Test of findFormatForGame method, of class FormatService.
   */
  @Test
  public void testFindFormatForGame()
  {
    Optional<Format> result = FormatService.getInstance().findFormatForGame(game.getName(),
            game.getFormatList().get(0).getName());
    assertTrue(result.isPresent());

    result = FormatService.getInstance().findFormatForGame(game.getName(),
            game.getFormatList().get(0).getName() + "x");
    assertFalse(result.isPresent());

    result = FormatService.getInstance().findFormatForGame(game.getName() + "x",
            game.getFormatList().get(0).getName());
    assertFalse(result.isPresent());
  }

  /**
   * Test of findFormatByGame method, of class FormatService.
   */
  @Test
  public void testFindFormatByGame()
  {
    List<Format> result = FormatService.getInstance().findFormatByGame(game);
    assertFalse(result.isEmpty());

    result = FormatService.getInstance().findFormatByGame(game.getName() + "2");
    assertTrue(result.isEmpty());
  }
}
