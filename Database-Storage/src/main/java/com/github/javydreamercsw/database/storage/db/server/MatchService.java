package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchHasTeamPK;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultTypeJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

public class MatchService extends Service<MatchEntry>
{
  private MatchEntryJpaController mc
          = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchHasTeamJpaController mhtc
          = new MatchHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultJpaController mrc
          = new MatchResultJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultTypeJpaController mrtc
          = new MatchResultTypeJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final MatchService INSTANCE = createMatchService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static MatchService createMatchService()
    {
      final MatchService reviewService = new MatchService();
      return reviewService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private MatchService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static MatchService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Save a match to the database.
   *
   * @param match Match to save.
   * @throws Exception if something goes wrong with the database.
   */
  public void saveMatch(MatchEntry match) throws Exception
  {
    if (match.getMatchEntryPK() != null
            && mc.findMatchEntry(match.getMatchEntryPK()) != null)
    {
      mc.edit(match);
    }
    else
    {
      mc.create(match);
    }
  }

  /**
   * Delete a match from the database.
   *
   * @param match Match to delete.
   * @throws NonexistentEntityException if match doesn't exist
   * @throws IllegalOrphanException if an entity was left orphan.
   */
  public void deleteMatch(MatchEntry match) throws NonexistentEntityException,
          IllegalOrphanException
  {
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      mhtc.destroy(mht.getMatchHasTeamPK());
    }
    mc.destroy(match.getMatchEntryPK());
  }

  /**
   * Find matches of the specified format.
   *
   * @param format Format name to look for.
   * @return List of matches with the specified format.
   */
  public List<MatchEntry> findMatchesWithFormat(String format)
  {
    List<MatchEntry> results = new ArrayList<>();
    mc.findMatchEntryEntities().forEach(match ->
    {
      if (format.trim().isEmpty()
              || match.getFormat().getName().equals(format))
      {
        results.add(match);
      }
    });
    return results;
  }

  /**
   * Find a match by db key.
   *
   * @param key Key for the match.
   * @return Match or null if not found.
   */
  public MatchEntry findMatch(MatchEntryPK key)
  {
    return mc.findMatchEntry(key);
  }

  /**
   * Add a team to a match.
   *
   * @param match Match to be added to.
   * @param team Team to add
   * @return true if added. False if team already in the match or was unable to
   * be added.
   * @throws Exception persisting to data base.
   */
  public boolean addTeam(MatchEntry match, Team team) throws Exception
  {
    // Check the team is not in this match already
    if (!match.getMatchHasTeamList().stream().noneMatch((mht)
            -> (Objects.equals(mht.getTeam().getId(), team.getId()))))
    {
      return false;
    }
    MatchHasTeam mht = new MatchHasTeam();
    mht.setTeam(team);
    mht.setMatchEntry(match);
    if (match.getMatchEntryPK() != null)
    {
      mht.setMatchHasTeamPK(new MatchHasTeamPK(team.getId(),
              match.getMatchEntryPK().getId(),
              match.getMatchEntryPK().getFormatId(),
              match.getFormat().getGame().getId()));
    }

    if (match.getMatchEntryPK() != null)
    {
      mhtc.create(mht);
    }

    match.getMatchHasTeamList().add(mht);

    // Make sure each team member has a record for this game. Add one otherwise.
    team.getPlayerList().forEach(player ->
    {
      boolean found = false;
      for (Record r : player.getRecordList())
      {
        if (Objects.equals(r.getGame().getId(), 
                match.getFormat().getGame().getId()))
        {
          found = true;
          break;
        }
      }
      if (!found)
      {
        try
        {
          Record record = new Record();
          record.getPlayerList().add(player);
          record.setGame(match.getFormat().getGame());
          RecordService.getInstance().saveRecord(record);
          player.getRecordList().add(record);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }
    });
    return true;
  }

  /**
   * Remove team from match.
   *
   * @param match Match to remove from.
   * @param team Team to remove.
   * @throws NonexistentEntityException if team doesn't exist in match.
   */
  public void removeTeam(MatchEntry match, Team team)
          throws NonexistentEntityException
  {
    MatchHasTeam mht = null;
    for (MatchHasTeam temp : match.getMatchHasTeamList())
    {
      if (temp.getTeam().getId().equals(team.getId()))
      {
        mht = temp;
        break;
      }
    }
    if (mht != null)
    {
      match.getMatchHasTeamList().remove(mht);
      if (match.getMatchEntryPK() != null)
      {
        mhtc.destroy(mht.getMatchHasTeamPK());
      }
    }
  }

  @Override
  public List<MatchEntry> getAll()
  {
    return mc.findMatchEntryEntities();
  }

  /**
   * Get result type by name.
   *
   * @param type type to search for (as found in the data base).
   * @return Optional type.
   */
  public Optional<MatchResultType> getResultType(String type)
  {
    for (MatchResultType mrt : getResultTypes())
    {
      if (mrt.getType().equals(type))
      {
        return Optional.of(mrt);
      }
    }
    return Optional.empty();
  }

  /**
   * Get all Result Types.
   *
   * @return list of result types.
   */
  public List<MatchResultType> getResultTypes()
  {
    return mrtc.findMatchResultTypeEntities();
  }

  /**
   * Set result for a match.
   *
   * @param mht Team to assign result to.
   * @param mrt Result type.
   * @throws NonexistentEntityException If something doesn't exist.
   * @throws Exception persisting to data base
   */
  public void setResult(MatchHasTeam mht, MatchResultType mrt)
          throws NonexistentEntityException, Exception
  {
    if (mht.getMatchResult() != null)
    {
      //Delete the old one.
      mrc.destroy(mht.getMatchResult().getMatchResultPK());
    }
    MatchResult mr = new MatchResult();
    mr.setMatchResultType(mrt);
    mr.getMatchHasTeamList().add(mht);
    mrc.create(mr);
    mht.setMatchResult(mr);
  }

  /**
   * Lock the match result.This is meant not to be undone as it calculates
   * experience and update records which is dependent on when it happens.
   *
   * @param mr Match Result to lock.
   * @throws java.lang.Exception
   */
  public void lockMatchResult(MatchResult mr) throws Exception
  {
    mr.setLocked(true);

    // Update the record
    mr.getMatchHasTeamList().forEach(mht ->
    {
      mht.getTeam().getPlayerList().forEach(player ->
      {
        Record record = player.getRecordList().get(0);
        switch (mr.getMatchResultType().getType())
        {
          case "result.loss":
            record.setLoses(record.getLoses() + 1);
            break;
          case "result.draw":
            record.setDraws(record.getDraws() + 1);
            break;
          //Various reasons leading to a win.
          case "result.win":
          //Fall thru
          case "result.forfeit":
          //Fall thru
          case "result.no_show":
            record.setWins(record.getWins() + 1);
            break;
        }
        try
        {
          RecordService.getInstance().saveRecord(record);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      });
    });
    mrc.edit(mr);
  }

  /**
   * Update a match result.
   * 
   * @param mr Match result to update.
   * @throws Exception If result doesn't exist.
   */
  public void updateResult(MatchResult mr) throws Exception
  {
    if (mr.getMatchResultPK() != null)
    {
      mrc.edit(mr);
    }
    else
    {
      throw new Exception("Trying to update non existing result!");
    }
  }
}
