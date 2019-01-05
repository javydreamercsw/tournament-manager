/*
 * Abstract tournament implementation.
 */
package com.github.javydreamercsw.tournament.manager;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.ArrayUtils;
import org.openide.util.Lookup;

import com.github.javydreamercsw.tournament.manager.api.Encounter;
import com.github.javydreamercsw.tournament.manager.api.EncounterResult;
import com.github.javydreamercsw.tournament.manager.api.ResultListener;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentListener;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import com.github.javydreamercsw.tournament.manager.api.Variables;
import com.github.javydreamercsw.tournament.manager.signup.TournamentSignupException;

public abstract class AbstractTournament implements TournamentInterface
{
  private int format = 1;
  /**
   * Amount of non-wins to be eliminated.
   */
  private final int eliminations;
  /**
   * Encounter id
   */
  protected int encounterCount = 1;
  /**
   * Current round number
   */
  protected int round = 0;
  /**
   * Teams that registered.
   */
  private final List<TeamInterface> teams = new ArrayList<>();
  /**
   * Current list of active teams. This is an exact copy of teams before the
   * tournament starts. After it starts, teams that get eliminated or drop out
   * are no longer on this list.
   *
   * The last at the end only the winner(s) will be on the list.
   */
  private final List<TeamInterface> teamsCopy = new ArrayList<>();
  /**
   * History of the pairings for the tournament.
   */
  protected final Map<Integer, Map<Integer, Encounter>> pairingHistory
          = new LinkedHashMap<>();
  private final static Logger LOG
          = Logger.getLogger(AbstractTournament.class.getSimpleName());

  /**
   * Amount of points for a win.
   */
  private final int winPoints;

  /**
   * Amount of points for a loss.
   */
  private final int lossPoints;

  /**
   * Amount of points for a draw.
   */
  private final int drawPoints;
  private long no_show_time;
  private long round_time;
  private final List<TournamentListener> listeners
          = new ArrayList<>();
  protected final boolean pairAlikeRecords;
  private int id = -1;

  public AbstractTournament(int winPoints, int lossPoints, int drawPoints,
          int eliminations, boolean pairAlikeRecords)
  {
    this.winPoints = winPoints;
    this.lossPoints = lossPoints;
    this.drawPoints = drawPoints;
    this.pairAlikeRecords = pairAlikeRecords;
    this.eliminations = eliminations;
  }

  public AbstractTournament(int winPoints, int lossPoints, int drawPoints,
          int eliminations)
  {
    this.winPoints = winPoints;
    this.lossPoints = lossPoints;
    this.drawPoints = drawPoints;
    this.pairAlikeRecords = false;
    this.eliminations = eliminations;
  }

  public AbstractTournament(int winPoints, int lossPoints, int drawPoints,
          boolean pairAlikeRecords)
  {
    this.winPoints = winPoints;
    this.lossPoints = lossPoints;
    this.drawPoints = drawPoints;
    this.pairAlikeRecords = pairAlikeRecords;
    this.eliminations = 1;
  }

  public AbstractTournament(int winPoints, int lossPoints, int drawPoints)
  {
    this.winPoints = winPoints;
    this.lossPoints = lossPoints;
    this.drawPoints = drawPoints;
    this.pairAlikeRecords = false;
    this.eliminations = 1;
  }

  @Override
  public int getRound()
  {
    return round;
  }

  @Override
  public void addTeam(TeamInterface team) throws TournamentSignupException
  {
    //Loop thru teamsCopy to check for name
    boolean found = false;
    for (TeamInterface t : getActiveTeams())
    {
      for (TournamentPlayerInterface tpi : t.getTeamMembers())
      {
        for (TournamentPlayerInterface newtpi : team.getTeamMembers())
        {
          if (tpi.get(Variables.PLAYER_NAME.getDisplayName())
                  .equals(newtpi.get(Variables.PLAYER_NAME.getDisplayName())))
          {
            found = true;
            break;
          }
        }
      }
    }
    if (!found)
    {
      getActiveTeams().add(team);
      teams.add(team);
    }
    else
    {
      throw new TournamentSignupException(
              MessageFormat.format(
                      "Team already signed: {0}",
                      team));
    }
  }

  @Override
  public void removeTeam(TeamInterface team) throws TournamentSignupException,
          TournamentException
  {
    //Loop thru teamsCopy to check for name
    boolean found = false;
    List<TeamInterface> toRemove = new ArrayList<>();
    for (TeamInterface existingTeam : getActiveTeams())
    {
      for (TournamentPlayerInterface player : existingTeam.getTeamMembers())
      {
        for (TournamentPlayerInterface newPlayer : team.getTeamMembers())
        {
          if (player.getName().equals(newPlayer.getName()))
          {
            toRemove.add(existingTeam);
            found = true;
            break;
          }
        }
      }
    }
    for (TeamInterface remove : toRemove)
    {
      getActiveTeams().remove(remove);

      // If we haven't started remove it from the overall list as well.
      teams.remove(remove);

      // Resolve any pending Encounters by forfeiting
      for (Encounter encounter : getPairings().values())
      {
        if (encounter.getEncounterSummary().containsKey(remove))
        {
          if (encounter.getEncounterSummary().size() == 2)
          {
            // Simple case where leaving forfeits to the other team.
            encounter.getEncounterSummary().entrySet().forEach(entry ->
            {
              if (entry.getKey().getName().equals(remove.getName()))
              {
                encounter.getEncounterSummary().put(entry.getKey(),
                        EncounterResult.NO_SHOW);
              }
              else
              {
                encounter.getEncounterSummary().put(entry.getKey(),
                        EncounterResult.WIN);
              }
            });
          }
          else
          {
            LOG.info(encounter.toString());
            throw new TournamentException(
                    MessageFormat.format(
                            "Team: {0} dropped and there are encounters that need "
                            + "to be manually resolved.", team));
          }
        }
      }
    }
    if (!found)
    {
      throw new TournamentSignupException(
              MessageFormat.format(
                      "Unable to remove Team. Team not found: {0}", team));
    }
  }

  /**
   * Pick a random number from start to end, excluding the specified numbers.
   *
   * @param rnd Random
   * @param start start number
   * @param end end number
   * @param exclude numbers to exclude.
   * @return random number.
   */
  public int getRandomWithExclusion(Random rnd, int start, int end, Integer[] exclude)
  {
    //Make sure exclude is sorted
    if (exclude != null)
    {
      Arrays.sort(exclude);
    }
    LOG.log(Level.FINE, "Start: {0}, End: {1}, Exclude: {2}",
            new Object[]
            {
              start, end,
              exclude == null ? "null" : exclude.length
            });
    int range = end - start + 1
            - (exclude == null ? 0 : exclude.length);
    assert range > 0 :
            MessageFormat.format("end: {0} start: {1} exlude: {2}",
                    end, start, exclude == null ? "null" : exclude.length);
    int random = start + rnd.nextInt(range);
    if (exclude != null)
    {
      for (int ex : exclude)
      {
        if (random < ex)
        {
          break;
        }
        random++;
      }
    }
    return random;
  }

  @Override
  public void nextRound() throws TournamentException
  {
    processRound(round);
    //Increase round
    round++;
    //Calculate pairings
    getPairings();
  }

  @Override
  public int getAmountOfTeams()
  {
    return getActiveTeams().size();
  }

  @Override
  public boolean roundComplete()
  {
    boolean result = true;
    //Check that all encounters have a set value.
    if (getPairings() != null)
    {
      for (Encounter e : getPairings().values())
      {
        for (Map.Entry<TeamInterface, EncounterResult> entry
                : e.getEncounterSummary().entrySet())
        {
          if (entry.getValue().equals(EncounterResult.UNDECIDED))
          {
            LOG.log(Level.FINE,
                    "{0} is still not resolved!", e.toString());
            result = false;
          }
        }
      }
    }
    return result;
  }

  @Override
  public TreeMap<Integer, List<TeamInterface>> getRankings()
  {
    TreeMap<Integer, List<TeamInterface>> rankings
            = new TreeMap<>((Integer o1, Integer o2) -> o2.compareTo(o1));
    getTeams().forEach((player) ->
    {
      int points = getPoints(player);
      if (rankings.get(points) == null)
      {
        List<TeamInterface> list = new ArrayList<>();
        list.add(player);
        rankings.put(points, list);
      }
      else
      {
        rankings.get(points).add(player);
      }
    });
    return rankings;
  }

  @Override
  public void displayRankings()
  {
    int i = 1;
    for (Entry<Integer, List<TeamInterface>> entry : getRankings().entrySet())
    {
      List<TeamInterface> tied = entry.getValue();
      String value;
      StringBuilder sb = new StringBuilder();
      if (tied.size() > 3)
      {
        value = MessageFormat.format("{0} tied", tied.size());
      }
      else
      {
        tied.forEach((t) ->
        {
          if (!sb.toString().isEmpty())
          {
            sb.append(", ");
          }
          sb.append(t.toString());
        });
        value = sb.toString();
      }
      System.out.println(MessageFormat.format("{0}. ({1}) {2}", i,
              entry.getKey(), value));
      i++;
    }
  }

  @Override
  public void processRound(int round)
  {
    //Remove teams with loses from tournament
    List<TeamInterface> toRemove
            = new ArrayList<>();
    for (TeamInterface team : getActiveTeams())
    {
      //Loss or draw gets you eliminated
      if (team.getTeamMembers().get(0).getRecord().getLosses()
              + team.getTeamMembers().get(0).getRecord().getDraws()
              >= getEliminations())
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
          LOG.log(Level.FINE, "Player: {0} is eliminated!", t.toString());
          removeTeam(t);
        }
        catch (TournamentException ex)
        {
          LOG.log(Level.FINE, null, ex);
          errors.add(t);
        }
      }
    });
  }

  @Override
  public Map<Integer, Encounter> getPairings()
  {
    synchronized (getActiveTeams())
    {
      if (pairingHistory.get(getRound()) == null && getActiveTeams().size() > 1)
      {
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
            rankPlayers.stream().filter((p)
                    -> (isTeamActive(p))).forEachOrdered((p) ->
            {
              players.add(p);
            });
            if (pending != null)
            {
              //We got someone pending from previous level, pair with him
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
          Map<Integer, Encounter> pairings
                  = new HashMap<>();
          Integer[] exclude = new Integer[]
          {
          };
          Random rnd = new Random();
          while (exclude.length < getActiveTeams().size()
                  && getActiveTeams().size() > 1)
          {
            int player1
                    = getRandomWithExclusion(rnd, 0,
                            getActiveTeams().size() - 1, exclude);
            exclude = ArrayUtils.add(exclude, player1);
            if (exclude.length == getActiveTeams().size())
            {
              //Only one player left, pair with Bye
              LOG.log(Level.FINE, "Pairing {0} vs. BYE",
                      getActiveTeams().get(player1).getName());
              addPairing(pairings,
                      getActiveTeams().get(player1), BYE);
            }
            else
            {
              int player2 = getRandomWithExclusion(rnd, 0,
                      getActiveTeams().size() - 1, exclude);
              addPairing(pairings,
                      getActiveTeams().get(player1),
                      getActiveTeams().get(player2));
              exclude = ArrayUtils.add(exclude, player2);
            }
          }
          pairingHistory.put(getRound(), pairings);
        }
      }
    }
    return pairingHistory.get(getRound());
  }

  @Override
  public TeamInterface getWinnerTeam()
  {
    TeamInterface winner = null;
    if (round > 1)
    {
      winner = (TeamInterface) getActiveTeams().toArray()[0];
    }
    return winner;
  }

  @Override
  public int getWinPoints()
  {
    return winPoints;
  }

  @Override
  public int getLossPoints()
  {
    return lossPoints;
  }

  @Override
  public int getDrawPoints()
  {
    return drawPoints;
  }

  @Override
  public int getPoints(TeamInterface team)
  {
    return team.getTeamMembers().get(0).getRecord().getWins() * getWinPoints()
            + team.getTeamMembers().get(0).getRecord().getDraws() * getDrawPoints();
  }

  /**
   * @return the teamsCopy
   */
  @Override
  public List<TeamInterface> getActiveTeams()
  {
    return teamsCopy;
  }

  @Override
  public void setNoShowTime(long time)
  {
    this.no_show_time = time;
  }

  @Override
  public long getNoShowTime()
  {
    return this.no_show_time;
  }

  @Override
  public void setRoundTime(long time)
  {
    this.round_time = time;
  }

  @Override
  public long getRoundTime()
  {
    return this.round_time;
  }

  @Override
  public void addTournamentListener(TournamentListener rtl)
  {
    if (!listeners.contains(rtl))
    {
      listeners.add(rtl);
    }
  }

  @Override
  public void removeTournamentListener(TournamentListener rtl)
  {
    if (listeners.contains(rtl))
    {
      listeners.remove(rtl);
    }
  }

  @Override
  public void updateResults(int encounterId,
          TeamInterface team, EncounterResult result)
          throws TournamentException
  {
    //Normal processing of results
    for (Map.Entry<Integer, Map<Integer, Encounter>> entry : pairingHistory.entrySet())
    {
      if (entry.getValue().containsKey(encounterId))
      {
        //Found round
        Encounter encounter = entry.getValue().get(encounterId);
        Map<TeamInterface, EncounterResult> encounterSummary
                = encounter.getEncounterSummary();
        for (Map.Entry<TeamInterface, EncounterResult> entry2 : encounterSummary.entrySet())
        {
          if (entry2.getKey().hasMember(team.getTeamMembers().get(0)))
          {
            //Apply result to this team
            encounter.updateResult(entry2.getKey(), result);
            for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class))
            {
              listener.updateResults(encounter);
            }
          }
          else
          {
            //Depending on the result for the target team assign result for the others
            switch (result)
            {
              case WIN:
                //All others are losers
                encounter.updateResult(entry2.getKey(), EncounterResult.LOSS);
                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class))
                {
                  listener.updateResults(encounter);
                }
                break;
              case NO_SHOW:
              case FORFEIT:
              //Fall thru
              case LOSS:
                //All others are winners
                encounter.updateResult(entry2.getKey(), EncounterResult.WIN);
                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class))
                {
                  listener.updateResults(encounter);
                }
                break;
              case DRAW:
                //Everyone drew
                encounter.updateResult(entry2.getKey(), EncounterResult.DRAW);
                for (ResultListener listener : Lookup.getDefault().lookupAll(ResultListener.class))
                {
                  listener.updateResults(encounter);
                }
                break;
              default:
                throw new TournamentException(MessageFormat.format("Unhandled result: {0}", result));
            }
          }
        }
      }
    }
  }

  protected int log(int x, int base)
  {
    return (int) (Math.log(x) / Math.log(base));
  }

  protected void addPairing(Map<Integer, Encounter> pairings,
          TeamInterface player1, TeamInterface player2)
  {
    pairings.put(encounterCount,
            new Encounter(encounterCount, getFormat(),
                    player1,
                    player2));
    if (player1 == BYE)
    {
      try
      {
        //Assign the win already, BYE always losses
        pairings.get(encounterCount)
                .updateResult(player2,
                        EncounterResult.WIN);
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    else if (player2 == BYE)
    {
      try
      {
        //Assign the win already, BYE always losses
        pairings.get(encounterCount)
                .updateResult(player1,
                        EncounterResult.WIN);
      }
      catch (TournamentException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    encounterCount++;
  }

  @Override
  public void showPairings()
  {
    Map<Integer, Encounter> pairings = getPairings();
    if (pairings != null)
    {
      pairings.entrySet().forEach((entry) ->
      {
        LOG.info(MessageFormat.format("{0} vs. {1}",
                entry.getValue().getEncounterSummary().keySet().toArray(
                        new TeamInterface[]
                        {
                        })[0].getTeamMembers().get(0).toString(),
                entry.getValue().getEncounterSummary().keySet().toArray(
                        new TeamInterface[]
                        {
                        })[1].getTeamMembers().get(0).toString()));
      });
    }
  }

  @Override
  public boolean isTeamActive(TeamInterface t)
  {
    //Always active by default.
    return true;
  }

  @Override
  public void setId(int id)
  {
    this.id = id;
  }

  @Override
  public int getId()
  {
    return id;
  }

  @Override
  public Map<Integer, Encounter> getRound(int round)
  {
    return pairingHistory.get(round);
  }

  @Override
  public void setRound(int round, Map<Integer, Encounter> encounters)
  {
    pairingHistory.put(round, encounters);
  }

  /**
   * @return the format
   */
  protected int getFormat()
  {
    return format;
  }

  /**
   * @param format the format to set
   */
  protected void setFormat(int format)
  {
    this.format = format;
  }

  @Override
  public void addTeams(List<TeamInterface> teams)
          throws TournamentSignupException
  {
    for (TeamInterface team : teams)
    {
      addTeam(team);
    }
  }

  @Override
  public List<TeamInterface> getTeams()
  {
    return Collections.unmodifiableList(teams);
  }

  @Override
  public List<TournamentListener> getListeners()
  {
    return Collections.unmodifiableList(listeners);
  }

  /**
   * @return the eliminations
   */
  @Override
  public int getEliminations()
  {
    return eliminations;
  }
}
