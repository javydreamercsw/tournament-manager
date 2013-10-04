package net.sourceforge.javydreamercsw.tournament.manager.api;

/**
 * Exceptions specific for the tournament.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentException extends Exception {

    public TournamentException(String message) {
        super(message);
    }

    public TournamentException(String message, Throwable cause) {
        super(message, cause);
    }

    public TournamentException(Throwable cause) {
        super(cause);
    }
}
