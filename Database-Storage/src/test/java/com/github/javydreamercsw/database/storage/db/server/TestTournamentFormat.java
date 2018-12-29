package com.github.javydreamercsw.database.storage.db.server;


import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.AbstractTournament;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;

@ServiceProvider(service = TournamentInterface.class)
public class TestTournamentFormat extends AbstractTournament
        implements TournamentInterface
{
  public TestTournamentFormat()
  {
    super(3, 0, 1, true);
  }

  public TestTournamentFormat(int winPoints, int lossPoints, int drawPoints, 
          boolean pairAlikeRecords)
  {
    super(winPoints, lossPoints, drawPoints, pairAlikeRecords);
  }

  public TestTournamentFormat(int winPoints, int lossPoints, int drawPoints)
  {
    super(winPoints, lossPoints, drawPoints);
  }

  @Override
  public String getName()
  {
    return "Dummy Tournament Format";
  }

  @Override
  public int getMinimumAmountOfRounds()
  {
    return 2;
  }

  @Override
  public TournamentInterface createTournament(List<TeamInterface> teams, 
          int winPoints, int lossPoints, int drawPoints) 
          throws TournamentSignupException
  {
    TestTournamentFormat dummy = new TestTournamentFormat(winPoints, lossPoints, 
            drawPoints, true);
    dummy.addTeams(teams);
    return dummy;
  }
}
