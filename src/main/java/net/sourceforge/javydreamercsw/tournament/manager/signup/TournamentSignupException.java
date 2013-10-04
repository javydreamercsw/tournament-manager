package net.sourceforge.javydreamercsw.tournament.manager.signup;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;

/**
 * Exceptions specific for the tournament sign ups.
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TournamentSignupException extends TournamentException {

    public TournamentSignupException(String message) {
        super(message);
    }

    public TournamentSignupException(String message, Throwable cause) {
        super(message, cause);
    }

    public TournamentSignupException(Throwable cause) {
        super(cause);
    }
}
