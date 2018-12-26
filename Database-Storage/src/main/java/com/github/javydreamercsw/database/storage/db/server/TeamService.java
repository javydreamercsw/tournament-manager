package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.controller.MatchHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TeamHasFormatRecordJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

import de.gesundkrank.jskills.Rating;

public class TeamService extends Service<Team>
{
  private TeamJpaController tc
          = new TeamJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchHasTeamJpaController mhtc
          = new MatchHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private final TeamHasFormatRecordJpaController thfrc
          = new TeamHasFormatRecordJpaController(DataBaseManager.getEntityManagerFactory());

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
      result.addAll(getAll());
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
      for (TeamHasFormatRecord thfr : t.getTeamHasFormatRecordList())
      {
        thfrc.destroy(thfr.getTeamHasFormatRecordPK());
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
      addMember(team, p);
    }
    saveTeam(team);
  }

  /**
   * Add player to team.
   *
   * @param team Team to add players to.
   * @param player Player to add.
   */
  public void addMember(Team team, Player player)
  {
    team.getPlayerList().add(player);
    player.getTeamList().add(team);
    saveTeam(team);
  }

  @Override
  public List<Team> getAll()
  {
    return tc.findTeamEntities();
  }

  /**
   * Convert a team form the database representation to the JSKill
   * representation.
   *
   * @param t Team to convert.
   * @param format Format to get ratings for.
   * @return converted team.
   */
  public com.github.javydreamercsw.tournament.manager.Team convertToTeam(Team t,
          Format format)
  {
    com.github.javydreamercsw.tournament.manager.Team team
            = new com.github.javydreamercsw.tournament.manager.Team(t.getId(),
                    new ArrayList<>());
    t.getPlayerList().forEach(player ->
    {
      Rating r;
      if (hasFormatRecord(t, format))
      {
        TeamHasFormatRecord thfr = getFormatRecord(t, format);
        r = new Rating(thfr.getMean(), thfr.getStandardDeviation());
      }
      else
      {
        r = new Rating(0.0, 0.0);
      }
      team.addPlayer(PlayerService.getInstance().convertToUIPlayer(player), r);
    });
    return team;
  }

  /**
   * Check if this team has a record entry in the database for this format.
   *
   * @param team Team to check.
   * @param format Format to check
   * @return true if exists, false otherwise.
   */
  public boolean hasFormatRecord(Team team, Format format)
  {
    return getFormatRecord(team, format) != null;
  }

  /**
   * Get the teams record for the specified format.
   *
   * @param team Team to check.
   * @param format Format to check.
   * @return Record or null if not found.
   */
  public TeamHasFormatRecord getFormatRecord(Team team, Format format)
  {
    for (TeamHasFormatRecord thfr
            : findTeam(team.getId()).getTeamHasFormatRecordList())
    {
      if (thfr.getFormat().getFormatPK().equals(format.getFormatPK()))
      {
        return thfr;
      }
    }
    return null;
  }
}
