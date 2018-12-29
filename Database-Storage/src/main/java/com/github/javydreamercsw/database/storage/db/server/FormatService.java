package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.FormatPK;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.controller.FormatJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

/**
 * Simple backend service to store and retrieve {@link Format} instances.
 */
public class FormatService extends Service<Format>
{
  private FormatJpaController fc
          = new FormatJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final FormatService INSTANCE = createCategoryService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static FormatService createCategoryService()
    {
      FormatService formatService = new FormatService();
      return formatService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private FormatService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static FormatService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Fetches the formats whose name matches the given filter text.
   *
   * The matching is case insensitive. When passed an empty filter text, the
   * method returns all formats. The returned list is ordered by name.
   *
   * @param filter the filter text
   * @return the list of matching formats
   */
  public List<Format> findFormats(String filter)
  {
    final String normalizedFilter = filter.toLowerCase();
    List<Format> results = new ArrayList<>();

    // Must be for tests or other low level checks.
    fc.findFormatEntities().forEach(format ->
    {
      if (format.getName().toLowerCase().contains(normalizedFilter))
      {
        results.add(format);
      }
    });
    return results;
  }

  /**
   * Searches for the exact category whose name matches the given filter text.
   * The matching is case insensitive.
   *
   * @param game Game to search for
   * @return a list of formats for the specified game.
   * @throws IllegalStateException if the result is ambiguous
   */
  public List<Format> findFormatByGame(String game)
  {
    ArrayList<Format> result = new ArrayList<>();

    if (game != null)
    {
      Optional<Game> g = GameService.getInstance().findGameByName(game);
      if (g.isPresent())
      {
        result.addAll(g.get().getFormatList());
      }
    }

    return result;
  }

  /**
   * Searches for the exact category whose name matches the given filter
   * text.The matching is case insensitive.
   *
   *
   * @param game Game to search for
   * @return a list of formats for the specified game.
   * @throws IllegalStateException if the result is ambiguous
   */
  public List<Format> findFormatByGame(Game game)
  {
    ArrayList<Format> result = new ArrayList<>();

    if (game != null)
    {
      result.addAll(findFormatByGame(game.getName()));
    }
    return result;
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
  public Optional<Format> findFormatByName(String name)
  {
    List<Format> formatsMatching = findFormats(name);

    if (formatsMatching.isEmpty())
    {
      return Optional.empty();
    }
    if (formatsMatching.size() > 1)
    {
      throw new IllegalStateException("Format " + name + " is ambiguous");
    }
    return Optional.of(formatsMatching.get(0));
  }

  /**
   * Fetches the exact category whose name matches the given filter text.
   *
   * Behaves like {@link #findFormatByName(String)}, except that returns a
   * {@link Format} instead of an {@link Optional}. If the category can't be
   * identified, an exception is thrown.
   *
   * @param name the filter text
   * @return the category, if found
   * @throws IllegalStateException if not exactly one category matches the given
   * name
   */
  public Format findFormatOrThrow(String name)
  {
    return findFormatByName(name)
            .orElseThrow(() -> new IllegalStateException("Format "
            + name + " does not exist"));
  }

  /**
   * Searches for the exact category with the given id.
   *
   * @param id the category id
   * @return an {@link Optional} containing the category if found, or
   * {@link Optional#empty()}
   */
  public Optional<Format> findFormatById(FormatPK id)
  {
    return Optional.ofNullable(fc.findFormat(id));
  }

  /**
   * Deletes the given category from the category store.
   *
   * @param format the category to delete
   */
  public void deleteFormat(Format format)
  {
    try
    {
      fc.destroy(format.getFormatPK());
    }
    catch (IllegalOrphanException ex)
    {
      Exceptions.printStackTrace(ex);
    }
    catch (NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Persists the given format into the format store.
   *
   * @param format the format to save
   * @throws java.lang.Exception
   */
  public void saveFormat(Format format) throws Exception
  {
    if (format.getFormatPK() != null
            && fc.findFormat(format.getFormatPK()) != null)
    {
      fc.edit(format);
    }
    else
    {
      fc.create(format);
    }
  }

  public Optional<Format> findFormatForGame(String game, String format)
  {
    Format result = null;
    for (Format f : findFormatByGame(game))
    {
      if (f.getName().equals(format))
      {
        result = f;
        break;
      }
    }
    if (result == null)
    {
      return Optional.empty();
    }
    else
    {
      return Optional.of(result);
    }
  }

  @Override
  public List<Format> getAll()
  {
    return fc.findFormatEntities();
  }
}
