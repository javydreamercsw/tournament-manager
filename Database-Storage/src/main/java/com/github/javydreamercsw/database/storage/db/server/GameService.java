package com.github.javydreamercsw.database.storage.db.server;

import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.controller.GameJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

public class GameService extends Service<Game>
{
  private final GameJpaController gc
          = new GameJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final GameService INSTANCE = createGameService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static GameService createGameService()
    {
      GameService gameService = new GameService();
      return gameService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private GameService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static GameService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public Optional<Game> findGameByName(String name)
  {
    Game game = null;
    for (Game g : gc.findGameEntities())
    {
      if (g.getName().equals(name))
      {
        game = g;
        break;
      }
    }
    return Optional.ofNullable(game);
  }

  /**
   * Save the game.
   *
   * @param game
   */
  public void saveGame(Game game)
  {
    try
    {
      if (game.getId() == null)
      {
        gc.create(game);
      }
      else
      {
        gc.edit(game);

      }
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  @Override
  public List<Game> getAll()
  {
    return gc.findGameEntities();
  }

  public void deleteGame(Game game)
  {
    try
    {
      gc.destroy(game.getId());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }
}
