package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultTypeJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class MatchService
{
  private MatchEntryJpaController mc
          = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchHasTeamJpaController mhtc
          = new MatchHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultJpaController mrc
          = new MatchResultJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultTypeJpaController mrtc
          = new MatchResultTypeJpaController(DataBaseManager.getEntityManagerFactory());
  private final String[] matchResults = new String[]
  {
    "UNDEFINED", "DRAW", "WIN", "LOSE"
  };

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
    // Initialize the match result types
    for (String type : matchResults)
    {
      boolean found = false;
      for (MatchResultType mrt : mrtc.findMatchResultTypeEntities())
      {
        if (mrt.getType().equals(type))
        {
          found = true;
          break;
        }
      }
      if (!found)
      {
        MatchResultType mrt = new MatchResultType(type);
        mrtc.create(mrt);
      }
    }
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
      match.getMatchHasTeamList().forEach(mht ->
      {
        if (mht.getMatchHasTeamPK() == null)
        {
          try
          {
            // Needs to be created
            mhtc.create(mht);
          }
          catch (Exception ex)
          {
            Exceptions.printStackTrace(ex);
          }
        }
      });
      mc.edit(match);
    }
    else
    {
      // Remove the teams since the entry needs to exist first
      List<Team> teams = new ArrayList<>();
      match.getMatchHasTeamList().forEach(mht -> teams.add(mht.getTeam()));
      match.getMatchHasTeamList().clear();
      mc.create(match);

      // Now add the teams
      for (Team team : teams)
      {
        addTeam(match, team);
      }
    }
  }

  public void deleteMatch(MatchEntry match)
  {
    try
    {
      mc.destroy(match.getMatchEntryPK());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
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

  public void addTeam(MatchEntry match, Team team) throws Exception
  {
    MatchHasTeam mht = new MatchHasTeam();
    mht.setTeam(team);
    mht.setMatchEntry(match);

    MatchResult mr = new MatchResult();
    mr.setMatchResultType(mrtc.findMatchResultType(1));

    if (match.getMatchEntryPK() != null)
    {
      mrc.create(mr);
    }

    mht.setMatchResult(mr);

    if (match.getMatchEntryPK() != null)
    {
      mhtc.create(mht);
    }

    match.getMatchHasTeamList().add(mht);
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
}
