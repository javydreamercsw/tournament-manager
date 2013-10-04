package net.sourceforge.javydreamercsw.tournament.manager.api;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface ResultListener {

    /**
     * Propagate results to listeners.
     *
     * @param encounter Encounter
     * @throws TournamentException
     */
    public void updateResults(Encounter encounter) throws TournamentException;
}
