package com.github.javydreamercsw.database.storage.db.server;


import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.stream.Stream;

import org.openide.util.Exceptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;

public class FormatServiceTest extends AbstractServerTest
{
  private Game game = new Game("Test");

  @BeforeClass
  @Override
  public void setup()
  {
    super.setup();
    GameService.getInstance().saveGame(game);
    Stream.of("Commander",
            "Beatdown",
            "Legacy",
            "Standard",
            "Draft")
            .forEach(name ->
            {
              if (!FormatService.getInstance().findFormatByName(name).isPresent())
              {
                try
                {
                  Format format = new Format(name);
                  format.setGame(game);
                  FormatService.getInstance().saveFormat(format);
                }
                catch (Exception ex)
                {
                  Exceptions.printStackTrace(ex);
                  fail();
                }
              }
            });
  }

  /**
   * Test of findFormats method, of class FormatService.
   */
  @Test
  public void testFindFormats()
  {
    System.out.println("findFormats");
    assertFalse(FormatService.getInstance().findFormats("").isEmpty());
  }

  /**
   * Test of findFormatByName method, of class FormatService.
   */
  @Test
  public void testFindFormatByName()
  {
    System.out.println("findFormatByName");
    assertEquals(1, FormatService.getInstance().findFormats("Commander").size());

    assertEquals(0, FormatService.getInstance().findFormats("Commander2").size());
  }

  /**
   * Test of findFormatOrThrow method, of class FormatService.
   */
  @Test
  public void testFindFormatOrThrow()
  {
    System.out.println("findFormatOrThrow");
    assertNotNull(FormatService.getInstance().findFormatOrThrow("Commander"));

    try
    {
      FormatService.getInstance().findFormatOrThrow("Commander2");
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
    System.out.println("findCategoryById");
    assertNotNull(FormatService.getInstance()
            .findFormatById(FormatService.getInstance()
                    .findFormatByName("Commander").get().getFormatPK()));
  }

  /**
   * Test of deleteFormat method, of class FormatService.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testDeleteFormat() throws Exception
  {
    System.out.println("deleteFormat");
    Format format = new Format("test");
    format.setGame(game);
    assertEquals(FormatService.getInstance().findFormats("test").size(), 0);
    FormatService.getInstance().saveFormat(format);
    assertEquals(FormatService.getInstance().findFormats("test").size(), 1);
    FormatService.getInstance().deleteFormat(FormatService.getInstance()
            .findFormatByName(format.getName()).get());
    assertEquals(FormatService.getInstance().findFormats("test").size(), 0);
  }
}
