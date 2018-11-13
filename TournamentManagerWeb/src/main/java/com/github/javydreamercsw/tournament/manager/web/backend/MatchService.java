package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.javydreamercsw.database.storage.db.MatchEntry;
import net.sourceforge.javydreamercsw.database.storage.db.MatchEntryPK;
import net.sourceforge.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class MatchService
{
  private MatchEntryJpaController matchController
          = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final MatchService INSTANCE = createMatchService();
    private static Map<Integer, MatchEntry> matches = new HashMap<>();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static MatchService createMatchService()
    {
      final MatchService reviewService = new MatchService();
      MatchEntryJpaController c = new MatchEntryJpaController(DataBaseManager
              .getEntityManagerFactory());
      c.findMatchEntryEntities().forEach(match ->
      {
        matches.put(match.getMatchEntryPK().getId(), match);
      });
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
    matchController.create(match);
  }
  
  public void deleteMatch(MatchEntry match) throws Exception
  {
    matchController.destroy(match.getMatchEntryPK());
  }

  public List<MatchEntry> findMatchesWithFormat(String format)
  {
    List<MatchEntry> results = new ArrayList<>();
    HashMap<String, Object> parameters = new HashMap<>();
    parameters.put("name", format);
    List<Object> result
            = DataBaseManager.namedQuery("Format.findByName", parameters);
    result.forEach(r -> results.add((MatchEntry) r));
    return results;
  }

  public List<MatchEntry> findMatch(MatchEntryPK key)
  {
    List<MatchEntry> results = new ArrayList<>();
    MatchEntryJpaController c = new MatchEntryJpaController(DataBaseManager
            .getEntityManagerFactory());
    MatchEntry match = c.findMatchEntry(key);
    if (match != null)
    {
      results.add(match);
    }
    return results;
  }
  
  public List<MatchEntry> findMatches(String value)
  {
    return matchController.findMatchEntryEntities();
  }
}
