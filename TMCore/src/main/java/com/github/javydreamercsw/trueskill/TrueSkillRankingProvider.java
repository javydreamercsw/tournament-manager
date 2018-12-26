package com.github.javydreamercsw.trueskill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import com.github.javydreamercsw.tournament.manager.api.standing.RankingProvider;

import de.gesundkrank.jskills.GameInfo;
import de.gesundkrank.jskills.Player;
import de.gesundkrank.jskills.SkillCalculator;
import de.gesundkrank.jskills.Team;
import de.gesundkrank.jskills.trueskill.FactorGraphTrueSkillCalculator;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;
import de.gesundkrank.jskills.trueskill.TwoTeamTrueSkillCalculator;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = RankingProvider.class)
public class TrueSkillRankingProvider implements RankingProvider
{
  private GameInfo gameInfo;
  private final Map<List<Integer>, Team> teamList = new HashMap<>();
  private SkillCalculator calculator;

  @Override
  public void addTeam(TeamInterface... teams) throws Exception
  {
    for (TeamInterface team : teams)
    {
      List<Integer> ids = new ArrayList<>();
      Team t = null;
      for (TournamentPlayerInterface tm : team.getTeamMembers())
      {
        Player<Integer> p = new Player<>(tm.getID());
        if (t == null)
        {
          t = new Team(p, getGameInfo().getDefaultRating());
        }
        else
        {
          t.addPlayer(p, getGameInfo().getDefaultRating());
        }
        if (!ids.contains(tm.getID()))
        {
          ids.add(tm.getID());
        }
        else
        {
          throw new Exception("Team with duplicated ids!\n" + team.toString());
        }
      }
      teamList.put(ids, t);
    }
  }

  /**
   * @return the gameInfo
   */
  public GameInfo getGameInfo()
  {
    if (gameInfo == null)
    {
      gameInfo = GameInfo.getDefaultGameInfo();
    }
    return gameInfo;
  }

  /**
   * @param gameInfo the gameInfo to set
   */
  public void setGameInfo(GameInfo gameInfo)
  {
    this.gameInfo = gameInfo;
  }

  /**
   * @return the calculator
   */
  public SkillCalculator getCalculator()
  {
    if (getTeamList().size() == 2)
    {
      //Two teams, check how many players in each to pick the right calculator.
      boolean multiplayerTeam = false;
      for (Team t : getTeamList())
      {
        if (t.size() > 1)
        {
          multiplayerTeam = true;
          break;
        }
      }
      if (multiplayerTeam)
      {
        calculator = new TwoTeamTrueSkillCalculator();
      }
      else
      {
        calculator = new TwoPlayerTrueSkillCalculator();
      }
    }
    else
    {
      calculator = new FactorGraphTrueSkillCalculator();
    }
    return calculator;
  }

  /**
   * @return the teamList
   */
  public List<Team> getTeamList()
  {
    ArrayList<Team> teams = new ArrayList<>();
    teams.addAll(teamList.values());
    return Collections.unmodifiableList(teams);
  }
}
