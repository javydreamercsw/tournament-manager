package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import net.sourceforge.javydreamercsw.database.storage.db.MatchPK;
import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.controller.MatchJpaController;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class MatchServer extends Match implements DatabaseEntity<Match> {

    public MatchServer(int round, int match) throws TournamentException {
        MatchJpaController controller
                = new MatchJpaController(DataBaseManager.getEntityManagerFactory());
        MatchPK pk = new MatchPK(round);
        pk.setId(match);
        Match m = controller.findMatch(pk);
        if (m == null) {
            throw new TournamentException("Unable to find match: " + pk);
        } else {
            update((MatchServer) this, m);
        }
    }

    public MatchServer(Round r) {
        super(r.getRoundPK().getId());
        setRound(r);
        setTeamList(new ArrayList<Team>());
    }

    public MatchServer(Match m) {
        MatchJpaController controller
                = new MatchJpaController(DataBaseManager.getEntityManagerFactory());
        update((MatchServer) this, controller.findMatch(m.getMatchPK()));
    }

    @Override
    public int write2DB() {
        MatchJpaController controller
                = new MatchJpaController(DataBaseManager.getEntityManagerFactory());
        if (getMatchPK().getId() > 0) {
            Match m = controller.findMatch(getMatchPK());
            update(m, this);
            try {
                controller.edit(m);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            Match m = new Match();
            update(m, this);
            try {
                controller.create(m);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return getMatchPK().getId();
    }

    @Override
    public void update(Match target, Match source) {
        target.setMatchPK(source.getMatchPK());
        target.setRound(source.getRound());
        target.setTeamList(source.getTeamList());
    }

    @Override
    public Match getEntity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
