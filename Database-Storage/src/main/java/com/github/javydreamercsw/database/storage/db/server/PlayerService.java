package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.PlayerJpaController;
import com.github.javydreamercsw.database.storage.db.controller.RecordJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.UIPlayer;

/**
 * Simple backend service to store and retrieve {@link UIPlayer} instances.
 */
public class PlayerService extends Service<Player>
{
  private final PlayerJpaController pc
          = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());

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
    List<Player> results = new ArrayList<>();
    if (filter == null || filter.trim().isEmpty())
    {
      results.addAll(pc.findPlayerEntities());
    }
    else
    {
      String normalizedFilter = filter.toLowerCase();
      pc.findPlayerEntities().forEach(player ->
      {
        if (player.getName().toLowerCase().contains(normalizedFilter))
        {
          results.add(player);
        }
      });
    }
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
  public Optional<Player> findPlayerByName(String name)
  {
    List<Player> playersMatching = findPlayers(name);

    if (playersMatching.isEmpty())
    {
      return Optional.empty();
    }
    if (playersMatching.size() > 1)
    {
      return Optional.empty();
    }
    return Optional.of(playersMatching.get(0));
  }

  /**
   * Fetches the exact place whose name matches the given filter text.
   *
   * Behaves like {@link #findPlayerByName(String)}, except that returns a
   * {@link UIPlayer} instead of an {@link Optional}. If the category can't be
   * identified, an exception is thrown.
   *
   * @param name the filter text
   * @return the category, if found
   * @throws IllegalStateException if not exactly one category matches the given
   * name
   */
  public Player findNameOrThrow(String name) throws IllegalStateException
  {
    return findPlayerByName(name)
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
    return Optional.ofNullable(pc.findPlayer(id));
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
      RecordJpaController rc
              = new RecordJpaController(DataBaseManager.getEntityManagerFactory());
      player.getTeamList().forEach((team) ->
      {
        TeamService.getInstance().deleteTeam(team);
      });
      for (Record r : player.getRecordList())
      {
        rc.destroy(r.getRecordPK());
      }
      pc.destroy(player.getId());
    }
    catch (NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Persists the given player into the player store.If the category is already
   * persistent, the saved category will get updated with the name of the given
   * category object.
   *
   * If the category is new (i.e. its id is null), it will get a new unique id
   * before being saved.
   *
   * @param player the category to save
   * @throws java.lang.Exception
   */
  public void savePlayer(Player player) throws Exception
  {
    if (player.getId() != null && pc.findPlayer(player.getId()) != null)
    {
      try
      {
        pc.edit(player);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      pc.create(player);

      //Create the single player's team
      Team alone = new Team();
      alone.setName(player.getName());
      alone.getPlayerList().add(player);
      TeamService.getInstance().saveTeam(alone);
    }
  }

  @Override
  public List<Player> getAll()
  {
    return pc.findPlayerEntities();
  }

  /**
   * Convert a player to a UIPlayer.
   * @param p Player to transform
   * @return transformed player.
   */
  public UIPlayer convertToUIPlayer(Player p)
  {
    return new UIPlayer(p.getName(), p.getId());
  }
}
