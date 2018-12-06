package com.github.javydreamercsw.tournament.manager.elimination.tournament;

import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;

@ServiceProvider(service = TournamentInterface.class)
public class DoubleElimination extends Elimination
{
  public DoubleElimination()
  {
    super(2, false);
  }

  public DoubleElimination(int winPoints, int lossPoints, int drawPoints,
          boolean pairAlikeRecords)
  {
    super(2, winPoints, lossPoints, drawPoints, pairAlikeRecords);
  }

  @Override
  public String getName()
  {
    return "Double Elimination";
  }

  @Override
  public TournamentInterface createTournament(List<TeamInterface> teams,
          int winPoints, int lossPoints, int drawPoints)
          throws TournamentSignupException
  {
    DoubleElimination de = new DoubleElimination(winPoints, lossPoints,
            drawPoints, true);
    de.addTeams(teams);
    return de;
  }
}
