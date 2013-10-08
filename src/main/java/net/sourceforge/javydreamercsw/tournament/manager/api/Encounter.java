package net.sourceforge.javydreamercsw.tournament.manager.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sourceforge.javydreamercsw.tournament.manager.Team;

/**
 * Where to parties face off
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Encounter {

    private final Map<Team, EncounterResult> teams
            = new HashMap<>();
    private final int id;
    private static final Logger LOG
            = Logger.getLogger(Encounter.class.getName());

    /**
     * Create an encounter between multiple teams.
     *
     * @param id encounter id
     * @param team1
     * @param team2
     * @param t additional teams (optional)
     */
    public Encounter(int id, Team team1, Team team2, Team... t) {
        teams.put(team1, EncounterResult.UNDECIDED);
        teams.put(team2, EncounterResult.UNDECIDED);
        for (Team team : t) {
            teams.put(team, EncounterResult.UNDECIDED);
        }
        this.id = id;
    }

    /**
     * Create an encounter between two players.
     *
     * @param id encounter id
     * @param p1 player 1
     * @param p2 player 2
     */
    public Encounter(int id, TournamentPlayerInterface p1, TournamentPlayerInterface p2) {
        teams.put(new Team(p1), EncounterResult.UNDECIDED);
        teams.put(new Team(p2), EncounterResult.UNDECIDED);
        this.id = id;
    }

    public void updateResult(TournamentPlayerInterface player,
            EncounterResult result) throws TournamentException {
        Team target = null;
        for (Entry<Team, EncounterResult> entry : getEncounterSummary().entrySet()) {
            if (entry.getKey().getTeamMembers().contains(player)) {
                target = entry.getKey();
            }
        }
        if (target != null) {
            LOG.log(Level.FINE, "Updating result for player: {0} to: {1}",
                    new Object[]{player.getName(), result});
            for (TournamentPlayerInterface p : target.getTeamMembers()) {
                switch (result) {
                    case WIN:
                        p.win();
                        break;
                    case DRAW:
                        player.draw();
                        break;
                    case NO_SHOW:
                    //Fall thru
                    case LOSS:
                        p.loss();
                        break;
                    case UNDECIDED:
                        throw new TournamentException("Unexpected result: " + result);
                    default:
                        throw new TournamentException("Invalid result: " + result);
                }
            }
        } else {
            throw new TournamentException("TournamentPlayerInterface not part of this encounter: " + player.getName());
        }
    }

    /**
     * @return the teams
     */
    public Map<Team, EncounterResult> getEncounterSummary() {
        return teams;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof Encounter) {
            Encounter encounter = (Encounter) obj;
            result = encounter.getId() == getId();
        }
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + (this.teams != null ? this.teams.hashCode() : 0);
        hash = 19 * hash + this.id;
        return hash;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Team t : teams.keySet()) {
            if (!sb.toString().isEmpty()) {
                sb.append(" vs. ");
            }
            sb.append(t.toString());
        }
        return "Encounter " + id + " (" + sb.toString() + ")";
    }
}
