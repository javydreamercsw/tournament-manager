/*
 * This interfaces exposes the API to read/write tournaments.
 */
package net.sourceforge.javydreamercsw.tournament.manager.api.storage;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface StorageInterface {

    /**
     * Save tournament.
     *
     * @param ti tournament to save
     * @throws StorageException
     */
    public void saveTournament(TournamentInterface ti) throws StorageException;

    /**
     * Save tournament.
     *
     * @param id tournament id to load
     * @return Loaded tournament
     * @throws StorageException
     */
    public TournamentInterface loadTournament(int id) throws StorageException;

    /**
     * Initialize the storage.
     */
    public void initialize();

    /**
     * Check if storage is initialized.
     *
     * @return true if storage is initialized
     */
    public boolean isInitialized();
}
