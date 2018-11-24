package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;

import com.github.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;

import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class MatchService
{
  private MatchEntryJpaController mc
          = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final MatchService INSTANCE = createMatchService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static MatchService createMatchService()
    {
      final MatchService reviewService = new MatchService();
      return reviewService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private MatchService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static MatchService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public void saveMatch(MatchEntry match) throws Exception
  {
    if (match.getMatchEntryPK() != null
            && mc.findMatchEntry(match.getMatchEntryPK()) != null)
    {
      mc.edit(match);
    }
    else
    {
      mc.create(match);
    }
  }

  public void deleteMatch(MatchEntry match)
  {
    try
    {
      mc.destroy(match.getMatchEntryPK());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  public List<MatchEntry> findMatchesWithFormat(String format)
  {
    List<MatchEntry> results = new ArrayList<>();
    mc.findMatchEntryEntities().forEach(match ->
    {
      if (match.getFormat().getName().equals(format))
      {
        results.add(match);
      }
    });
    return results;
  }

  public List<MatchEntry> findMatch(MatchEntryPK key)
  {
    List<MatchEntry> results = new ArrayList<>();
    MatchEntry match = mc.findMatchEntry(key);
    if (match != null)
    {
      results.add(match);
    }
    return results;
  }

  public List<MatchEntry> findMatches()
  {
    return mc.findMatchEntryEntities();
  }
}
