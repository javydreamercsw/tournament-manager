package net.sourceforge.javydreamercsw.tournament.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.openide.util.Lookup;

import de.gesundkrank.jskills.Rating;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Team extends de.gesundkrank.jskills.Team implements TeamInterface
{
  private static final long serialVersionUID = 8398904493889254598L;
  private final String name;
  private RecordInterface record = null;

  public Team(List<TournamentPlayerInterface> teamMembers)
  {
    this("", teamMembers);
  }

  public Team(String name, List<TournamentPlayerInterface> teamMembers)
  {
    this.name = name;
    teamMembers.forEach(member ->
    {
      put(member, new Rating(0, 0));
    });
    record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
  }

  public Team(TournamentPlayerInterface p1)
  {
    this("", Arrays.asList(p1));
  }

  /**
   * @return the teamMembers
   */
  @Override
  public List<TournamentPlayerInterface> getTeamMembers()
  {
    List<TournamentPlayerInterface> members = new ArrayList<>();
    keySet().forEach(player ->
    {
      members.add((TournamentPlayerInterface) player);
    });
    return Collections.unmodifiableList(members);
  }

  /**
   * @return the name
   */
  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder((name.trim().isEmpty() ? ""
            : "Team " + name + "("));
    keySet().forEach((p) ->
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
  public boolean hasMember(TournamentPlayerInterface member)
  {
    boolean found = false;
    for (TournamentPlayerInterface player : getTeamMembers())
    {
      if (player.getName().equals(member.getName()))
      {
        found = true;
        break;
      }
    }
    return found;
  }

  @Override
  public RecordInterface getRecord()
  {
    return record;
  }

  @Override
  public TeamInterface createTeam(String name, List<TournamentPlayerInterface> players)
  {
    return new Team(name, players);
  }
}
