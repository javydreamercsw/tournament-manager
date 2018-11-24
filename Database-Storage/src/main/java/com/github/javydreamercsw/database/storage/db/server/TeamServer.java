package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.TeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TeamServer extends Team implements DatabaseEntity<Team> {
  private static final long serialVersionUID = 2352135356546640102L;

    public TeamServer(String name, List<Player> players) {
        setId(0);
        setMatchHasTeamList(new ArrayList<>());
        setMatchHasTeamList(new ArrayList<>());
        setName(name);
        setPlayerList(players);
        setTournamentHasTeamList(new ArrayList<>());
    }

    public TeamServer(Team t) {
        update((TeamServer) this, t);
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
        target.setMatchHasTeamList(source.getMatchHasTeamList());
        target.setName(source.getName());
        target.setPlayerList(source.getPlayerList());
        target.setTournamentHasTeamList(source.getTournamentHasTeamList());
        target.setMatchHasTeamList(source.getMatchHasTeamList());
    }

    @Override
    public Team getEntity() {
        return new TeamJpaController(DataBaseManager.getEntityManagerFactory()).findTeam(getId());
    }

    public static boolean hasMembers(Team team, List<TournamentPlayerInterface> teamMembers) {
        boolean found = false;
        //If it's not even the same size, don't bother
        if (teamMembers.size() == team.getPlayerList().size()) {
            for (TournamentPlayerInterface pi : teamMembers) {
                boolean internalFound = false;
                for (Player p : team.getPlayerList()) {
                    if (p.getId().equals(pi.getID())) {
                        internalFound = true;
                        break;
                    }
                }
                if (!internalFound) {
                    found = false;
                    break;
                } else {
                    found = true;
                }
            }
        }
        return found;
    }
}
