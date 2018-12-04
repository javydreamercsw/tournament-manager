package com.github.javydreamercsw.tournament.manager.elimination.tournament;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;

@ServiceProvider(service = TournamentInterface.class)
public class SingleElimination extends Elimination
{
  public SingleElimination()
  {
    super(1, false);
  }

  public SingleElimination(int winPoints, int lossPoints, int drawPoints, 
          boolean pairAlikeRecords)
  {
    super(1, winPoints, lossPoints, drawPoints, pairAlikeRecords);
  }
  
  
  
    @Override
  public String getName()
  {
    return "Single Elimination";
  }

  @Override
  public TournamentInterface createTournament(List<TeamInterface> teams, 
          int winPoints, int lossPoints, int drawPoints)
  {
    SingleElimination se = new SingleElimination(winPoints, lossPoints, 
            drawPoints, true);
    se.teams.addAll(teams);
    return se;
  }
}
