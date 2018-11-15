package com.github.javydreamercsw.tournament.manager.web.backend;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam;
import net.sourceforge.javydreamercsw.database.storage.db.controller.RoundJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TournamentHasTeamJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TournamentJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;

/**
 *
 * @author Javier Ortiz Bultron <javierortiz@pingidentity.com>
 */
public class TournamentService
{
  private TournamentJpaController tc
          = new TournamentJpaController(DataBaseManager.getEntityManagerFactory());
  private TournamentHasTeamJpaController thtc
          = new TournamentHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private RoundJpaController rc
          = new RoundJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final TournamentService INSTANCE = createTournamentService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static TournamentService createTournamentService()
    {
      TournamentService service = new TournamentService();

      return service;
    }
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static TournamentService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public List<Tournament> findTournaments(String value)
  {
    List<Tournament> result = new ArrayList<>();
    tc.findTournamentEntities().forEach(tournament ->
    {
      if (tournament.getName().toLowerCase().contains(value))
      {
        result.add(tournament);
      }
    });
    return result;
  }

  public void saveTournament(Tournament t)
  {
    if (t.getId() != null && tc.findTournament(t.getId()) != null)
    {
      try
      {
        tc.edit(t);
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
      tc.create(t);
    }
  }

  public void deleteTournament(Tournament t)
  {
    try
    {
      tc.destroy(t.getId());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  public List<Tournament> findTournament(Integer id)
  {
    List<Tournament> results = new ArrayList<>();
    Tournament t = tc.findTournament(id);
    if (t != null)
    {
      results.add(t);
    }
    return results;
  }

  /**
   * Add teams to the tournament.
   *
   * @param t Tournament
   * @param teams Teams to add
   * @throws Exception if there's an error adding teams.
   */
  public void addTeams(Tournament t, List<Team> teams) throws Exception
  {
    addTeams(t, teams.toArray(new Team[0]));
  }

  /**
   * Add teams to the tournament.
   *
   * @param t Tournament
   * @param teams Teams to add
   * @throws Exception if there's an error adding teams.
   */
  public void addTeams(Tournament t, Team... teams) throws Exception
  {
    for (Team team : teams)
    {
      addTeam(t, team, false);
    }
    saveTournament(t);
  }

  /**
   * Add team to the tournament.
   *
   * @param t Tournament
   * @param team Team to add
   * @throws Exception if there's an error adding team.
   */
  public void addTeam(Tournament t, Team team) throws Exception
  {
    addTeam(t, team, true);
  }

  /**
   * Add team to the tournament.
   *
   * @param t Tournament
   * @param team Team to add
   * @param save Save the tournament as part of this transaction.
   * @throws Exception if there's an error adding team.
   */
  public void addTeam(Tournament t, Team team, boolean save) throws Exception
  {
    TournamentHasTeam tht = new TournamentHasTeam();
    tht.setTeam(team);
    tht.setTournament(t);
    t.getTournamentHasTeamList().add(tht);
    thtc.create(tht);
    if (save)
    {
      saveTournament(t);
    }
  }

  /**
   * Add round to tournament.
   *
   * @param t Tournament to add round to.
   * @throws Exception If something goes wrong adding it.
   */
  public void addRound(Tournament t) throws Exception
  {
    addRound(t, true);
  }

  /**
   * Add round to tournament.
   *
   * @param t Tournament to add round to.
   * @param save Save the tournament as part of this transaction.
   * @throws Exception If something goes wrong adding it.
   */
  public void addRound(Tournament t, boolean save) throws Exception
  {
    Round round = new Round();
    round.setTournament(t);
    rc.create(round);
    t.getRoundList().add(round);
    if (save)
    {
      saveTournament(t);
    }
  }

  /**
   * Delete a team from the tournament.
   *
   * @param tht Team to remove.
   * @throws NonexistentEntityException if it doesn't exist.
   */
  public void deleteTeamFromTournament(TournamentHasTeam tht)
          throws NonexistentEntityException
  {
    thtc.destroy(tht.getTournamentHasTeamPK());
  }
}
