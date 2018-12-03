package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.RoundJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TournamentHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TournamentJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

public class TournamentService extends Service<Tournament>
{
  private TournamentJpaController tc
          = new TournamentJpaController(DataBaseManager.getEntityManagerFactory());
  private TournamentHasTeamJpaController thtc
          = new TournamentHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private RoundJpaController rc
          = new RoundJpaController(DataBaseManager.getEntityManagerFactory());

  private TournamentService()
  {
  }

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
      if (tournament.getName().toLowerCase().contains(value.toLowerCase()))
      {
        result.add(tournament);
      }
    });
    return result;
  }

  public void saveTournament(Tournament t) throws NonexistentEntityException, 
          IllegalOrphanException, Exception
  {
    if (t.getId() != null && tc.findTournament(t.getId()) != null)
    {
        tc.edit(t);
    }
    else
    {
      tc.create(t);
    }
  }

  public void deleteTournament(Tournament t) throws IllegalOrphanException,
          NonexistentEntityException
  {
    for (Round round : t.getRoundList())
    {
      deleteRound(round);
    }

    for (TournamentHasTeam tht : t.getTournamentHasTeamList())
    {
      deleteTeamFromTournament(tht);
    }
    tc.destroy(t.getId());
  }

  public Tournament findTournament(Integer id)
  {
    return tc.findTournament(id);
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
      addTeam(t, team);
    }
  }

  /**
   * Add team to the tournament.
   *
   * @param t Tournament
   * @param team Team to add
   * @throws IllegalOrphanException if an orphan is left
   * @throws Exception if there's an error adding team.
   */
  public void addTeam(Tournament t, Team team) throws IllegalOrphanException, 
          Exception
  {
    TournamentHasTeam tht = new TournamentHasTeam();
    tht.setTeam(team);
    tht.setTournament(t);
    thtc.create(tht);
    t.getTournamentHasTeamList().add(tht);
    saveTournament(t);
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

  @Override
  public List<Tournament> getAll()
  {
    return tc.findTournamentEntities();
  }

  public void deleteRound(Round round) throws IllegalOrphanException,
          NonexistentEntityException
  {
    rc.destroy(round.getRoundPK());
  }
}
