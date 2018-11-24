package com.github.javydreamercsw.database.storage.db;

import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.Player;

import java.util.List;

import com.github.javydreamercsw.database.storage.db.server.FormatServer;
import com.github.javydreamercsw.database.storage.db.server.PlayerServer;
import com.github.javydreamercsw.database.storage.db.server.RoundServer;
import com.github.javydreamercsw.database.storage.db.server.TeamServer;
import com.github.javydreamercsw.database.storage.db.server.TournamentServer;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TestHelper {

    public static PlayerServer createPlayer(String name) {
      return new PlayerServer(new Player(name));
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

  public static FormatServer createFormat(String name)
  {
    return new FormatServer(name);
  }
}
