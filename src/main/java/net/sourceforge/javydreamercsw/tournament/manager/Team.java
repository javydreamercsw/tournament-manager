package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.List;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Team {

    private final String name;

    private final List<TournamentPlayerInterface> teamMembers;

    public Team(List<TournamentPlayerInterface> teamMembers) {
        this.teamMembers = teamMembers;
        name = "";
    }

    public Team(String name, List<TournamentPlayerInterface> teamMembers) {
        this.name = name;
        this.teamMembers = teamMembers;
    }

    public Team(TournamentPlayerInterface p1) {
        teamMembers = new ArrayList<>();
        teamMembers.add(p1);
        name = "";
    }

    /**
     * @return the teamMembers
     */
    public List<TournamentPlayerInterface> getTeamMembers() {
        return teamMembers;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder((name.trim().isEmpty() ? "" : "Team " + name + "("));
        for (TournamentPlayerInterface p : teamMembers) {
            String val = sb.toString();
            if (!val.trim().isEmpty() && !val.endsWith("(")) {
                sb.append(", ");
            }
            sb.append(p.toString());
        }
        if (!sb.toString().trim().isEmpty() && sb.toString().startsWith("Team")) {
            sb.append(")");
        }
        return sb.toString();
    }
}
