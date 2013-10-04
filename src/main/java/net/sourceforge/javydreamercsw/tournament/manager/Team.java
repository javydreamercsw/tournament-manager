package net.sourceforge.javydreamercsw.tournament.manager;

import net.sourceforge.javydreamercsw.tournament.manager.api.Player;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Team {

    private final String name;

    private final List<Player> teamMembers;

    public Team(List<Player> teamMembers) {
        this.teamMembers = teamMembers;
        name = "";
    }

    public Team(String name, List<Player> teamMembers) {
        this.name = name;
        this.teamMembers = teamMembers;
    }

    public Team(Player p1) {
        teamMembers = new ArrayList<Player>();
        teamMembers.add(p1);
        name = "";
    }

    /**
     * @return the teamMembers
     */
    public List<Player> getTeamMembers() {
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
        for (Player p : teamMembers) {
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
