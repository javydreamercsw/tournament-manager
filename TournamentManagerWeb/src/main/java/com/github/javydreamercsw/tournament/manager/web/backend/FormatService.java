package com.github.javydreamercsw.tournament.manager.web.backend;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Simple backend service to store and retrieve {@link Format} instances.
 */
public class FormatService {

    /**
     * Helper class to initialize the singleton Service in a thread-safe way
     * and to keep the initialization ordering clear between the two services.
     * See also:
     * https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
     */
    private static class SingletonHolder {
        static final FormatService INSTANCE = createCategoryService();

        /** This class is not meant to be instantiated. */
        private SingletonHolder() {
        }

        private static FormatService createCategoryService() {
            FormatService categoryService = new FormatService();

            StaticData.FORMATS.forEach(name -> 
                    categoryService.saveFormat(new Format(name)));

            return categoryService;
        }
    }

    private Map<Long, Format> formats = new HashMap<>();
    private AtomicLong nextId = new AtomicLong(0);

    /**
     * Declared private to ensure uniqueness of this Singleton.
     */
    private FormatService() {
    }

    /**
     * Gets the unique instance of this Singleton.
     * @return  the unique instance of this Singleton
     */
    public static FormatService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Fetches the formats whose name matches the given filter text.
     *
     * The matching is case insensitive. When passed an empty filter text,
     * the method returns all formats. The returned list is ordered
     * by name.
     *
     * @param filter    the filter text
     * @return          the list of matching formats
     */
    public List<Format> findFormats(String filter) {
        String normalizedFilter = filter.toLowerCase();

        // Make a copy of each matching item to keep entities and DTOs separated
        return formats.values().stream()
                .filter(c -> c.getName().toLowerCase().contains(normalizedFilter))
                .map(Format::new)
                .sorted((c1, c2) -> c1.getName().compareToIgnoreCase(c2.getName()))
                .collect(Collectors.toList());
    }

    /**
     * Searches for the exact category whose name matches the given filter text.
     *
     * The matching is case insensitive.
     *
     * @param name  the filter text
     * @return      an {@link Optional} containing the category if found,
     *              or {@link Optional#empty()}
     * @throws IllegalStateException    if the result is ambiguous
     */
    public Optional<Format> findFormatByName(String name) {
        List<Format> formatsMatching = findFormats(name);

        if (formatsMatching.isEmpty()) {
            return Optional.empty();
        }
        if (formatsMatching.size() > 1) {
            throw new IllegalStateException("Format " + name + " is ambiguous");
        }
        return Optional.of(formatsMatching.get(0));
    }

    /**
     * Fetches the exact category whose name matches the given filter text.
     *
     * Behaves like {@link #findFormatByName(String)}, except that returns
     * a {@link Format} instead of an {@link Optional}. If the category
     * can't be identified, an exception is thrown.
     *
     * @param name  the filter text
     * @return      the category, if found
     * @throws IllegalStateException    if not exactly one category matches the given name
     */
    public Format findFormatOrThrow(String name) {
        return findFormatByName(name)
                .orElseThrow(() -> new IllegalStateException("Format " + 
                        name + " does not exist"));
    }

    /**
     * Searches for the exact category with the given id.
     *
     * @param id    the category id
     * @return      an {@link Optional} containing the category if found,
     *              or {@link Optional#empty()}
     */
    public Optional<Format> findCategoryById(Long id) {
        Format category = formats.get(id);
        return Optional.ofNullable(category);
    }

    /**
     * Deletes the given category from the category store.
     * @param category  the category to delete
     * @return  true if the operation was successful, otherwise false
     */
    public boolean deleteFormat(Format category) {
        return formats.remove(category.getId()) != null;
    }

    /**
     * Persists the given category into the category store.
     *
     * If the category is already persistent, the saved category will get updated
     * with the name of the given category object.
     * If the category is new (i.e. its id is null), it will get a new unique id
     * before being saved.
     *
     * @param dto   the category to save
     */
    public void saveFormat(Format dto) {
        Format entity = formats.get(dto.getId());

        if (entity == null) {
            // Make a copy to keep entities and DTOs separated
            entity = new Format(dto);
            if (dto.getId() == null) {
                entity.setId(nextId.incrementAndGet());
            }
            formats.put(entity.getId(), entity);
        } else {
            entity.setName(dto.getName());
        }
    }

}
