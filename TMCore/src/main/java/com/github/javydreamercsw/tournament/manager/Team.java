package com.github.javydreamercsw.tournament.manager;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import com.github.javydreamercsw.tournament.manager.api.standing.RecordInterface;
import de.gesundkrank.jskills.Rating;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.openide.util.Lookup;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Team extends de.gesundkrank.jskills.Team implements TeamInterface {
  private static final long serialVersionUID = 8398904493889254598L;
  private final String name;
  private RecordInterface record = null;
  private final int teamId;

  public Team(int id, List<TournamentPlayerInterface> teamMembers) {
    this(id, "", teamMembers);
  }

  public Team(int id, String name, List<TournamentPlayerInterface> teamMembers) {
    this.name = name;
    teamMembers.forEach(
        member -> {
          put(member, new Rating(0, 0));
        });
    record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
    this.teamId = id;
  }

  public Team(int id, TournamentPlayerInterface p1) {
    this(id, "", Arrays.asList(p1));
  }

  /**
   * @return the teamMembers
   */
  @Override
  public List<TournamentPlayerInterface> getTeamMembers() {
    List<TournamentPlayerInterface> members = new ArrayList<>();
    keySet()
        .forEach(
            player -> {
              members.add((TournamentPlayerInterface) player);
            });
    return Collections.unmodifiableList(members);
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
    StringBuilder sb = new StringBuilder((name.trim().isEmpty() ? "" : "Team " + name + "("));
    keySet()
        .forEach(
            (p) -> {
              String val = sb.toString();
              if (!val.trim().isEmpty() && !val.endsWith("(")) {
                sb.append(", ");
              }
              sb.append(p.toString());
            });
    if (!sb.toString().trim().isEmpty() && sb.toString().startsWith("Team")) {
      sb.append(")");
    }
    return sb.toString();
  }

  @Override
  public boolean hasMember(TournamentPlayerInterface member) {
    return hasMember(member.getID());
  }

  @Override
  public RecordInterface getRecord() {
    return record;
  }

  @Override
  public TeamInterface createTeam(int id, String name, List<TournamentPlayerInterface> players) {
    return new Team(id, name, players);
  }

  /**
   * @return the teamId
   */
  @Override
  public int getTeamId() {
    return teamId;
  }

  @Override
  public boolean hasMember(int memberId) {
    boolean found = false;
    for (TournamentPlayerInterface player : getTeamMembers()) {
      if (player.getID() == memberId) {
        found = true;
        break;
      }
    }
    return found;
  }
}
