package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.MissingResourceException;

import org.openide.util.NbBundle;
import org.testng.annotations.Test;

public class BundleTest
{
  @Test
  public void testBundle()
  {
    assertNotNull(NbBundle.getMessage(DataBaseManager.class, "message.db.locked"));

    try
    {
      NbBundle.getMessage(DataBaseManager.class, "dummy");
      fail("Expected MissingResourceException!");
    }
    catch (MissingResourceException ex)
    {
      // Expected
    }
  }
}
