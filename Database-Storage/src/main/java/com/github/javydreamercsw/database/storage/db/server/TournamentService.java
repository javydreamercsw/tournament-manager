package com.github.javydreamercsw.database.storage.db.server;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Round;
import com.github.javydreamercsw.database.storage.db.RoundPK;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.database.storage.db.TournamentHasTeam;
import com.github.javydreamercsw.database.storage.db.TournamentPK;
import com.github.javydreamercsw.database.storage.db.controller.RoundJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TournamentFormatJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TournamentHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TournamentJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.api.Encounter;
import com.github.javydreamercsw.tournament.manager.api.EncounterResult;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentListener;
import com.github.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;

public class TournamentService extends Service<Tournament>
{
  private TournamentJpaController tc
          = new TournamentJpaController(DataBaseManager.getEntityManagerFactory());
  private TournamentHasTeamJpaController thtc
          = new TournamentHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private RoundJpaController rc
          = new RoundJpaController(DataBaseManager.getEntityManagerFactory());
  private TournamentFormatJpaController tfc
          = new TournamentFormatJpaController(DataBaseManager.getEntityManagerFactory());
  private Map<TournamentPK, TournamentInterface> tournamentMap = new HashMap<>();

  private TournamentService()
  {
  }

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final TournamentService INSTANCE = createTournamentService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static TournamentService createTournamentService()
    {
      TournamentService service = new TournamentService();

      return service;
    }
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static TournamentService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  public List<Tournament> findTournaments(String value)
  {
    List<Tournament> result = new ArrayList<>();
    tc.findTournamentEntities().forEach(tournament ->
    {
      if (tournament.getName().toLowerCase().contains(value.toLowerCase()))
      {
        result.add(tournament);
      }
    });
    return result;
  }

  public void saveTournament(Tournament t) throws NonexistentEntityException,
          IllegalOrphanException, Exception
  {
    if (t.getTournamentPK() != null && tc.findTournament(t.getTournamentPK()) != null)
    {
      tc.edit(t);
    }
    else
    {
      tc.create(t);
    }
  }

  public void deleteTournament(Tournament t) throws IllegalOrphanException,
          NonexistentEntityException
  {
    for (Round round : t.getRoundList())
    {
      deleteRound(round);
    }

    for (TournamentHasTeam tht : t.getTournamentHasTeamList())
    {
      deleteTeamFromTournament(tht);
    }
    tc.destroy(t.getTournamentPK());
  }

  public Tournament findTournament(TournamentPK id)
  {
    return tc.findTournament(id);
  }

  /**
   * Add teams to the tournament.
   *
   * @param t Tournament
   * @param teams Teams to add
   * @throws Exception if there's an error adding teams.
   */
  public void addTeams(Tournament t, List<Team> teams) throws Exception
  {
    addTeams(t, teams.toArray(new Team[0]));
  }

  /**
   * Add teams to the tournament.
   *
   * @param t Tournament
   * @param teams Teams to add
   * @throws Exception if there's an error adding teams.
   */
  public void addTeams(Tournament t, Team... teams) throws Exception
  {
    for (Team team : teams)
    {
      addTeam(t, team);
    }
  }

  /**
   * Add team to the tournament.
   *
   * @param t Tournament
   * @param team Team to add
   * @throws IllegalOrphanException if an orphan is left
   * @throws Exception if there's an error adding team.
   */
  public void addTeam(Tournament t, Team team) throws IllegalOrphanException,
          Exception
  {
    TournamentHasTeam tht = new TournamentHasTeam();
    tht.setTeam(team);
    tht.setTournament(t);
    thtc.create(tht);
    t.getTournamentHasTeamList().add(tht);
    saveTournament(t);
  }

  /**
   * Add round to tournament.
   *
   * @param t Tournament to add round to.
   * @throws Exception If something goes wrong adding it.
   */
  public void addRound(Tournament t) throws Exception
  {
    addRound(t, true);
  }

  /**
   * Add round to tournament.
   *
   * @param t Tournament to add round to.
   * @param save Save the tournament as part of this transaction.
   * @throws Exception If something goes wrong adding it.
   */
  public void addRound(Tournament t, boolean save) throws Exception
  {
    Round round = new Round();
    round.setTournament(t);
    round.setRoundNumber(t.getRoundList().size() + 1);
    rc.create(round);
    t.getRoundList().add(round);
    if (save)
    {
      saveTournament(t);
    }
  }

  /**
   * Delete a team from the tournament.
   *
   * @param tht Team to remove.
   * @throws NonexistentEntityException if it doesn't exist.
   */
  public void deleteTeamFromTournament(TournamentHasTeam tht)
          throws NonexistentEntityException
  {
    thtc.destroy(tht.getTournamentHasTeamPK());
  }

  @Override
  public List<Tournament> getAll()
  {
    return tc.findTournamentEntities();
  }

  /**
   * Delete round.
   *
   * @param round
   * @throws IllegalOrphanException
   * @throws NonexistentEntityException
   */
  public void deleteRound(Round round) throws IllegalOrphanException,
          NonexistentEntityException
  {
    for (MatchEntry me : round.getMatchEntryList())
    {
      MatchService.getInstance().deleteMatch(me);
    }
    rc.destroy(round.getRoundPK());
  }

  /**
   * Find a tournament format.
   *
   * @param name format name
   * @return Format or null if not found.
   */
  public TournamentFormat findFormat(String name)
  {
    for (TournamentFormat tf : tfc.findTournamentFormatEntities())
    {
      if (tf.getFormatName().equals(name))
      {
        return tf;
      }
    }
    return null;
  }

  /**
   * Add a tournament format
   *
   * @param tf Format to add
   * @throws Exception if something goes wrong creating it.
   */
  public void addFormat(TournamentFormat tf) throws Exception
  {
    tfc.create(tf);
  }

  public List<TournamentFormat> getFormats()
  {
    return tfc.findTournamentFormatEntities();
  }

  /**
   * Start the tournament.This will set up the first round.
   *
   * @param tournament Tournament to start.
   * @param listeners Tournament listeners
   * @throws TournamentException If something goes wrong.
   */
  public void startTournament(Tournament tournament,
          TournamentListener... listeners) throws TournamentException
  {
    try
    {
      // Check it has not been started already.
      if (hasStarted(tournament))
      {
        throw new TournamentException("Tournament has already been started!");
      }
      // Check it's not over already.
      if (isOver(tournament))
      {
        throw new TournamentException("Tournament is already over!");
      }

      // First get the Tournament Format
      Class<? extends TournamentInterface> format
              = (Class<? extends TournamentInterface>) Class.forName(tournament
                      .getTournamentFormat().getImplementationClass());

      //Convert the DB teams to the JSkill implementation
      List<TeamInterface> teams = new ArrayList<>();
      tournament.getTournamentHasTeamList().forEach(tht ->
      {
        List<TournamentPlayerInterface> players = new ArrayList<>();
        tht.getTeam().getPlayerList().forEach(player ->
        {
          players.add(new UIPlayer(player.getName(), player.getId()));
        });
        teams.add(new com.github.javydreamercsw.tournament.manager.Team(
                tht.getTeam().getId(),
                tht.getTeam().getName(), players));
      });
      TournamentInterface tf
              = Lookup.getDefault().lookup(format)
                      .createTournament(teams,
                              tournament.getWinPoints(),
                              tournament.getLossPoints(),
                              tournament.getDrawPoints());
      tournamentMap.put(tournament.getTournamentPK(), tf);

      // Add listeners
      for (TournamentListener listner : listeners)
      {
        addTournamentListener(tournament, listner);
      }

      // Create first round.
      startNextRound(tournament);
    }
    catch (ClassNotFoundException | TournamentException ex)
    {
      Exceptions.printStackTrace(ex);
      throw new TournamentException("Unable to create tournament!", ex);
    }
  }

  /**
   * Start next round.
   *
   * @param tournament tournament to start the next round of.
   * @throws TournamentException if tournament has not been started or is
   * already over.
   */
  public void startNextRound(Tournament tournament) throws TournamentException
  {
    if (isOver(tournament))
    {
      throw new TournamentException("Tournament is already over!");
    }

    try
    {
      if (tournament.getRoundList().isEmpty())
      {
        // Mark as started.
        tournament.setStartDate(LocalDateTime.now());
        saveTournament(tournament);
      }

      // Get the tournament implementation to build the next round.
      TournamentInterface ti = tournamentMap.get(tournament.getTournamentPK());

      ti.nextRound();
      Map<Integer, Encounter> pairings = ti.getPairings();
      if (pairings != null && !pairings.isEmpty())
      {
        try
        {
          // Create a round
          TournamentService.getInstance().addRound(tournament);

          Round currentRound
                  = tournament.getRoundList().get(tournament.getRoundList().size() - 1);
          // Persist the round in the database.
          for (Encounter encounter : pairings.values())
          {
            MatchEntry me = new MatchEntry();
            me.setRound(currentRound);
            me.setFormat(tournament.getFormat());
            MatchService.getInstance().saveMatch(me);
            for (TeamInterface team : encounter.getEncounterSummary().keySet())
            {
              MatchService.getInstance().addTeam(me,
                      TeamService.getInstance().findTeam(team.getTeamId()));
            }
            currentRound.getMatchEntryList().add(me);
          }
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
          throw new TournamentException("Error creating round!", ex);
        }
        ti.getListeners().forEach(listener -> listener
                .roundStart(tournament.getRoundList().size()));
      }
      else
      {
        // Tournament is over!
        // Mark as ended.
        tournament.setEndDate(LocalDateTime.now());
        saveTournament(tournament);
      }
    }
    catch (IllegalOrphanException ex)
    {
      Exceptions.printStackTrace(ex);
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  /**
   * Checks if tournament already has started.
   *
   * @param tournament Tournament to check
   * @return true if started, false otherwise.
   */
  public boolean hasStarted(Tournament tournament)
  {
    return findTournament(tournament.getTournamentPK()).getStartDate() != null;
  }

  /**
   * Checks if tournament already finished.
   *
   * @param tournament Tournament to check
   * @return true if is over, false otherwise.
   */
  public boolean isOver(Tournament tournament)
  {
    return findTournament(tournament.getTournamentPK()).getEndDate() != null;
  }

  /**
   * get a round by id.
   *
   * @param roundPK ID.
   * @return Round with the given ID or null if not found.
   */
  public Round getRound(RoundPK roundPK)
  {
    return rc.findRound(roundPK);
  }

  /**
   * Add a round time listener.
   *
   * @param tournament Tournament to add listener to.
   * @param listener Listener to add.
   * @throws TournamentException if tournament is not found.
   */
  public void addTournamentListener(Tournament tournament,
          TournamentListener listener) throws TournamentException
  {
    if (tournamentMap.containsKey(tournament.getTournamentPK()))
    {
      tournamentMap.get(tournament.getTournamentPK())
              .addTournamentListener(listener);
    }
    else
    {
      throw new TournamentException("Tournament not found!");
    }
  }

  /**
   * Remove a round time listener.
   *
   * @param tournament Tournament to remove listener from.
   * @param listener Listener to remove.
   * @throws TournamentException if tournament is not found.
   */
  public void removeRoundListener(Tournament tournament,
          TournamentListener listener) throws TournamentException
  {
    if (tournamentMap.containsKey(tournament.getTournamentPK()))
    {
      tournamentMap.get(tournament.getTournamentPK())
              .removeTournamentListener(listener);
    }
    else
    {
      throw new TournamentException("Tournament not found!");
    }
  }

  public void setResult(Tournament tournament, MatchHasTeam mht,
          MatchResultType rt) throws Exception
  {
    // Update the running tournament results.
    final TournamentInterface ti
            = tournamentMap.get(tournament.getTournamentPK());

    synchronized (ti)
    {
      // Update database
      MatchService.getInstance().setResult(mht, rt);
      for (Player player : mht.getTeam().getPlayerList())
      {
        // Update runtime
        for (Encounter e : ti.getRound(tournament.getRoundList().size()).values())
        {
          for (TeamInterface team : e.getEncounterSummary().keySet())
          {
            if (team.hasMember(player.getId()))
            {
              EncounterResult result;
              switch (rt.getType())
              {
                case "result.noshow":
                  result = EncounterResult.NO_SHOW;
                  break;
                case "result.forfeit":
                  result = EncounterResult.FORFEIT;
                  break;
                case "result.loss":
                  result = EncounterResult.LOSS;
                  break;
                case "result.draw":
                  result = EncounterResult.DRAW;
                  break;
                default:
                  result = EncounterResult.WIN;
              }
              e.updateResult(team, result);
            }
          }
        }
      }
      int roundNumber = mht.getMatchEntry().getRound().getRoundNumber();
      if (isRoundOver(tournament, roundNumber))
      {
        ti.processRound(roundNumber);
        ti.getListeners().forEach(listener -> listener.roundOver(roundNumber));
      }
    }
  }

  /**
   * Check if the round is over or not.
   *
   * @param t Tournament to check
   * @param round Round to check
   * @return true if over, false otherwise.
   */
  public boolean isRoundOver(Tournament t, int round)
  {
    return getRound(t, round).getMatchEntryList().stream().noneMatch((me)
            -> (!me.getMatchHasTeamList().stream().noneMatch((mht)
                    -> (mht.getMatchResult() == null))));
  }

  /**
   * Get round for a tournament.
   *
   * @param t Tournament to get round from.
   * @param round Round number to get.
   * @return The round or null if not found.
   */
  public Round getRound(Tournament t, int round)
  {
    List<Round> rounds = findTournament(t.getTournamentPK()).getRoundList();
    if (rounds.size() >= round)
    {
      return rounds.get(round - 1);
    }
    return null;
  }

  public void checkStatus(Tournament t)
  {

  }

  /**
   * Save a tournament Format.
   *
   * @param tf format to save
   * @throws NonexistentEntityException If editing and it doesn't exist.
   * @throws Exception If something goes wrong persisting to database.
   */
  public void saveTournamentFormat(TournamentFormat tf)
          throws NonexistentEntityException, Exception
  {
    if (tf.getId() == null)
    {
      tfc.create(tf);
    }
    else
    {
      tfc.edit(tf);
    }
  }

  /**
   * Get all Tournament Formats.
   *
   * @return All formats in the database.
   */
  public List<TournamentFormat> getAllFormats()
  {
    return tfc.findTournamentFormatEntities();
  }
}
