package net.sourceforge.javydreamercsw.tournament.manager.api;

import java.util.List;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public interface TeamInterface {

    /**
     * @return the team's name
     */
    String getName();

    /**
     * @return the team members
     */
    List<TournamentPlayerInterface> getTeamMembers();

    /**
     * Checks if a player is part of this team.
     *
     * @param member member to look for.
     * @return true if found
     */
    boolean hasMember(TournamentPlayerInterface member);
}
