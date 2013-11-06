package net.sourceforge.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import net.sourceforge.javydreamercsw.database.storage.db.Match;
import net.sourceforge.javydreamercsw.database.storage.db.Round;
import net.sourceforge.javydreamercsw.database.storage.db.RoundPK;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.controller.RoundJpaController;
import net.sourceforge.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import org.openide.util.Exceptions;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class RoundServer extends Round implements DatabaseEntity<Round> {

    public RoundServer(Tournament t) {
        super(t.getId());
        setRoundPK(new RoundPK(t.getId()));
        setMatchList(new ArrayList<Match>());
        setTournament(t);
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
        target.setMatchList(source.getMatchList());
        target.setRoundPK(source.getRoundPK());
        target.setTournament(source.getTournament());
    }

    @Override
    public Round getEntity() {
        return new RoundJpaController(DataBaseManager.getEntityManagerFactory()).findRound(getRoundPK());
    }

}
