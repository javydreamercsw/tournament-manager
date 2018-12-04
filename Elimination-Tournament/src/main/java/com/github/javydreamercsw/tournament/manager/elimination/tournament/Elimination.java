package com.github.javydreamercsw.tournament.manager.elimination.tournament;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;

import com.github.javydreamercsw.tournament.manager.AbstractTournament;
import com.github.javydreamercsw.tournament.manager.api.Encounter;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public abstract class Elimination extends AbstractTournament
        implements TournamentInterface
{

  private static final Logger LOG
          = Logger.getLogger(Elimination.class.getName());
  private final int eliminations;

  /**
   * Provide eliminations and pairing option. Defaults to 3 points for a win, 0
   * for loses and 1 for draws.
   *
   * @param eliminations amount of eliminations before elimination
   * @param pairAlikeRecords true to pair alike records, false for random
   */
  public Elimination(int eliminations, boolean pairAlikeRecords)
  {
    super(3, 0, 1, pairAlikeRecords);
    this.eliminations = eliminations;
  }

  /**
   * Provide eliminations and pairing option. Defaults to one elimination, 3
   * points for a win, 0 for loses, 1 for draws and random pairing.
   */
  public Elimination()
  {
    super(3, 0, 1, false);
    this.eliminations = 1;
  }

  /**
   *
   * @param eliminations amount of eliminations before elimination
   * @param winPoints points to be allocated for a win
   * @param lossPoints points to be allocated for a loss
   * @param drawPoints points to be allocated for a draw
   * @param pairAlikeRecords true to pair alike records, false for random
   */
  public Elimination(int eliminations, int winPoints, int lossPoints,
          int drawPoints, boolean pairAlikeRecords)
  {
    super(winPoints, lossPoints, drawPoints, pairAlikeRecords);
    this.eliminations = eliminations;
  }

  @Override
  public boolean isTeamActive(TeamInterface t)
  {
    boolean result = false;
    for (TeamInterface team : getActiveTeams())
    {
      for (TournamentPlayerInterface player : team.getTeamMembers())
      {
        for (TournamentPlayerInterface p : t.getTeamMembers())
        {
          if (player.getName().equals(p.getName()))
          {
            result = true;
            break;
          }
        }
      }
    }
    return result;
  }

  @Override
  public Map<Integer, Encounter> getPairings()
  {
    synchronized (getActiveTeams())
    {
      if (pairingHistory.get(getRound()) == null)
      {
        //Remove teams with loses from tournament
        List<TeamInterface> toRemove
                = new ArrayList<>();
        for (TeamInterface team : getActiveTeams())
        {
          //Loss or draw gets you eliminated
          if (team.getTeamMembers().get(0).getRecord().getLosses()
                  + team.getTeamMembers().get(0).getRecord()
                          .getDraws() >= eliminations)
          {
            toRemove.add(team);
          }
        }
        List<TeamInterface> errors = new ArrayList<>();
        toRemove.forEach((t) ->
        {
          if (!errors.contains(t))
          {
            try
            {
              LOG.log(Level.FINE, "Removing player: {0}", t.toString());
              removeTeam(t);
            }
            catch (TournamentException ex)
            {
              LOG.log(Level.FINE, null, ex);
              errors.add(t);
            }
          }
        });
        if (pairAlikeRecords)
        {
          Map<Integer, Encounter> pairings
                  = new HashMap<>();
          Integer[] exclude;
          Random rnd = new Random();
          //This will hold the reminder unpaired player due to odd number of players with same record.
          TeamInterface pending = null;
          for (Entry<Integer, List<TeamInterface>> rankings : getRankings().entrySet())
          {
            //Pair all people with same ranking together
            exclude = new Integer[]
            {
            };
            List<TeamInterface> rankPlayers = rankings.getValue();
            List<TeamInterface> players = new ArrayList<>();
            //Only use active players
            for (TeamInterface p : rankPlayers)
            {
              if (isTeamActive(p))
              {
                players.add(p);
              }
            }
            if (pending != null)
            {
              //We got someone pending from previous level, pair with him
              //Pair them
              TeamInterface opp = null;
              int lucky;
              while (opp == null && exclude.length < players.size())
              {
                lucky = rnd.nextInt(players.size());
                opp = players.get(lucky);
                //Exclude the unlucky one from the rest of processing
                exclude = ArrayUtils.add(exclude, lucky);
                if (isTeamActive(opp))
                {
                  addPairing(pairings, pending, opp);
                  LOG.log(Level.INFO, "Pairing {0} from higher level with {1}",
                          new Object[]
                          {
                            pending, opp
                          });
                  pending = null;
                }
                else
                {
                  opp = null;
                }
              }
            }

            if (players.size() % 2 != 0)
            {
              //Someone will pair with someone in a lower level, lucky...
              int lucky = rnd.nextInt(players.size());
              pending = players.get(lucky);
              //Exclude the lucky one from the rest of processing
              exclude = ArrayUtils.add(exclude, lucky);
              LOG.log(Level.INFO, "Pairing {0} with lower level", pending);
            }
            //We have an even number, pair them together
            while (players.size() - exclude.length >= 2)
            {
              LOG.log(Level.FINE, "exclude {0} players: {1}",
                      new Object[]
                      {
                        exclude.length, players.size()
                      });
              int player1
                      = getRandomWithExclusion(rnd, 0,
                              players.size() - 1, exclude);
              exclude = ArrayUtils.add(exclude, player1);
              int player2
                      = getRandomWithExclusion(rnd, 0,
                              players.size() - 1, exclude);
              exclude = ArrayUtils.add(exclude, player2);
              TeamInterface team1 = players.get(player1);
              TeamInterface team2 = players.get(player2);
              //Pair them
              if (isTeamActive(team1) && isTeamActive(team2))
              {
                addPairing(pairings, team1, team2);
              }
            }
          }
          if (pending != null)
          {
            if (getActiveTeams().size() == 1)
            {
              //Got our winner
            }
            else
            {
              //We got someone pending. Pair with him BYE
              addPairing(pairings, pending, BYE);
              LOG.log(Level.INFO, "Pairing {0} with BYE", pending);
            }
          }
          pairingHistory.put(getRound(), pairings);
        }
        else
        {
          super.getPairings();
        }
      }
    }

    return pairingHistory.get(getRound());
  }

  @Override
  public int getMinimumAmountOfRounds()
  {
    /**
     * Assuming two competitors per match, if there are n competitors, there
     * will be r = log {2} n rounds required, or if there are r rounds, there
     * will be n= 2^r competitors. In the opening round, 2^r - n competitors
     * will get a bye.
     */
    return log(teams.size(), 2);
  }
}
