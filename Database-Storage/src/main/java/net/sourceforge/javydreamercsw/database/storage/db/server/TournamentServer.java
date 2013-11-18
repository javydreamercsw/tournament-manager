package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam;
import net.sourceforge.javydreamercsw.database.storage.db.controller.TournamentJpaController;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class TournamentServer extends Tournament implements DatabaseEntity<Tournament> {

    public TournamentServer(String name) {
        super(name);
        setId(0);
        setRoundList(new ArrayList<Round>());
        setTournamentHasTeamList(new ArrayList<TournamentHasTeam>());
        setWinPoints(0);
        setDrawPoints(0);
        setLossPoints(0);
    }

    public TournamentServer(String name, int winPoints, int drawPoints, int lossPoints) {
        super(name);
        setId(0);
        setRoundList(new ArrayList<Round>());
        setTournamentHasTeamList(new ArrayList<TournamentHasTeam>());
        setWinPoints(winPoints);
        setDrawPoints(drawPoints);
        setLossPoints(lossPoints);
    }

    public TournamentServer(Tournament t) {
        update((TournamentServer) this, t);
    }

    @Override
    public int write2DB() {
        TournamentJpaController controller
                = new TournamentJpaController(DataBaseManager.getEntityManagerFactory());
        if (getId() > 0) {
            Tournament tm = controller.findTournament(getId());
            update(tm, this);
            try {
                controller.edit(tm);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            Tournament tm = new Tournament();
            try {
                update(tm, this);
                controller.create(tm);
                setId(tm.getId());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
            setId(tm.getId());
        }
        return getId();
    }

    @Override
    public void update(Tournament target, Tournament source) {
        target.setId(source.getId());
        target.setName(source.getName());
        target.setRoundList(source.getRoundList());
        target.setTournamentHasTeamList(source.getTournamentHasTeamList());
        target.setDrawPoints(source.getDrawPoints());
        target.setWinPoints(source.getDrawPoints());
        target.setLossPoints(source.getLossPoints());
    }

    @Override
    public Tournament getEntity() {
        return new TournamentJpaController(DataBaseManager.getEntityManagerFactory()).findTournament(getId());
    }
}
