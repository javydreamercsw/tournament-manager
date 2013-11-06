package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TeamJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TeamServer extends Team implements DatabaseEntity<Team> {

    public TeamServer(String name, List<Player> players) {
        setId(0);
        setMatchList(new ArrayList<Match>());
        setName(name);
        setPlayerList(players);
        setTournamentHasTeamList(new ArrayList<TournamentHasTeam>());
    }

    @Override
    public int write2DB() {
        TeamJpaController controller
                = new TeamJpaController(DataBaseManager.getEntityManagerFactory());
        if (getId() > 0) {
            Team t = controller.findTeam(getId());
            update(t, this);
            try {
                controller.edit(t);
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            Team t = new Team();
            update(t, this);
            try {
                controller.create(t);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            setId(t.getId());
        }
        return getId();
    }

    @Override
    public void update(Team target, Team source) {
        target.setId(source.getId());
        target.setMatchList(source.getMatchList());
        target.setName(source.getName());
        target.setPlayerList(source.getPlayerList());
        target.setTournamentHasTeamList(source.getTournamentHasTeamList());
    }

    @Override
    public Team getEntity() {
        return new TeamJpaController(DataBaseManager.getEntityManagerFactory()).findTeam(getId());
    }
}
