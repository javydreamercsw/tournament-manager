/*
 * Default Database Storage.
 */
package net.sourceforge.javydreamercsw.database.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import de.gesundkrank.jskills.IPlayer;
import net.sourceforge.javydreamercsw.database.storage.db.Record;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;
import net.sourceforge.javydreamercsw.database.storage.db.server.DataBaseManager;
import net.sourceforge.javydreamercsw.database.storage.db.server.RoundServer;
import net.sourceforge.javydreamercsw.database.storage.db.server.TournamentServer;
import net.sourceforge.javydreamercsw.tournament.manager.api.TeamInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageException;
import net.sourceforge.javydreamercsw.tournament.manager.api.storage.StorageInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
@ServiceProvider(service = StorageInterface.class)
public class DatabaseStorage implements StorageInterface
{

  private boolean initialized = false;

  @Override
  public void saveTournament(TournamentInterface ti) throws StorageException
  {
    initialize();
    Map<String, Object> properties = new HashMap<>();
    //Already exists
    properties.put("id", ti.getId());
    List<Object> result = DataBaseManager.namedQuery("Tournament.findById", properties);
    TournamentServer ts;
    if (result.isEmpty())
    {
      ts = new TournamentServer(ti.getName());
      ts.write2DB();
      ti.setId(ts.getId());
    }
    else
    {
      ts = new TournamentServer((Tournament) result.get(0));
      ts.setName(ti.getName());
    }
    ts.setWinPoints(ti.getWinPoints());
    ts.setDrawPoints(ti.getDrawPoints());
    ts.setLossPoints(ti.getLossPoints());
    //Process rounds
    int currentRound = ti.getRound();
    for (int r = 1; r <= currentRound; r++)
    {
      if (ts.getRoundList().size() >= r)
      {
        //Round exists, update
        RoundServer theRound
                = new RoundServer(ts.getRoundList().get(r - 1));
//        for (Map.Entry<Integer, Encounter> entry : ti.getPairings().entrySet())
//        {
//          MatchServer ms;
//          Encounter encounter = entry.getValue();
//          try
//          {
//            ms = new MatchServer(theRound.getRoundPK().getId(),
//                    entry.getKey(), encounter.getFormat());
//            //TODO: Update just in case
//          }
//          catch (TournamentException ex)
//          {
//            //Is a new encounter
//            ms = new MatchServer(theRound.getEntity()., encounter.getFormat());
//            List<Team> teams = new ArrayList<>();
//            List<Player> players = new ArrayList<>();
//            Team found = null;
//            for (Map.Entry<TeamInterface, EncounterResult> e2
//                    : encounter.getEncounterSummary().entrySet())
//            {
//              for (TournamentPlayerInterface p : e2.getKey().getTeamMembers())
//              {
//                //Is there a team for them already?
//                PlayerServer player = new PlayerServer(p);
//                teams.addAll(player.getTeamList());
//                players.add(player.getEntity());
//              }
//              //Now teams have all the teams for the players, look to see if one contains them all
//              for (Team t : teams)
//              {
//                if (TeamServer.hasMembers(t, e2.getKey().getTeamMembers()))
//                {
//                  found = t;
//                  break;
//                }
//              }
//              if (found == null)
//              {
//                //Need to create one
//                TeamServer temp = new TeamServer("", players);
//                temp.write2DB();
//                found = temp.getEntity();
//              }
//              ms.getMatchHasTeamList().add(new MatchHasTeam());
//            }
//            ms.write2DB();
//          }
//        }
      }
      else
      {
        //New round, add
      }
    }
    ts.setRoundList(null);
    //Process teams
    ts.setTournamentHasTeamList(null);
  }

  @Override
  public void loadTournament(int id)
          throws StorageException
  {
    initialize();
    Map<String, Object> properties = new HashMap<>();
    //Already exists
    properties.put("id", id);
    List<Object> result = DataBaseManager.namedQuery("Tournament.findById", properties);
    TournamentServer ts;
    if (result.isEmpty())
    {
      throw new StorageException("Unable to find tournament with id: " + id);
    }
    else
    {
      ts = new TournamentServer((Tournament) result.get(0));
    }
    List<TeamInterface> teams = new ArrayList<>();
    ts.getTournamentHasTeamList().forEach((tht) ->
    {
      List<TournamentPlayerInterface> players = new ArrayList<>();
      tht.getTeam().getPlayerList().forEach((p) ->
      {
        int wins = 0;
        int loss = 0;
        int draws = 0;
        for (Record r : p.getRecordList())
        {
          wins += r.getWins();
          loss += r.getLoses();
          draws += r.getDraws();
        }
        players.add(Lookup.getDefault().lookup(TournamentPlayerInterface.class)
                .createInstance(p.getName(), wins, loss, draws));
      });
      teams.add(Lookup.getDefault().lookup(TeamInterface.class)
              .createTeam(tht.getTeam().getName(), players));
    });
    TournamentInterface tournament
            = Lookup.getDefault().lookup(TournamentInterface.class)
                    .createTournament(teams, ts.getWinPoints(),
                    ts.getLossPoints(),
                    ts.getDrawPoints());
    tournament.setId(id);
  }

  @Override
  public void initialize()
  {
    if (!isInitialized())
    {
      initialized = DataBaseManager.getEntityManagerFactory().isOpen();
    }
  }

  @Override
  public boolean isInitialized()
  {
    return initialized;
  }

  @Override
  public int addPlayer(IPlayer player)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public IPlayer getPlayer(int id)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
