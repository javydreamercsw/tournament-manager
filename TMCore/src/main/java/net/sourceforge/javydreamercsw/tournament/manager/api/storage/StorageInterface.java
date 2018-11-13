/*
 * This interfaces exposes the API to read/write tournaments.
 */
package net.sourceforge.javydreamercsw.tournament.manager.api.storage;

import de.gesundkrank.jskills.IPlayer;
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
     * @throws StorageException
     */
    public void loadTournament(int id) throws StorageException;

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

  /**
   * Add a player
   *
   * @param player player to add.
   * @return id of the created player.
   */
  public int addPlayer(IPlayer player);

  /**
   * Retrieve a player by id.
   *
   * @param id ID to retrieve.
   * @return Player or null if it doesn't exist.
   */
  public IPlayer getPlayer(int id);
}
