/*
 * Default Database Storage.
 */
package net.sourceforge.javydreamercsw.database.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.javydreamercsw.database.storage.db.Player;
import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.Team;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.TournamentHasTeam;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;
import net.sourceforge.javydreamercsw.database.storage.db.server.MatchServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.PlayerServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.RoundServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.TeamServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.TournamentServer;
import net.sourceforge.javydreamercsw.tournament.manager.api.Encounter;
import net.sourceforge.javydreamercsw.tournament.manager.api.EncounterResult;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentException;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageException;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageInterface;
import org.openide.util.Lookup;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class DatabaseStorage implements StorageInterface {

    private boolean initialized = false;

    @Override
    public void saveTournament(TournamentInterface ti) throws StorageException {
        initialize();
        Map<String, Object> properties = new HashMap<>();
        //Already exists
        properties.put("id", ti.getId());
        List<Object> result = DataBaseManager.namedQuery("Tournament.findById", properties);
        TournamentServer ts;
        if (result.isEmpty()) {
            ts = new TournamentServer(ti.getName());
            ts.write2DB();
            ti.setId(ts.getId());
        } else {
            ts = new TournamentServer((Tournament) result.get(0));
            ts.setName(ti.getName());
        }
        ts.setWinPoints(ti.getWinPoints());
        ts.setDrawPoints(ti.getDrawPoints());
        ts.setLossPoints(ti.getLossPoints());
        //Process rounds
        int currentRound = ti.getRound();
        for (int r = 1; r <= currentRound; r++) {
            if (ts.getRoundList().size() >= r) {
                //Round exists, update
                RoundServer theRound
                        = new RoundServer(ts.getRoundList().get(r - 1));
                for (Map.Entry<Integer, Encounter> entry : ti.getPairings().entrySet()) {
                    MatchServer ms;
                    Encounter encounter = entry.getValue();
                    try {
                        ms = new MatchServer(theRound.getRoundPK().getId(),
                                entry.getKey());
                        //TODO: Update just in case
                    } catch (TournamentException ex) {
                        //Is a new encounter
                        ms = new MatchServer(theRound.getEntity());
                        List<Team> teams = new ArrayList<>();
                        List<Player> players = new ArrayList<>();
                        Team found = null;
                        for (Map.Entry<TeamInterface, EncounterResult> e2
                                : encounter.getEncounterSummary().entrySet()) {
                            for (TournamentPlayerInterface p : e2.getKey().getTeamMembers()) {
                                //Is there a team for them already?
                                PlayerServer player = new PlayerServer(p);
                                teams.addAll(player.getTeamList());
                                players.add(player.getEntity());
                            }
                            //Now teams have all the teams for the players, look to see if one contains them all
                            for (Team t : teams) {
                                if (TeamServer.hasMembers(t, e2.getKey().getTeamMembers())) {
                                    found = t;
                                    break;
                                }
                            }
                            if (found == null) {
                                //Need to create one
                                TeamServer temp = new TeamServer("", players);
                                temp.write2DB();
                                found = temp.getEntity();
                            }
                            ms.getTeamList().add(found);
                        }
                        ms.write2DB();
                    }
                }
            } else {
                //New round, add
            }
        }
        ts.setRoundList(null);
        //Process teams
        ts.setTournamentHasTeamList(null);
    }

    @Override
    public void loadTournament(int id)
            throws StorageException {
        initialize();
        Map<String, Object> properties = new HashMap<>();
        //Already exists
        properties.put("id", id);
        List<Object> result = DataBaseManager.namedQuery("Tournament.findById", properties);
        TournamentServer ts;
        if (result.isEmpty()) {
            throw new StorageException("Unable to find tournament with id: " + id);
        } else {
            ts = new TournamentServer((Tournament) result.get(0));
        }
        List<TeamInterface> teams = new ArrayList<>();
        for (TournamentHasTeam tht : ts.getTournamentHasTeamList()) {
            List<TournamentPlayerInterface> players = new ArrayList<>();
            for (Player p : tht.getTeam().getPlayerList()) {
                int wins = 0;
                int loss = 0;
                int draws = 0;
                for (Record r : p.getRecordList()) {
                    wins += r.getWins();
                    loss += r.getLoses();
                    draws += r.getDraws();
                }
                players.add(Lookup.getDefault().lookup(TournamentPlayerInterface.class)
                        .createInstance(p.getName(), wins, loss, draws));
            }
            teams.add(Lookup.getDefault().lookup(TeamInterface.class)
                    .createTeam(tht.getTeam().getName(), players));
        }
        TournamentInterface tournament
                = Lookup.getDefault().lookup(TournamentInterface.class)
                .createTournament(teams, ts.getWinPoints(),
                        ts.getLossPoints(),
                        ts.getDrawPoints());
        tournament.setId(id);
//        for (Round round : ts.getRoundList()) {
//            //Convert from database to interface
//            Map<Integer, Encounter> encounters = new HashMap<>();
//            for (Match match : round.getMatchList()) {
//                List<TeamInterface> teams = new ArrayList<>();
//                for (Team t : match.getTeamList()) {
//                    Team team = new TeamServer(t).getEntity();
//                    List< TournamentPlayerInterface> teamMembers
//                            = new ArrayList<>();
//                    for (Player player : team.getPlayerList()) {
//                        int wins = 0;
//                        int losses = 0;
//                        int draws = 0;
//                        for (Record record : player.getRecordList()) {
//                            losses += record.getLoses();
//                            wins += record.getWins();
//                            draws += record.getDraws();
//                        }
//                        teamMembers.add(Lookup.getDefault().lookup(TournamentPlayerInterface.class).createInstance(player.getName(), wins, losses, draws));
//                    }
//                    teams.add(Lookup.getDefault().lookup(TeamInterface.class).createTeam(team.getName(), teamMembers));
//                }
//
////                new Encounter(match.getMatchPK().getId(), teams);
//                encounters.put(match.getMatchPK().getId(), null);
//            }
//            tournament.setRound(round.getRoundPK().getId(), encounters);
//        }
    }

    @Override
    public void initialize() {
        if (!isInitialized()) {
            initialized = DataBaseManager.getEntityManagerFactory().isOpen();
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }
}
