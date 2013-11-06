package net.sourceforge.javydreamercsw.database.storage.db;

import java.util.List;
import net.sourceforge.javydreamercsw.database.storage.db.server.PlayerServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.RoundServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.TeamServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.TournamentServer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestHelper {

    public static PlayerServer createPlayer(String name) {
        return new PlayerServer(new net.sourceforge.javydreamercsw.tournament.manager.Player(name));
    }

    public static TournamentServer createTournament(String name) {
        return new TournamentServer(name);
    }

    public static RoundServer createRound(Tournament t) {
        return new RoundServer(t);
    }

    public static TeamServer createTeam(String name, List<Player> players) {
        return new TeamServer(name, players);
    }
}
