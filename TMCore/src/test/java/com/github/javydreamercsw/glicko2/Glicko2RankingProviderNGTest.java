package com.github.javydreamercsw.glicko2;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import com.github.javydreamercsw.tournament.manager.Team;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.standing.RankingProvider;
import forwardloop.glicko2s.Glicko2;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;
import org.testng.annotations.Test;

public class Glicko2RankingProviderNGTest {

  private Glicko2RankingProvider instance;

  public Glicko2RankingProviderNGTest() {
    Collection<? extends RankingProvider> providers =
        Lookup.getDefault().lookupAll(RankingProvider.class);
    for (RankingProvider rp : providers) {
      if (rp instanceof Glicko2RankingProvider) {
        instance = (Glicko2RankingProvider) rp;
        break;
      }
    }
    assertNotNull(instance);
  }

  @Test
  public void testMatch() throws Exception {
    int playerCount = 0;
    UIPlayer p1 = new UIPlayer("Player 1", ++playerCount);
    UIPlayer p2 = new UIPlayer("Player 2", ++playerCount);
    TeamInterface t1 = new Team(1, p1);
    TeamInterface t2 = new Team(2, p2);
    TeamInterface[] teams = new TeamInterface[] {t1, t2};
    instance.addTeam(teams);

    Glicko2 r1 = instance.getRating(p1.getID());
    Glicko2 r2 = instance.getRating(p2.getID());

    // Default rating for newPlayerRating() is 1500.0
    assertEquals(r1.rating(), 1_500.0, 0.0);
    assertEquals(r2.rating(), 1_500.0, 0.0);

    List<TeamInterface> teamList = new ArrayList<>();
    teamList.add(t1);
    teamList.add(t2);

    instance.updateRatings(teamList, Arrays.asList(1, 2));

    r1 = instance.getRating(p1.getID());
    r2 = instance.getRating(p2.getID());

    // Player 1 won, so their rating should increase and deviation decrease.
    assertTrue(r1.rating() > 1_500.0);
    assertTrue(r1.ratingDeviation() < 350.0); // Default deviation is 350.0

    // Player 2 lost, so their rating should decrease and deviation decrease.
    assertTrue(r2.rating() < 1_500.0);
    assertTrue(r2.ratingDeviation() < 350.0);
  }
}
