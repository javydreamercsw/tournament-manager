package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Lookup;

import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Team implements TeamInterface
{
  private final String name;
  private RecordInterface record = null;
  private final List<TournamentPlayerInterface> teamMembers;

  public Team(List<TournamentPlayerInterface> teamMembers)
  {
        this.teamMembers = teamMembers;
        name = "";
        record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
    }

    public Team(String name, List<TournamentPlayerInterface> teamMembers) {
        this.name = name;
        this.teamMembers = teamMembers;
        record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
    }

    public Team(TournamentPlayerInterface p1) {
        teamMembers = new ArrayList<>();
        teamMembers.add(p1);
        name = "";
        record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
    }

    /**
     * @return the teamMembers
     */
    @Override
    public List<TournamentPlayerInterface> getTeamMembers() {
        return teamMembers;
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder((name.trim().isEmpty() ? ""
              : "Team " + name + "("));
      teamMembers.forEach((p) ->
      {
        String val = sb.toString();
        if (!val.trim().isEmpty() && !val.endsWith("("))
        {
          sb.append(", ");
        }
        sb.append(p.toString());
      });
      if (!sb.toString().trim().isEmpty() && sb.toString().startsWith("Team"))
      {
            sb.append(")");
        }
        return sb.toString();
    }

    @Override
    public boolean hasMember(TournamentPlayerInterface member) {
        boolean found = false;
        for (TournamentPlayerInterface player : getTeamMembers()) {
            if (player.getName().equals(member.getName())) {
                found = true;
                break;
            }
        }
        return found;
    }

    @Override
    public RecordInterface getRecord() {
        return record;
  }
}
