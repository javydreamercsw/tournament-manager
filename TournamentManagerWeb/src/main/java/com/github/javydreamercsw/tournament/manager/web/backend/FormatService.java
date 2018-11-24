package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;

import com.github.javydreamercsw.database.storage.db.controller.FormatJpaController;

import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 * Simple backend service to store and retrieve {@link Format} instances.
 */
public class FormatService
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
  public Optional<Format> findFormatById(Integer id)
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
      fc.destroy(format.getId());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Persists the given format into the format store.
   *
   * @param format the format to save
   */
  public void saveFormat(Format format)
  {
    if (format.getId() != null && fc.findFormat(format.getId()) != null)
    {
      try
      {
        fc.edit(format);
      }
      catch (NonexistentEntityException ex)
      {
        Exceptions.printStackTrace(ex);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      fc.create(format);
    }
  }
}
