package com.github.javydreamercsw.database.storage.db.server;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;

import com.github.javydreamercsw.database.storage.db.controller.TournamentHasTeamJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TournamentHasTeamServer extends TournamentHasTeam
        implements DatabaseEntity<TournamentHasTeam>
{
  private static final long serialVersionUID = -141187315917412721L;

  public TournamentHasTeamServer(Tournament t, Team team)
  {
    super(t.getId(), team.getId());
    setTeam(team);
    setTournament(t);
  }

  @Override
  public int write2DB()
  {
    TournamentHasTeamJpaController controller
            = new TournamentHasTeamJpaController(DataBaseManager
                    .getEntityManagerFactory());
    if (controller.findTournamentHasTeam(getTournamentHasTeamPK()) != null)
    {
      TournamentHasTeam tht = new TournamentHasTeam();
      update(tht, this);
      try
      {
        controller.create(tht);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
      setTournamentHasTeamPK(tht.getTournamentHasTeamPK());
    }
    return getTournamentHasTeamPK().getTeamId();
  }

  @Override
  public void update(TournamentHasTeam target, TournamentHasTeam source)
  {
    target.setRecordList(source.getRecordList());
    target.setTeam(source.getTeam());
    target.setTournament(source.getTournament());
    target.setTournamentHasTeamPK(source.getTournamentHasTeamPK());
  }

  @Override
  public TournamentHasTeam getEntity()
  {
    return new TournamentHasTeamJpaController(DataBaseManager
            .getEntityManagerFactory()).findTournamentHasTeam(getTournamentHasTeamPK());
  }
}
