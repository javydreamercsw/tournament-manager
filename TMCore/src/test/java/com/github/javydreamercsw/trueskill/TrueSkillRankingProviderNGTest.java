package com.github.javydreamercsw.trueskill;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.openide.util.Lookup;
import org.testng.annotations.Test;

import com.github.javydreamercsw.tournament.manager.Team;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.standing.RankingProvider;

import de.gesundkrank.jskills.IPlayer;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class TrueSkillRankingProviderNGTest
{
  private final RankingProvider instance
          = Lookup.getDefault().lookup(RankingProvider.class);

  public TrueSkillRankingProviderNGTest()
  {
    assertNotNull(instance);
    assertTrue(instance instanceof TrueSkillRankingProvider);
  }

  /**
   * Test of addTeam method, of class TrueSkillRankingProvider.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMatch() throws Exception
  {
    System.out.println("addMatch");
    int playerCount = 0;
    UIPlayer p1 = new UIPlayer("Player 1", ++playerCount);
    UIPlayer p2 = new UIPlayer("Player 2", ++playerCount);
    TeamInterface[] teams = new TeamInterface[]
    {
      new Team(1, p1), new Team(2, p2)
    };
    instance.addTeam(teams);
    TrueSkillRankingProvider p = (TrueSkillRankingProvider) instance;
    List<de.gesundkrank.jskills.Team> teamList = p.getTeamList();
    assertEquals(teamList.size(), playerCount);

    assertTrue(p.getCalculator() instanceof TwoPlayerTrueSkillCalculator);

    double quality = p.getCalculator().calculateMatchQuality(p.getGameInfo(),
            de.gesundkrank.jskills.Team.concat(teamList.toArray(new ITeam[0])));

    System.out.println("Quality: " + quality);
    assertTrue(quality > 0);

    Map<IPlayer, Rating> newRatings
            = p.getCalculator().calculateNewRatings(p.getGameInfo(),
                    de.gesundkrank.jskills.Team.concat(teamList
                            .toArray(new ITeam[0])),
                    new int[]
                    {
                      1, 2
                    });
    assertEquals(newRatings.size(), playerCount);

    UIPlayer p3 = new UIPlayer("Player 3", ++playerCount);
    UIPlayer p4 = new UIPlayer("Player 4", ++playerCount);
    teams = new TeamInterface[]
    {
      new Team(3, p3), new Team(4, p4)
    };

    instance.addTeam(teams);

    teamList = p.getTeamList();

    assertEquals(teamList.size(), playerCount);

    quality = p.getCalculator().calculateMatchQuality(p.getGameInfo(),
            de.gesundkrank.jskills.Team.concat(teamList.subList(2, 4)
                    .toArray(new ITeam[0])));

    System.out.println("Quality: " + quality);
    assertTrue(quality > 0);

    newRatings
            = p.getCalculator().calculateNewRatings(p.getGameInfo(),
                    de.gesundkrank.jskills.Team.concat(teamList.subList(2, 4)
                            .toArray(new ITeam[0])),
                    new int[]
                    {
                      1, 2
                    });
    assertEquals(newRatings.size(), 2);

    teams = new TeamInterface[]
    {
      new Team(1, p1), new Team(2, p2), new Team(3, p3), new Team(4, p4)
    };

    instance.addTeam(teams);

    teamList = p.getTeamList();

    assertEquals(teamList.size(), 4);

    quality = p.getCalculator().calculateMatchQuality(p.getGameInfo(),
            de.gesundkrank.jskills.Team.concat(teamList.subList(2, 4)
                    .toArray(new ITeam[0])));

    System.out.println("Quality: " + quality);
    assertTrue(quality > 0);

    newRatings
            = p.getCalculator().calculateNewRatings(p.getGameInfo(),
                    de.gesundkrank.jskills.Team.concat(teamList
                            .toArray(new ITeam[0])),
                    new int[]
                    {
                      1, 2, 3, 4
                    });

    assertEquals(newRatings.size(), playerCount);
    int i = 0;
    for (Entry<IPlayer, Rating> entry : newRatings.entrySet())
    {
      System.out.println(entry.getKey() + ": " + entry.getValue());
      assertEquals((int) Integer.valueOf(entry.getKey().toString()), ++i);
    }
  }
}
