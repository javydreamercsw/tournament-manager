package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.controller.PlayerJpaController;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class PlayerServer extends Player implements DatabaseEntity<Player> {

    public PlayerServer(TournamentPlayerInterface p) {
        setName(p.getName());
        setRecordList(new ArrayList<Record>());
        setTeamList(new ArrayList<Team>());
        setId(0);
    }

    public PlayerServer(Player p) {
        Player player = new PlayerJpaController(
                DataBaseManager.getEntityManagerFactory()).findPlayer(p.getId());
        update((PlayerServer) this, player);
    }

    @Override
    public int write2DB() {
        PlayerJpaController controller
                = new PlayerJpaController(DataBaseManager.getEntityManagerFactory());
        if (getId() > 0) {
            //Update
            Player player = controller.findPlayer(getId());
            update(player, this);
            try {
                controller.edit(player);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            //New one
            Player player = new Player();
            try {
                update(player, this);
                controller.create(player);
                setId(player.getId());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            setId(player.getId());
        }
        return getId();
    }

    @Override
    public void update(Player target, Player source) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setRecordList(source.getRecordList());
        target.setTeamList(source.getTeamList());
    }
}
