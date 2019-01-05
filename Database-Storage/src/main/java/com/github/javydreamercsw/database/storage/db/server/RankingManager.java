package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;

public class RankingManager
{
  private final static Comparator COMP = Comparator
          .comparingDouble((TeamHasFormatRecord thfr) -> thfr.getPoints())
          .thenComparingDouble(thfr -> thfr.getMean())
          .thenComparingDouble(thfr -> thfr.getStandardDeviation());

  public static Map<Integer, List<TeamHasFormatRecord>> getRankings(Format f)
  {
    return getRankings(f.getTeamHasFormatRecordList());
  }

  protected static Map<Integer, List<TeamHasFormatRecord>> getRankings(List<TeamHasFormatRecord> items)
  {
    Map<Integer, List<TeamHasFormatRecord>> rankings = new HashMap<>();
    // Sort the list based on points, mean and SD (in that order)
    Collections.sort(items, COMP.reversed());

    // Now create the map with the ranking
    int rank = 1;
    for (TeamHasFormatRecord thfr : items)
    {
      if (rankings.containsKey(rank) && !rankings.get(rank).isEmpty())
      {
        // Compare to the current rank.
        if (compare(rankings.get(rank).get(0), thfr) > 0)
        {
          rank += rankings.get(rank).size();
        }
      }

      // Make sure there's an entry for the current rank.
      if (!rankings.containsKey(rank))
      {
        rankings.put(rank, new ArrayList<>());
      }

      rankings.get(rank).add(thfr);
    }
    return rankings;
  }

  public static int compare(TeamHasFormatRecord t1, TeamHasFormatRecord t2)
  {
    return COMP.compare(t1, t2);
  }
}
