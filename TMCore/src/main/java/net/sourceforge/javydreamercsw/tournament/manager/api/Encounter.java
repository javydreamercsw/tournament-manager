package net.sourceforge.javydreamercsw.tournament.manager.api;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.javydreamercsw.tournament.manager.Team;

/**
 * Where to parties face off
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Encounter
{

  private final Map<TeamInterface, EncounterResult> results
          = new HashMap<>();
  private final int id;
  private static final Logger LOG
          = Logger.getLogger(Encounter.class.getName());
  private int format;

  /**
   * Create an encounter between multiple teams.
   *
   * @param id encounter id
   * @param format encounter format
   * @param team1 team 1
   * @param team2 team 2
   * @param t additional teams (optional)
   */
  public Encounter(int id, int format, TeamInterface team1, TeamInterface team2,
          TeamInterface... t)
  {
    results.put(team1, EncounterResult.UNDECIDED);
    results.put(team2, EncounterResult.UNDECIDED);
    for (TeamInterface team : t)
    {
      results.put(team, EncounterResult.UNDECIDED);
    }
    this.id = id;
    this.format = format;
  }

  /**
   * Create an encounter between two players.
   *
   * @param id encounter id
   * @param format encounter format
   * @param team1 team 1
   * @param team2 team 2
   */
  public Encounter(int id, int format, TournamentPlayerInterface team1,
          TournamentPlayerInterface team2)
  {
    results.put(new Team(team1), EncounterResult.UNDECIDED);
    results.put(new Team(team2), EncounterResult.UNDECIDED);
    this.id = id;
    this.format = format;
  }

  public void updateResult(TeamInterface team,
          EncounterResult result) throws TournamentException
  {
    TeamInterface target = null;
    for (Entry<TeamInterface, EncounterResult> entry : getEncounterSummary().entrySet())
    {
      if (entry.getKey().getTeamMembers().contains(team.getTeamMembers().get(0)))
      {
        target = entry.getKey();
      }
    }
    if (target != null)
    {
      LOG.log(Level.FINE, "Updating result for player: {0} to: {1}",
              new Object[]
              {
                team.getName(), result
              });
      for (TournamentPlayerInterface p : target.getTeamMembers())
      {
        switch (result)
        {
          case WIN:
            p.getRecord().win();
            break;
          case DRAW:
            p.getRecord().draw();
            break;
          case NO_SHOW:
          //Fall thru
          case LOSS:
            p.getRecord().loss();
            break;
          case UNDECIDED:
            throw new TournamentException(MessageFormat
                    .format("Unexpected result: {0}", result));
          default:
            throw new TournamentException(MessageFormat
                    .format("Invalid result: {0}", result));
        }
      }
    }
    else
    {
      throw new TournamentException(MessageFormat
              .format("TournamentPlayerInterface not part of this encounter: {0}",
                      team.getName()));
    }
  }

  /**
   * @return the teams
   */
  public Map<TeamInterface, EncounterResult> getEncounterSummary()
  {
    return results;
  }

  @Override
  public boolean equals(Object obj)
  {
    boolean result = false;
    if (obj instanceof Encounter)
    {
      Encounter encounter = (Encounter) obj;
      result = encounter.getId() == getId();
    }
    return result;
  }

  @Override
  public int hashCode()
  {
    int hash = 7;
    hash = 19 * hash + (this.results != null ? this.results.hashCode() : 0);
    hash = 19 * hash + this.id;
    return hash;
  }

  /**
   * @return the id
   */
  public int getId()
  {
    return id;
  }

  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    results.keySet().forEach((t) ->
    {
      if (!sb.toString().isEmpty())
      {
        sb.append(" vs. ");
      }
      sb.append(t.toString());
    });
    return MessageFormat.format("Encounter {0} ({1})", id, sb.toString());
  }

  public int getFormat()
  {
    return format;
  }
}
