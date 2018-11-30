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

  public void deleteMatch(MatchEntry match)
  {
    try
    {
      for (MatchHasTeam mht : match.getMatchHasTeamList())
      {
        mhtc.destroy(mht.getMatchHasTeamPK());
      }
      mc.destroy(match.getMatchEntryPK());
    }
    catch (NonexistentEntityException | IllegalOrphanException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

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

  public List<MatchEntry> findMatch(MatchEntryPK key)
  {
    List<MatchEntry> results = new ArrayList<>();
    MatchEntry match = mc.findMatchEntry(key);
    if (match != null)
    {
      results.add(match);
    }
    return results;
  }

  public List<MatchEntry> findMatches()
  {
    return mc.findMatchEntryEntities();
  }

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
    mht.setMatchHasTeamPK(new MatchHasTeamPK(match.getMatchEntryPK().getId(),
            match.getMatchEntryPK().getFormatId(), match.getFormat().getGame().getId(),
            team.getId()));

//    MatchResult mr = new MatchResult();
//    mr.setMatchResultType(mrtc.findMatchResultType(1));
//
//    if (match.getMatchEntryPK() != null)
//    {
//      mrc.create(mr);
//    }
//
//    mht.setMatchResult(mr);
    if (match.getMatchEntryPK() != null)
    {
      mhtc.create(mht);
    }

    match.getMatchHasTeamList().add(mht);
    return true;
  }

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

  public List<MatchResultType> getResultTypes()
  {
    return mrtc.findMatchResultTypeEntities();
  }

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
}
