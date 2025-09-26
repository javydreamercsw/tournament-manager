package com.github.javydreamercsw.tournament.manager.api;

import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;
import java.util.List;
import java.util.Map;
import org.netbeans.spi.project.LookupProvider;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TournamentManagerInterface extends LookupProvider {

  /** Start the next round. */
  void nextRound();

  /**
   * Register a team on the tournament.
   *
   * @param team Team to register.
   * @throws TournamentSignupException if there's a problem adding the team.
   */
  void registerTeam(TeamInterface team) throws TournamentSignupException;

  /**
   * Unregister a team on the tournament.
   *
   * @param team Team to unregister.
   * @throws TournamentSignupException if there's a problem removing the team.
   */
  void unregisterTeam(TeamInterface team) throws TournamentSignupException;

  /**
   * Create a tournament.
   *
   * @param tournament Tournament to create.
   */
  void createTournament(TournamentInterface tournament);

  /**
   * Set tournament description.
   *
   * @param description
   */
  void setDescription(String description);

  /**
   * Get the current rankings.
   *
   * @return current rankings
   */
  Map<Integer, List<TeamInterface>> getRankings();
}
