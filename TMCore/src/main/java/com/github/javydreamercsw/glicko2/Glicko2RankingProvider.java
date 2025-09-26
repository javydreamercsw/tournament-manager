package com.github.javydreamercsw.glicko2;

import static forwardloop.glicko2s.Glicko2J.calculateNewRating;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import com.github.javydreamercsw.tournament.manager.api.standing.RankingProvider;
import forwardloop.glicko2s.Glicko2;
import forwardloop.glicko2s.Glicko2J;
import forwardloop.glicko2s.Result;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.lookup.ServiceProvider;
import scala.Tuple2;

@ServiceProvider(service = RankingProvider.class, position = 2)
public class Glicko2RankingProvider implements RankingProvider {

  private final Map<Integer, Glicko2> playerRatings = new HashMap<>();
  private static final double DEFAULT_RATING = 1500;
  private static final double DEFAULT_DEVIATION = 350;
  private static final double DEFAULT_VOLATILITY = 0.06;

  @Override
  public void addTeam(TeamInterface... teams) throws Exception {
    for (TeamInterface team : teams) {
      for (TournamentPlayerInterface player : team.getTeamMembers()) {
        if (!playerRatings.containsKey(player.getID())) {
          Glicko2 glicko2 = new Glicko2(DEFAULT_RATING, DEFAULT_DEVIATION, DEFAULT_VOLATILITY);
          playerRatings.put(player.getID(), glicko2);
        }
      }
    }
  }

  @Override
  public String getName() {
    return "Glicko 2";
  }

  public void updateRatings(List<TeamInterface> teams, List<Integer> ranks) {
    Map<Integer, Glicko2> updatedRatings = new HashMap<>();

    for (int i = 0; i < teams.size(); i++) {
      TeamInterface team1 = teams.get(i);
      if (team1.getTeamMembers().size() == 1) {
        TournamentPlayerInterface p1 = team1.getTeamMembers().get(0);
        Glicko2 player = playerRatings.get(p1.getID());
        List<Tuple2<Glicko2, Result>> results = new ArrayList<>();

        for (int j = 0; j < teams.size(); j++) {
          if (i == j) continue;

          TeamInterface team2 = teams.get(j);
          if (team2.getTeamMembers().size() == 1) {
            TournamentPlayerInterface p2 = team2.getTeamMembers().get(0);
            Glicko2 opponent = playerRatings.get(p2.getID());

            Result matchResult;
            if (ranks.get(i) < ranks.get(j)) { // p1 wins
              matchResult = Glicko2J.Win; // Use Glicko2J.Win
            } else if (ranks.get(i) > ranks.get(j)) { // p1 loses
              matchResult = Glicko2J.Loss; // Use Glicko2J.Loss
            } else { // draw
              matchResult = null;
            }
            if (matchResult != null) {
              results.add(new Tuple2<>(opponent, matchResult));
            }
          }
        }

        Glicko2 newRating = calculateNewRating(player, results);
        updatedRatings.put(p1.getID(), newRating);
      }
    }

    // Update the ratings in the main map
    playerRatings.putAll(updatedRatings);
  }

  public Glicko2 getRating(int playerId) {
    return playerRatings.get(playerId);
  }
}
