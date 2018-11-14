package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TournamentJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class TournamentService
{
  private TournamentJpaController controller
          = new TournamentJpaController(DataBaseManager.getEntityManagerFactory());

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
    return controller.findTournamentEntities();
  }

  public void saveTournament(Tournament t)
  {
    controller.create(t);
  }

  public void deleteTournament(Tournament t)
  {
    try
    {
      controller.destroy(t.getId());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  public List<Tournament> findTournament(Integer id)
  {
    List<Tournament> results = new ArrayList<>();
    Tournament t = controller.findTournament(id);
    if (t != null)
    {
      results.add(t);
    }
    return results;
  }
}
