package net.sourceforge.javydreamercsw.database.storage.db.server;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface DatabaseEntity<T> {

    /**
     * Update database.
     *
     * @return new id
     */
    int write2DB();

    /**
     * Update the entity
     *
     * @param target
     * @param source
     */
    void update(T target, T source);
}
