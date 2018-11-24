package com.github.javydreamercsw.database.storage.db.server;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Record;

import com.github.javydreamercsw.database.storage.db.controller.RecordJpaController;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RecordServer extends Record implements DatabaseEntity<Record>
{
  private static final long serialVersionUID = 1121455691184116787L;

  public RecordServer()
  {
    setId(0);
  }

  public RecordServer(int wins, int loses, int draws)
  {
    super(wins, loses, draws);
    setId(0);
  }

  @Override
  public int write2DB()
  {
    RecordJpaController controller
            = new RecordJpaController(DataBaseManager.getEntityManagerFactory());
    if (getId() > 0)
    {
      Record r = controller.findRecord(getId());
      update(r, this);
      try
      {
        controller.edit(r);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      Record r = new Record();
      update(r, this);
      try
      {
        controller.create(r);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
      setId(r.getId());
    }
    return getId();
  }

  @Override
  public void update(Record target, Record source)
  {
    target.setDraws(source.getDraws());
    target.setId(source.getId());
    target.setLoses(source.getLoses());
    target.setPlayerList(source.getPlayerList());
    target.setTournamentHasTeamList(source.getTournamentHasTeamList());
    target.setWins(source.getWins());
  }

  @Override
  public Record getEntity()
  {
    return new RecordJpaController(DataBaseManager.getEntityManagerFactory())
            .findRecord(getId());
  }
}
