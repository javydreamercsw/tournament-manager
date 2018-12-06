package com.github.javydreamercsw.tournament.manager.api;

import java.util.List;

import com.github.javydreamercsw.tournament.manager.api.standing.RecordInterface;

import de.gesundkrank.jskills.ITeam;

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
   * Checks if a player is part of this team.
   *
   * @param memberId member id to look for.
   * @return true if found
   */
  boolean hasMember(int memberId);

  /**
   * Get the team's record.
   *
   * @return
   */
  RecordInterface getRecord();

  /**
   * Create a team.
   *
   * @param id Team id.
   * @param name Team name.
   * @param players Team members.
   * @return
   */
  TeamInterface createTeam(int id, String name,
          List<TournamentPlayerInterface> players);

  /**
   * Get the team's id.
   *
   * @return ID for this team.
   */
  public int getTeamId();
}
