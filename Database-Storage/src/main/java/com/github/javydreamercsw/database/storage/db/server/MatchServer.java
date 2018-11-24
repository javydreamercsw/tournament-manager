package com.github.javydreamercsw.database.storage.db.server;


import java.util.ArrayList;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;
import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class MatchServer extends MatchEntry implements DatabaseEntity<MatchEntry> {
  private static final long serialVersionUID = 1910222928574456623L;

    public MatchServer(int round, int format) throws TournamentException {
        MatchEntryJpaController controller
                = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
        MatchEntryPK pk = new MatchEntryPK(round, format);
        MatchEntry m = controller.findMatchEntry(pk);
        if (m == null) {
            throw new TournamentException("Unable to find match: " + pk);
        } else {
            update((MatchServer) this, m);
        }
    }
    
    public MatchServer(Round r, Format f) {
        super(r.getRoundPK().getId(),f.getId());
        setRound(r);
        setFormat(f);
        setMatchHasTeamList(new ArrayList<>());
    }

    public MatchServer(MatchEntry m) {
        MatchEntryJpaController controller
                = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
        update((MatchServer) this, controller.findMatchEntry(m.getMatchEntryPK()));
    }

    @Override
    public int write2DB() {
        MatchEntryJpaController controller
                = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
        if (getMatchEntryPK().getId() > 0) {
            MatchEntry m = controller.findMatchEntry(getMatchEntryPK());
            update(m, this);
            try {
                controller.edit(m);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            MatchEntry m = new MatchEntry();
            update(m, this);
            try {
                controller.create(m);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return getMatchEntryPK().getId();
    }

    @Override
    public void update(MatchEntry target, MatchEntry source) {
        target.setMatchEntryPK(source.getMatchEntryPK());
        target.setRound(source.getRound());
        target.setFormat(source.getFormat());
        target.setMatchHasTeamList(source.getMatchHasTeamList());
    }

    @Override
    public MatchEntry getEntity() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
