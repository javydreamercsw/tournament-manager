package net.sourceforge.javydreamercsw.trueskill;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.openide.util.Lookup;

import de.gesundkrank.jskills.IPlayer;
import de.gesundkrank.jskills.ITeam;
import de.gesundkrank.jskills.Rating;
import de.gesundkrank.jskills.trueskill.TwoPlayerTrueSkillCalculator;
import net.sourceforge.javydreamercsw.tournament.manager.Player;
import net.sourceforge.javydreamercsw.tournament.manager.Team;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.RankingProvider;

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
   * Test of addMatch method, of class TrueSkillRankingProvider.
   *
   * @throws java.lang.Exception
   */
  @Test
  public void testMatch() throws Exception
  {
    System.out.println("addMatch");
    int playerCount = 0;
    Player p1 = new Player("Player 1", ++playerCount);
    Player p2 = new Player("Player 2", ++playerCount);
    TeamInterface[] teams = new TeamInterface[]
    {
      new Team(p1), new Team(p2)
    };
    instance.addMatch("Test Match", teams);
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

    Player p3 = new Player("Player 3", ++playerCount);
    Player p4 = new Player("Player 4", ++playerCount);
    teams = new TeamInterface[]
    {
      new Team(p3), new Team(p4)
    };

    instance.addMatch("Test Match 2", teams);

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
      new Team(p1), new Team(p2), new Team(p3), new Team(p4)
    };

    instance.addMatch("Test Match 3", teams);

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
