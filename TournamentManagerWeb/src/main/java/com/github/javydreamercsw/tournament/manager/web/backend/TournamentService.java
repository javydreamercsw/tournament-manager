package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.Collections;
import java.util.List;

import net.sourceforge.javydreamercsw.database.storage.db.Tournament;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class TournamentService
{
  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final TournamentService INSTANCE = createTournamentService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static TournamentService createTournamentService()
    {
      TournamentService service = new TournamentService();

      return service;
    }
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static TournamentService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public List<Tournament> findTournaments(String value)
  {
    //TODO
    return Collections.EMPTY_LIST;
  }

  public void saveTournament(Tournament t)
  {
    //TODO
  }

  public void deleteTournament(Tournament t)
  {
    //TODO
  }

  public List<Tournament> findTournament(Integer id)
  {
    //TODO
    return Collections.EMPTY_LIST;
  }
}
