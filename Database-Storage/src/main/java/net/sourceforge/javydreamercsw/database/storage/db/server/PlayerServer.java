package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;

import org.openide.util.Exceptions;

import net.sourceforge.javydreamercsw.database.storage.db.controller.PlayerJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class PlayerServer extends Player implements DatabaseEntity<Player>
{
  private static final long serialVersionUID = -7515880033696798006L;

  public PlayerServer(TournamentPlayerInterface p)
  {
    setName(p.getName());
    setRecordList(new ArrayList<>());
    setTeamList(new ArrayList<>());
    setId(0);
  }

  public PlayerServer(Player p)
  {
    Player player;
    if (p.getId() != null)
    {
      player = new PlayerJpaController(
              DataBaseManager.getEntityManagerFactory()).findPlayer(p.getId());
    }
    else
    {
      player = new Player(p.getName());
      new PlayerJpaController(
              DataBaseManager.getEntityManagerFactory()).create(player);
    }
    if (player.getRecordList().isEmpty())
    {
      RecordServer record = new RecordServer(0, 0, 0);
      record.write2DB();
      player.getRecordList().add(record.getEntity());
    }
    update((PlayerServer) this, player);
  }

  @Override
  public int write2DB()
  {
    PlayerJpaController controller
            = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
    if (getId() > 0)
    {
      //Update
      Player player = controller.findPlayer(getId());
      update(player, this);
      try
      {
        controller.edit(player);
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      //New one
      Player player = new Player();
      try
      {
        update(player, this);
        controller.create(player);
        setId(player.getId());
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
      setId(player.getId());
    }
    return getId();
  }

  @Override
  public void update(Player target, Player source)
  {
    target.setId(source.getId());
    target.setName(source.getName());
    if (source.getRecordList() != null)
    {
      target.setRecordList(source.getRecordList());
    }
    if (source.getTeamList() != null)
    {
      target.setTeamList(source.getTeamList());
    }
  }

  @Override
  public Player getEntity()
  {
    return new PlayerJpaController(
            DataBaseManager.getEntityManagerFactory()).findPlayer(getId());
  }
}
