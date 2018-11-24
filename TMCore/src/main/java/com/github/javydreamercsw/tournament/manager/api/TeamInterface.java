package com.github.javydreamercsw.tournament.manager.api;

import java.util.List;

import de.gesundkrank.jskills.ITeam;

import com.github.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TeamInterface extends ITeam
{

  /**
   * @return the team's name
   */
  public String getName();

  /**
   * @return the team members
   */
  public List<TournamentPlayerInterface> getTeamMembers();

  /**
   * Checks if a player is part of this team.
   *
   * @param member member to look for.
   * @return true if found
   */
  boolean hasMember(TournamentPlayerInterface member);

  /**
   * Get the team's record.
   *
   * @return
   */
  RecordInterface getRecord();

  /**
   * Create a team.
   *
   * @param name Team name.
   * @param players Team members.
   * @return
   */
  TeamInterface createTeam(String name, List<TournamentPlayerInterface> players);
}
