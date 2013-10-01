package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Where to parties face off
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Encounter {

    private final List<Team> teams = new ArrayList<Team>();

    public Encounter(Team team1, Team team2, Team... t) {
        teams.add(team1);
        teams.add(team2);
        teams.addAll(Arrays.asList(t));
    }

    public Encounter(Player p1, Player p2) {
        teams.add(new Team(p1));
        teams.add(new Team(p2));
    }

    public void updateResult(int team, EncounterResult result) {
        assert team < getTeams().size();

        Team target = getTeams().get(team);
        for (Player p : target.getTeamMembers()) {
            switch (result) {
                case WIN:
                    p.win();
                    //The rest lose
                    for (int i = 0; i < getTeams().size(); i++) {
                        if (i != team) {
                            for (Player player : getTeams().get(i).getTeamMembers()) {
                                player.loss();
                            }
                        }
                    }
                    break;
                case DRAW:
                    //Everyone draws
                    for (Team t : getTeams()) {
                        for (Player player : t.getTeamMembers()) {
                            player.draw();
                        }
                    }
                    break;
                case LOSS:
                    //The others win
                    p.loss();
                    for (int i = 0; i < getTeams().size(); i++) {
                        if (i != team) {
                            for (Player player : getTeams().get(i).getTeamMembers()) {
                                player.win();
                            }
                        }
                    }
                    break;
                default:
                    throw new RuntimeException("Invalid result: " + result);
            }
        }
    }

    /**
     * @return the teams
     */
    public List<Team> getTeams() {
        return teams;
    }
}
