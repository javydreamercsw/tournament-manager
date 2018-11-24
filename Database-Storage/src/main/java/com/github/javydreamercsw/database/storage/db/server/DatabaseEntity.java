package com.github.javydreamercsw.database.storage.db.server;

import com.github.javydreamercsw.tournament.manager.api.storage.StorageException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 * @param <T>
 */
public interface DatabaseEntity<T> {

    /**
     * Update database.
     *
     * @return new id
     * @throws StorageException
     */
    int write2DB() throws StorageException;

    /**
     * Update the entity
     *
     * @param target
     * @param source
     */
    void update(T target, T source);

    /**
     * Get the underlying entity
     *
     * @return
     */
    T getEntity();
}
