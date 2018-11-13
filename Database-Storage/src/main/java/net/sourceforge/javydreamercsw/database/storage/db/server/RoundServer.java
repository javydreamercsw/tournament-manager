package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;

import org.openide.util.Exceptions;

import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.RoundPK;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.controller.RoundJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public final class RoundServer extends Round implements DatabaseEntity<Round> {
  private static final long serialVersionUID = -4051744388749043991L;

    public RoundServer(Tournament t) {
        super(t.getId());
        setRoundPK(new RoundPK(t.getId()));
        setMatchEntryList(new ArrayList<>());
        setTournament(t);
    }

    public RoundServer(Round r) {
        RoundJpaController controller
                = new RoundJpaController(DataBaseManager.getEntityManagerFactory());
        update((RoundServer) this, controller.findRound(r.getRoundPK()));
    }

    @Override
    public int write2DB() {
        RoundJpaController controller
                = new RoundJpaController(DataBaseManager.getEntityManagerFactory());
        if (getRoundPK().getId() > 0) {
            Round r = controller.findRound(getRoundPK());
            update(r, this);
            try {
                controller.edit(r);
            } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            Round r = new Round();
            update(r, this);
            try {
                controller.create(r);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return getRoundPK().getId();
    }

    @Override
    public void update(Round target, Round source) {
        target.setMatchEntryList(source.getMatchEntryList());
        target.setRoundPK(source.getRoundPK());
        target.setTournament(source.getTournament());
    }

    @Override
    public Round getEntity() {
        return new RoundJpaController(DataBaseManager.getEntityManagerFactory())
                .findRound(getRoundPK());
    }
}
