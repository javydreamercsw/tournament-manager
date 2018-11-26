package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.MatchHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

public class TeamService
{
  private TeamJpaController tc
          = new TeamJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchHasTeamJpaController mhtc
          = new MatchHasTeamJpaController(DataBaseManager.getEntityManagerFactory());

  private TeamService()
  {
  }

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final TeamService INSTANCE = createTeamService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static TeamService createTeamService()
    {
      TeamService service = new TeamService();
      return service;
    }
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static TeamService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public List<Team> findTeams(String value)
  {
    List<Team> result = new ArrayList<>();

    if (value.trim().isEmpty())
    {
      result.addAll(tc.findTeamEntities());
    }
    else
    {
      tc.findTeamEntities().forEach(team ->
      {
        if (team.getName() != null
                && team.getName().toLowerCase().contains(value.toLowerCase()))
        {
          result.add(team);
        }
      });
    }
    return result;
  }

  public Team findTeam(int id)
  {
    return tc.findTeam(id);
  }

  public void saveTeam(Team t)
  {
    if (t.getId() != null && tc.findTeam(t.getId()) != null)
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

  public void deleteTeam(Team t)
  {
    try
    {
      for (MatchHasTeam mht : t.getMatchHasTeamList())
      {
        mhtc.destroy(mht.getMatchHasTeamPK());
      }
      for (TournamentHasTeam tht : t.getTournamentHasTeamList())
      {
        TournamentService.getInstance().deleteTeamFromTournament(tht);
      }
      tc.destroy(t.getId());
    }
    catch (IllegalOrphanException | NonexistentEntityException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Add players to team.
   *
   * @param team Team to add players to.
   * @param players Players to add.
   */
  public void addMembers(Team team, List<Player> players)
  {
    addMembers(team, players.toArray(new Player[0]));
  }

  /**
   * Add players to team.
   *
   * @param team Team to add players to.
   * @param players Players to add.
   */
  public void addMembers(Team team, Player... players)
  {
    for (Player p : players)
    {
      addMember(team, p, false);
    }
    saveTeam(team);
  }

  /**
   * Add player to team.
   *
   * @param team Team to add players to.
   * @param player Player to add.
   * @param save Save team as part of this transaction.
   */
  public void addMember(Team team, Player player, boolean save)
  {
    team.getPlayerList().add(player);
    player.getTeamList().add(team);
    if (save)
    {
      saveTeam(team);
    }
  }
}
