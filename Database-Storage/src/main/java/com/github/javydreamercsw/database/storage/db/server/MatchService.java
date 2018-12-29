package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchEntryPK;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchHasTeamPK;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecordPK;
import com.github.javydreamercsw.database.storage.db.controller.MatchEntryJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchHasTeamJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultJpaController;
import com.github.javydreamercsw.database.storage.db.controller.MatchResultTypeJpaController;
import com.github.javydreamercsw.database.storage.db.controller.TeamHasFormatRecordJpaController;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.UIPlayer;
import com.github.javydreamercsw.tournament.manager.api.TeamInterface;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.standing.RankingProvider;
import com.github.javydreamercsw.trueskill.TrueSkillRankingProvider;

import de.gesundkrank.jskills.IPlayer;
import de.gesundkrank.jskills.Rating;

public class MatchService extends Service<MatchEntry>
{
  private MatchEntryJpaController mc
          = new MatchEntryJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchHasTeamJpaController mhtc
          = new MatchHasTeamJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultJpaController mrc
          = new MatchResultJpaController(DataBaseManager.getEntityManagerFactory());
  private MatchResultTypeJpaController mrtc
          = new MatchResultTypeJpaController(DataBaseManager.getEntityManagerFactory());
  private final RankingProvider rp
          = Lookup.getDefault().lookup(RankingProvider.class);
  private final TeamHasFormatRecordJpaController thfrc
          = new TeamHasFormatRecordJpaController(DataBaseManager.getEntityManagerFactory());

  /**
   * Helper class to initialize the singleton Service in a thread-safe way and
   * to keep the initialization ordering clear between the two services. See
   * also: https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom
   */
  private static class SingletonHolder
  {
    static final MatchService INSTANCE = createMatchService();

    /**
     * This class is not meant to be instantiated.
     */
    private SingletonHolder()
    {
    }

    private static MatchService createMatchService()
    {
      final MatchService reviewService = new MatchService();
      return reviewService;
    }
  }

  /**
   * Declared private to ensure uniqueness of this Singleton.
   */
  private MatchService()
  {
  }

  /**
   * Gets the unique instance of this Singleton.
   *
   * @return the unique instance of this Singleton
   */
  public static MatchService getInstance()
  {
    return SingletonHolder.INSTANCE;
  }

  /**
   * Save a match to the database.
   *
   * @param match Match to save.
   * @throws Exception if something goes wrong with the database.
   */
  public void saveMatch(MatchEntry match) throws Exception
  {
    if (match.getMatchEntryPK() != null
            && mc.findMatchEntry(match.getMatchEntryPK()) != null)
    {
      mc.edit(match);
    }
    else
    {
      mc.create(match);
    }
  }

  /**
   * Delete a match from the database.
   *
   * @param match Match to delete.
   * @throws NonexistentEntityException if match doesn't exist
   * @throws IllegalOrphanException if an entity was left orphan.
   */
  public void deleteMatch(MatchEntry match) throws NonexistentEntityException,
          IllegalOrphanException
  {
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      mhtc.destroy(mht.getMatchHasTeamPK());
    }
    mc.destroy(match.getMatchEntryPK());
  }

  /**
   * Find matches of the specified format.
   *
   * @param format Format name to look for.
   * @return List of matches with the specified format.
   */
  public List<MatchEntry> findMatchesWithFormat(String format)
  {
    List<MatchEntry> results = new ArrayList<>();
    mc.findMatchEntryEntities().forEach(match ->
    {
      if (format.trim().isEmpty()
              || match.getFormat().getName().equals(format))
      {
        results.add(match);
      }
    });
    return results;
  }

  /**
   * Find a match by db key.
   *
   * @param key Key for the match.
   * @return Match or null if not found.
   */
  public MatchEntry findMatch(MatchEntryPK key)
  {
    return mc.findMatchEntry(key);
  }

  /**
   * Add a team to a match.
   *
   * @param match Match to be added to.
   * @param team Team to add
   * @return true if added. False if team already in the match or was unable to
   * be added.
   * @throws Exception persisting to data base.
   */
  public boolean addTeam(MatchEntry match, Team team) throws Exception
  {
    // Check the team is not in this match already
    if (!match.getMatchHasTeamList().stream().noneMatch((mht)
            -> (Objects.equals(mht.getTeam().getId(), team.getId()))))
    {
      return false;
    }
    MatchHasTeam mht = new MatchHasTeam();
    mht.setTeam(team);
    mht.setMatchEntry(match);
    if (match.getMatchEntryPK() != null)
    {
      mht.setMatchHasTeamPK(new MatchHasTeamPK(team.getId(),
              match.getMatchEntryPK().getId(),
              match.getMatchEntryPK().getFormatId(),
              match.getFormat().getGame().getId()));
    }

    if (match.getMatchEntryPK() != null)
    {
      mhtc.create(mht);
    }

    match.getMatchHasTeamList().add(mht);

    // Make sure each team member has a record for this game. Add one otherwise.
    team.getPlayerList().forEach(player ->
    {
      boolean found = false;
      for (Record r : player.getRecordList())
      {
        if (Objects.equals(r.getGame().getId(),
                match.getFormat().getGame().getId()))
        {
          found = true;
          break;
        }
      }
      if (!found)
      {
        try
        {
          Record record = new Record();
          record.getPlayerList().add(player);
          record.setGame(match.getFormat().getGame());
          RecordService.getInstance().saveRecord(record);
          player.getRecordList().add(record);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }
    });
    return true;
  }

  /**
   * Remove team from match.
   *
   * @param match Match to remove from.
   * @param team Team to remove.
   * @throws NonexistentEntityException if team doesn't exist in match.
   */
  public void removeTeam(MatchEntry match, Team team)
          throws NonexistentEntityException
  {
    MatchHasTeam mht = null;
    for (MatchHasTeam temp : match.getMatchHasTeamList())
    {
      if (temp.getTeam().getId().equals(team.getId()))
      {
        mht = temp;
        break;
      }
    }
    if (mht != null)
    {
      match.getMatchHasTeamList().remove(mht);
      if (match.getMatchEntryPK() != null)
      {
        mhtc.destroy(mht.getMatchHasTeamPK());
      }
    }
  }

  @Override
  public List<MatchEntry> getAll()
  {
    return mc.findMatchEntryEntities();
  }

  /**
   * Get result type by name.
   *
   * @param type type to search for (as found in the data base).
   * @return Optional type.
   */
  public Optional<MatchResultType> getResultType(String type)
  {
    for (MatchResultType mrt : getResultTypes())
    {
      if (mrt.getType().equals(type))
      {
        return Optional.of(mrt);
      }
    }
    return Optional.empty();
  }

  /**
   * Get all Result Types.
   *
   * @return list of result types.
   */
  public List<MatchResultType> getResultTypes()
  {
    return mrtc.findMatchResultTypeEntities();
  }

  /**
   * Set result for a match.
   *
   * @param mht Team to assign result to.
   * @param mrt Result type.
   * @throws NonexistentEntityException If something doesn't exist.
   * @throws Exception persisting to data base
   */
  public void setResult(MatchHasTeam mht, MatchResultType mrt)
          throws NonexistentEntityException, Exception
  {
    if (mht.getMatchResult() != null)
    {
      //Delete the old one.
      mrc.destroy(mht.getMatchResult().getMatchResultPK());
    }
    MatchResult mr = new MatchResult();
    mr.setMatchResultType(mrt);
    mr.getMatchHasTeamList().add(mht);
    mrc.create(mr);
    mht.setMatchResult(mr);
  }

  /**
   * Lock the match result.This is meant not to be undone as it calculates
   * experience and update records which is dependent on when it happens.
   *
   * @param me Match Result to lock.
   * @throws TournamentException
   */
  public void lockMatchResult(MatchEntry me) throws TournamentException
  {
    for (MatchHasTeam mht : findMatch(me.getMatchEntryPK()).getMatchHasTeamList())
    {
      if (mht != null)
      {
        try
        {
          lockMatchResult(mht.getMatchResult());
        }
        catch (Exception ex)
        {
          throw new TournamentException(ex);
        }
      }
      else
      {
        throw new TournamentException("Missing result!");
      }
    }
  }

  /**
   * Lock the match result.This is meant not to be undone as it calculates
   * experience and update records which is dependent on when it happens.
   *
   * @param mr Match Result to lock.
   * @throws java.lang.Exception
   */
  public void lockMatchResult(MatchResult mr) throws Exception
  {
    mr.setLocked(true);

    // Update the record
    mr.getMatchHasTeamList().forEach(mht ->
    {
      mht.getTeam().getPlayerList().forEach(player ->
      {
        Record record = player.getRecordList().get(0);
        switch (mr.getMatchResultType().getType())
        {
          case "result.loss":
          //Fall thru
          case "result.forfeit":
          //Fall thru
          case "result.no_show":
            record.setLoses(record.getLoses() + 1);
            break;
          case "result.draw":
            record.setDraws(record.getDraws() + 1);
            break;
          //Various reasons leading to a win.
          case "result.win":
            record.setWins(record.getWins() + 1);
            break;
        }
        try
        {
          RecordService.getInstance().saveRecord(record);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      });
    });
    mrc.edit(mr);
  }

  /**
   * Update a match result.
   *
   * @param mr Match result to update.
   * @throws Exception If result doesn't exist.
   */
  public void updateResult(MatchResult mr) throws Exception
  {
    if (mr.getMatchResultPK() != null)
    {
      mrc.edit(mr);
    }
    else
    {
      throw new Exception("Trying to update non existing result!");
    }
  }

  /**
   * Set match results ranked or unranked.
   *
   * @param match Match to update.
   * @param ranked true for ranked, false for unranked.
   * @throws TournamentException if results are already locked.
   * @throws Exception If there's an error updating the result.
   */
  public void setRanked(MatchEntry match, boolean ranked)
          throws TournamentException, Exception
  {
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      if (!mht.getMatchResult().getLocked())
      {
        mht.getMatchResult().setRanked(ranked);
        MatchService.getInstance().updateResult(mht.getMatchResult());
      }
      else
      {
        throw new TournamentException("Rsults already locked!");
      }
    }
  }

  /**
   * Update rankings for the match. This assumes that all losers were eliminated
   * at the same time.
   *
   * @param me Match entry to check.
   * @throws TournamentException
   */
  public void updateRankings(final MatchEntry me) throws TournamentException
  {
    updateRankings(me, new HashMap<>());
  }

  /**
   * Update rankings for the match. This allows to give extra credits for losing
   * teams that lasted longer in the match. Use 1 for the key of the winner, 2
   * for the runner up and so on. Value implemented as a list to handle ties on
   * each place.
   *
   * @param me Match entry to check.
   * @param order Map indicating the place as key and a list of team id's as
   * value.
   * @throws TournamentException
   */
  public void updateRankings(final MatchEntry me,
          Map<Integer, List<Integer>> order) throws TournamentException
  {
    TrueSkillRankingProvider p = (TrueSkillRankingProvider) rp;
    // First make sure that everyone has a result
    MatchEntry match = findMatch(me.getMatchEntryPK());
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      if (mht.getMatchResult() == null || !mht.getMatchResult().getLocked())
      {
        throw new TournamentException("Not all teams have a locked result!");
      }
    }

    TeamInterface[] teams = new TeamInterface[match.getMatchHasTeamList().size()];
    int[] resultOrder = new int[match.getMatchHasTeamList().size()];
    //Ok, now check the order if any
    if (order.isEmpty())
    {
      order.put(1, new ArrayList<>());
      order.put(2, new ArrayList<>());
      // Create an order with the winner as first place and everyone else tied for second.
      for (MatchHasTeam mht : match.getMatchHasTeamList())
      {
        switch (mht.getMatchResult().getMatchResultType().getType())
        {
          case "result.win":
            order.get(1).add(mht.getTeam().getId());
            break;
          default:
            order.get(2).add(mht.getTeam().getId());
        }
      }
    }

    //Convert into the JSkill interface for calculations
    for (MatchHasTeam mht : match.getMatchHasTeamList())
    {
      try
      {
        p.addTeam(TeamService.getInstance().convertToTeam(mht.getTeam(),
                me.getFormat()));
      }
      catch (Exception ex)
      {
        throw new TournamentException(ex);
      }
    }

    int count = 0;
    for (Entry<Integer, List<Integer>> entry : order.entrySet())
    {
      for (Integer t : entry.getValue())
      {
        teams[count] = TeamService.getInstance()
                .convertToTeam(TeamService.getInstance().findTeam(t),
                        me.getFormat());
        resultOrder[count++] = entry.getKey();
      }
    }

    /**
     * Quality of the match is used to determine how even the match up is. It'll
     * be a number between 0% and 100%.
     *
     * The closest to zero the higher reward for the lower ranked for a win and
     * the lower reward for the higher ranked for the win. This number takes
     * into account all the skills from all the teams in the match.
     */
    double quality = getMatchQuality(teams);

    // Make the calculations
    Map<IPlayer, Rating> ratings
            = p.getCalculator().calculateNewRatings(p.getGameInfo(),
                    de.gesundkrank.jskills.Team.concat(teams),
                    resultOrder);

    // Now persist to database
    for (Entry<IPlayer, Rating> entry : ratings.entrySet())
    {
      Optional<Player> temp
              = PlayerService.getInstance()
                      .findPlayerById(((UIPlayer) entry.getKey()).getID());
      if (temp.isPresent())
      {
        Player player = temp.get();

        // This assumes that the first tema is the team only contaiining the player.
        Team team = player.getTeamList().get(0);

        int totalPoints = (int) getMatchPointsEarned(team, me, quality);

        // Change modifier based on who beated who
        try
        {
          if (TeamService.getInstance().hasFormatRecord(team, me.getFormat()))
          {
            //Update it
            TeamHasFormatRecord thfr
                    = TeamService.getInstance().getFormatRecord(team, me.getFormat());
            thfr.setMean(entry.getValue().getMean());
            thfr.setStandardDeviation(entry.getValue().getStandardDeviation());
            thfr.setPoints(thfr.getPoints() + totalPoints);
            if (thfr.getPoints() < 0)
            {
              // Don't go below zero.
              thfr.setPoints(0);
            }
            thfrc.edit(thfr);
          }
          else
          {
            TeamHasFormatRecord thfr
                    = new TeamHasFormatRecord(new TeamHasFormatRecordPK(
                            team.getId(),
                            me.getFormat().getFormatPK().getId(),
                            me.getFormat().getFormatPK().getGameId()),
                            entry.getValue().getMean(),
                            entry.getValue().getStandardDeviation());
            thfr.setFormat(me.getFormat());
            thfr.setTeam(team);
            //If it'll go below zero ignore it.
            thfr.setPoints(totalPoints > 0 ? totalPoints : 0);
            thfrc.create(thfr);
            team.getTeamHasFormatRecordList().add(thfr);
          }
        }
        catch (Exception ex)
        {
          throw new TournamentException(ex);
        }
        TeamService.getInstance().saveTeam(team);
      }
    }
  }

  /**
   * Calculate the point modifier based on the match quality.
   *
   * @param quality Match quality
   * @return modifier.
   */
  protected double calculateModifier(double quality)
  {
    double modifier;
    if (quality >= 90)
    {
      modifier = 1.0;
    }
    else if (quality >= 80)
    {
      modifier = 1.1;
    }
    else if (quality >= 70)
    {
      modifier = 1.2;
    }
    else if (quality >= 60)
    {
      modifier = 1.3;
    }
    else if (quality >= 50)
    {
      modifier = 1.4;
    }
    else if (quality >= 40)
    {
      modifier = 1.5;
    }
    else if (quality >= 30)
    {
      modifier = 1.6;
    }
    else if (quality >= 20)
    {
      modifier = 1.7;
    }
    else if (quality >= 10)
    {
      modifier = 1.8;
    }
    else
    {
      modifier = 2.0;
    }
    return modifier;
  }

  /**
   * Calculate match points to add/remove based on the result of the match.
   *
   * @param team Team to calculate points for.
   * @param me Match to calculate points for.
   * @param quality Match quality
   * @return points earned/lost.
   */
  protected double getMatchPointsEarned(Team team, MatchEntry me, double quality)
  {
    double base = 10.0;

    // Find result to adjust base accordingly
    for (MatchHasTeam mht : findMatch(me.getMatchEntryPK()).getMatchHasTeamList())
    {
      if (Objects.equals(mht.getTeam().getId(), team.getId()))
      {
        switch (mht.getMatchResult().getMatchResultType().getType())
        {
          case "result.loss":
          // Fall thru
          case "result.forfeit":
          // Fall thru
          case "result.no_show":
            // Points are substracted in a loss.
            base *= -1;
            break;
          case "result.draw":
            base = 0.0;
            break;
          default:
            // No modification
            break;
        }
        break;
      }
    }

    return base * calculateModifier(quality);
  }

  /**
   * Get math quality.
   *
   * @param me Match entry.
   * @return Quality of the match, in percentage.
   * @throws TournamentException if there's an error converting teams.
   */
  public double getMatchQuality(MatchEntry me) throws TournamentException
  {
    TeamInterface[] teams = new TeamInterface[me.getMatchHasTeamList().size()];
    //Convert into the JSkill interface for calculations

    int count = 0;
    for (MatchHasTeam mht : me.getMatchHasTeamList())
    {
      try
      {
        teams[count] = TeamService.getInstance().convertToTeam(mht.getTeam(),
                me.getFormat());
        count++;
      }
      catch (Exception ex)
      {
        throw new TournamentException(ex);
      }
    }

    return getMatchQuality(teams);
  }

  /**
   * Get math quality.
   *
   * @param teams Teams to get quality from.
   * @return Quality of the match, in percentage.
   */
  public double getMatchQuality(TeamInterface[] teams)
  {
    return ((TrueSkillRankingProvider) rp).getCalculator().calculateMatchQuality(
            ((TrueSkillRankingProvider) rp).getGameInfo(),
            de.gesundkrank.jskills.Team.concat(teams)) * 100;
  }
}
