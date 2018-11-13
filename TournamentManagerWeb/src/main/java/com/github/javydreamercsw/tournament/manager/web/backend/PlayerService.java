package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;

import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.controller.PlayerJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;
import net.sourceforge.javydreamercsw.tournament.manager.UIPlayer;

/**
 * Simple backend service to store and retrieve {@link UIPlayer} instances.
 */
public class PlayerService
{
  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final PlayerService INSTANCE = createPlayerService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static PlayerService createPlayerService()
    {
      PlayerService service = new PlayerService();

      return service;
    }
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static PlayerService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Fetches the players whose name matches the given filter text.
   *
   * The matching is case insensitive. When passed an empty filter text, the
   * method returns all players. The returned list is ordered by name.
   *
   * @param filter the filter text
   * @return the list of matching players
   */
  public List<Player> findPlayers(String filter)
  {
    String normalizedFilter = filter.toLowerCase();

    // Make a copy of each matching item to keep entities and DTOs separatedPlayerJpaController c =
    PlayerJpaController c
            = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
    List<Player> results = new ArrayList<>();
    c.findPlayerEntities().forEach(player ->
    {
      if (player.getName().contains(normalizedFilter))
      {
        results.add(player);
      }
    });
    return results;
  }

  /**
   * Searches for the exact category whose name matches the given filter text.
   *
   * The matching is case insensitive.
   *
   * @param name the filter text
   * @return an {@link Optional} containing the category if found, or
   * {@link Optional#empty()}
   * @throws IllegalStateException if the result is ambiguous
   */
  public Optional<Player> findPlayersByName(String name)
  {
    List<Player> playersMatching = findPlayers(name);

    if (playersMatching.isEmpty())
    {
      return Optional.empty();
    }
    if (playersMatching.size() > 1)
    {
      throw new IllegalStateException("Player " + name + " is ambiguous");
    }
    return Optional.of(playersMatching.get(0));
  }

  /**
   * Fetches the exact place whose name matches the given filter text.
   *
   * Behaves like {@link #findPlayersByName(String)}, except that returns a
   * {@link UIPlayer} instead of an {@link Optional}. If the category can't be
   * identified, an exception is thrown.
   *
   * @param name the filter text
   * @return the category, if found
   * @throws IllegalStateException if not exactly one category matches the given
   * name
   */
  public Player findNameOrThrow(String name)
  {
    return findPlayersByName(name)
            .orElseThrow(() -> new IllegalStateException("Player " + name
            + " does not exist"));
  }

  /**
   * Searches for the exact UIPlayer with the given id.
   *
   * @param id the category id
   * @return an {@link Optional} containing the category if found, or
   * {@link Optional#empty()}
   */
  public Optional<Player> findPlayerById(Integer id)
  {
    PlayerJpaController c
            = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
    return Optional.ofNullable(c.findPlayer(id));
  }

  /**
   * Deletes the given player from the player store.
   *
   * @param player the player to delete
   */
  public void deletePlayer(Player player)
  {
    try
    {
      PlayerJpaController c
              = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
      c.destroy(player.getId());
    }
    catch (NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Persists the given player into the player store.
   *
   * If the category is already persistent, the saved category will get updated
   * with the name of the given category object. If the category is new (i.e.
   * its id is null), it will get a new unique id before being saved.
   *
   * @param player the category to save
   */
  public void savePlayer(Player player)
  {
    if (player != null)
    {
      // Make a copy to keep entities and DTOs separated
      PlayerJpaController c
              = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
      c.create(player);
    }
  }
}
