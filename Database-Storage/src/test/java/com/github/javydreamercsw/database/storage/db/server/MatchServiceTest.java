package com.github.javydreamercsw.database.storage.db.server;

import static org.testng.Assert.*;

import com.github.javydreamercsw.database.storage.db.AbstractServerTest;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResult;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class MatchServiceTest extends AbstractServerTest {
  private Game game;
  private MatchEntry me;
  private Tournament t = new Tournament("Test 1");

  @BeforeClass
  @Override
  public void setup() throws NonexistentEntityException, IllegalOrphanException, Exception {
    super.setup();
    game =
        GameService.getInstance()
            .findGameByName(Lookup.getDefault().lookup(IGame.class).getName())
            .get();
    t.setTournamentFormat(
        TournamentService.getInstance()
            .findFormat(Lookup.getDefault().lookup(TournamentInterface.class).getName()));
    t.setFormat(FormatService.getInstance().getAll().get(0));
    TournamentService.getInstance().saveTournament(t);
    TournamentService.getInstance().addRound(t);

    GameService.getInstance().saveGame(game);

    me = new MatchEntry();
    me.setFormat(game.getFormatList().get(0));
    me.setRound(t.getRoundList().get(0));

    MatchService.getInstance().saveMatch(me);
  }

  @AfterMethod
  public void reset() {
    TeamService.getInstance()
        .getAll()
        .forEach(
            team -> {
              try {
                MatchService.getInstance().removeTeam(me, team);
              } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
                fail();
              }
            });

    assertTrue(
        MatchService.getInstance().findMatch(me.getMatchEntryPK()).getMatchHasTeamList().isEmpty());
  }

  /**
   * Test of write2DB method, of class MatchServer.
   *
   * @throws com.github.javydreamercsw.tournament.manager.api.TournamentException
   */
  @Test
  public void testMatchService() throws TournamentException, Exception {
    MatchEntry match = MatchService.getInstance().findMatch(me.getMatchEntryPK());
    assertNotNull(me.getFormat());
    assertNotNull(match.getFormat());

    // Add teams
    Player player = new Player("Player 1");
    PlayerService.getInstance().savePlayer(player);

    Player player2 = new Player("Player 2");
    PlayerService.getInstance().savePlayer(player2);

    TeamService.getInstance()
        .getAll()
        .forEach(
            team -> {
              try {
                MatchService.getInstance().addTeam(me, team);
              } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                fail();
              }
            });

    match = MatchService.getInstance().findMatch(me.getMatchEntryPK());
    assertEquals(match.getMatchHasTeamList().size(), 2);
    assertNotNull(match.getFormat());

    Random random = new Random();
    List<MatchResultType> resultTypes = MatchService.getInstance().getResultTypes();
    for (MatchHasTeam mht : match.getMatchHasTeamList()) {
      MatchService.getInstance()
          .setResult(mht, resultTypes.get(random.nextInt(resultTypes.size())));
    }

    List<Object> results = DataBaseManager.namedQuery("MatchEntry.findAll");
    assertTrue(results.size() > 0);

    results.forEach(
        result -> {
          MatchEntry m = (MatchEntry) result;
          assertEquals(m.getMatchHasTeamList().size(), 2);
          assertNotNull(m.getFormat());
        });

    assertTrue(match.getMatchHasTeamList().size() > 0);
    // Check that the record is updated accordingly.
    for (MatchHasTeam mht : match.getMatchHasTeamList()) {
      MatchResult result = mht.getMatchResult();
      MatchService.getInstance().lockMatchResult(result);

      assertTrue(mht.getTeam().getPlayerList().size() > 0);
      mht.getTeam()
          .getPlayerList()
          .forEach(
              p -> {
                switch (result.getMatchResultType().getType()) {
                  // Various reasons leading to a loss.
                  case "result.loss":
                  // Fall thru
                  case "result.forfeit":
                  // Fall thru
                  case "result.no_show":
                    assertEquals(p.getRecordList().get(0).getWins(), 0);
                    assertEquals(p.getRecordList().get(0).getLoses(), 1);
                    assertEquals(p.getRecordList().get(0).getDraws(), 0);
                    break;
                  case "result.draw":
                    assertEquals(p.getRecordList().get(0).getWins(), 0);
                    assertEquals(p.getRecordList().get(0).getLoses(), 0);
                    assertEquals(p.getRecordList().get(0).getDraws(), 1);
                    break;
                  case "result.win":
                    assertEquals(p.getRecordList().get(0).getWins(), 1);
                    assertEquals(p.getRecordList().get(0).getLoses(), 0);
                    assertEquals(p.getRecordList().get(0).getDraws(), 0);
                    break;
                }
              });
    }

    // All players must hava a non-zero record.
    assertFalse(PlayerService.getInstance().getAll().isEmpty());
    PlayerService.getInstance()
        .getAll()
        .forEach(
            p -> {
              assertTrue(p.getRecordList().size() == 1);
              assertTrue(
                  p.getRecordList().get(0).getDraws()
                          + p.getRecordList().get(0).getLoses()
                          + p.getRecordList().get(0).getWins()
                      > 0);
            });

    MatchService.getInstance().saveMatch(me);

    MatchService.getInstance().lockMatchResult(me);

    MatchService.getInstance().updateRankings(me);

    // Check the stats
    me.getMatchHasTeamList()
        .forEach(
            mht -> {
              Team dbTeam = TeamService.getInstance().findTeam(mht.getTeam().getId());
              TeamHasFormatRecord thfr =
                  TeamService.getInstance().getFormatRecord(dbTeam, me.getFormat());
              assertNotNull(thfr);
              assertTrue(thfr.getMean() + thfr.getStandardDeviation() != 0);
            });
  }

  @Test
  public void testFindMatchesWithFormat() {
    assertEquals(
        MatchService.getInstance().findMatchesWithFormat("").size(), game.getFormatList().size());

    assertEquals(
        MatchService.getInstance()
            .findMatchesWithFormat(game.getFormatList().get(0).getName())
            .size(),
        1);

    assertTrue(
        MatchService.getInstance()
            .findMatchesWithFormat(game.getFormatList().get(0).getName() + "x")
            .isEmpty());
  }

  @DataProvider
  public Object[][] getQualityToTest() {
    Object[][] data = new Object[10][];
    for (int i = 10; i > 0; i--) {
      data[10 - i] = new Object[] {i * 10.0};
    }
    return data;
  }

  @Test(dataProvider = "getQualityToTest")
  public void testcalculateBasePoints(double quality) throws Exception {
    // Add teams
    Optional<Player> p1 = PlayerService.getInstance().findPlayerByName("Player 1");
    Optional<Player> p2 = PlayerService.getInstance().findPlayerByName("Player 2");
    Player player = p1.isPresent() ? p1.get() : new Player("Player 1");
    PlayerService.getInstance().savePlayer(player);

    Player player2 = p2.isPresent() ? p2.get() : new Player("Player 2");
    PlayerService.getInstance().savePlayer(player2);

    TeamService.getInstance()
        .getAll()
        .forEach(
            team -> {
              try {
                MatchService.getInstance().addTeam(me, team);
              } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                fail();
              }
            });

    double calculatedQuality;
    do {
      int count = 0;
      for (MatchHasTeam mht : me.getMatchHasTeamList()) {
        MatchService.getInstance()
            .setResult(
                mht,
                count == 0
                    ? MatchService.getInstance().getResultType("result.win").get()
                    : MatchService.getInstance().getResultType("result.loss").get());
        count++;
      }
      TeamHasFormatRecord thfr1 =
          TeamService.getInstance()
              .getFormatRecord(
                  MatchService.getInstance()
                      .findMatch(me.getMatchEntryPK())
                      .getMatchHasTeamList()
                      .get(0)
                      .getTeam(),
                  game.getFormatList().get(0));

      double player1Points = thfr1 == null ? 0.0 : thfr1.getPoints();

      MatchService.getInstance().lockMatchResult(me);
      MatchService.getInstance().updateRankings(me);

      // Check results
      MatchService.getInstance()
          .findMatch(me.getMatchEntryPK())
          .getMatchHasTeamList()
          .forEach(
              (mht) -> {
                if (mht.getMatchResult().getMatchResultType().getType().equals("result.win")) {
                  assertEquals(
                      TeamService.getInstance()
                              .getFormatRecord(mht.getTeam(), game.getFormatList().get(0))
                              .getPoints()
                          - player1Points,
                      10.0 * MatchService.getInstance().calculateModifier(quality));
                } else {
                  assertEquals(
                      TeamService.getInstance()
                          .getFormatRecord(mht.getTeam(), game.getFormatList().get(0))
                          .getPoints(),
                      0);
                }
              });

      calculatedQuality = MatchService.getInstance().getMatchQuality(me);
    } while (calculatedQuality > quality);

    TeamService.getInstance()
        .getAll()
        .forEach(
            team -> {
              try {
                MatchService.getInstance().removeTeam(me, team);
              } catch (NonexistentEntityException ex) {
                Exceptions.printStackTrace(ex);
                fail();
              }
            });
  }
}
