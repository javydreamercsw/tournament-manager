package com.github.javydreamercsw.database.storage.db.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.tournament.manager.api.GameFormat;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.api.TournamentInterface;
import com.github.javydreamercsw.tournament.manager.api.storage.StorageException;
import com.googlecode.flyway.core.Flyway;
import com.googlecode.flyway.core.api.MigrationInfo;
import com.googlecode.flyway.core.api.MigrationState;
import com.googlecode.flyway.core.exception.FlywayException;

@Messages(
        {
          "result.win=Win",
          "result.loss=Loss",
          "result.draw=Draw",
          "result.forfeit=Forfeit",
          "result.no_show=No Show"
        })
public class DataBaseManager
{

  private static String PU = "TMPU";
  private static EntityManagerFactory emf = null;
  private static EntityManager em;
  private static Map<String, Object> properties;
  private static final Logger LOG
          = Logger.getLogger(DataBaseManager.class.getSimpleName());
  private static boolean dbError = false;
  private static DBState state = DBState.START_UP;
  public static final String JNDI_DATASOURCE_NAME
          = "java:comp/env/jdbc/TMDB";

  public static EntityManagerFactory getEntityManagerFactory()
  {
    if (emf == null)
    {
      emf = Persistence.createEntityManagerFactory(PU);
    }
    return emf;
  }

  /**
   * Get the current persistence unit name
   *
   * @return current persistence unit name
   */
  public static String getPersistenceUnitName()
  {
    return PU;
  }

  /**
   * @param aPU the PU to set
   */
  public static void setPersistenceUnitName(String aPU)
  {
    PU = aPU;
    LOG.log(Level.FINE, "Changed persistence unit name to: {0}", PU);
    //Set it to null so it's recreated with new Persistence Unit next time is requested.
    emf = null;
    em = null;
    dbError = false;
    try
    {
      reload();
    }
    catch (StorageException ex)
    {
      LOG.log(Level.SEVERE, null, ex);
      dbError = true;
    }
  }

  public static void reload() throws StorageException
  {
    reload(false);
  }

  public static void reload(boolean close) throws StorageException
  {
    if (close)
    {
      close();
    }
    getEntityManager();
    updateDBState();
  }

  public static void updateDBState()
  {
    DataSource ds = null;
    Connection conn = null;
    PreparedStatement stmt = null;
    ResultSet rs = null;
    try
    {
      ds = (javax.sql.DataSource) new InitialContext().lookup(JNDI_DATASOURCE_NAME);
      conn = ds.getConnection();
    }
    catch (NamingException ne)
    {
      LOG.log(Level.FINE, null, ne);
      try
      {
        //It might be the tests.
        ds = new JdbcDataSource();
        ((JdbcDataSource) ds).setPassword((String) getProperties().get("javax.persistence.jdbc.password"));
        ((JdbcDataSource) ds).setUser((String) getProperties().get("javax.persistence.jdbc.user"));
        ((JdbcDataSource) ds).setURL(
                (String) getProperties().get("javax.persistence.jdbc.url"));
        //Load the driver
        Class.forName((String) getProperties().get("javax.persistence.jdbc.driver"));
      }
      catch (ClassNotFoundException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
        dbError = true;
      }
      EntityTransaction transaction = getEntityManager().getTransaction();
      transaction.begin();
      conn = getEntityManager().unwrap(java.sql.Connection.class);
      transaction.commit();
    }
    catch (SQLException ex)
    {
      LOG.log(Level.SEVERE, null, ex);
      dbError = true;
    }
    if (conn != null)
    {
      try
      {
        stmt = conn.prepareStatement("select * from match_result_type");
        rs = stmt.executeQuery();
        if (!rs.next())
        {
          //Tables there but empty? Not safe to proceed
          setState(DBState.NEED_MANUAL_UPDATE);
        }
      }
      catch (SQLException ex)
      {
        LOG.log(Level.FINE, null, ex);
        //Need INIT, probably nothing there
        setState(DBState.NEED_INIT);
        //Create the database
        getEntityManager();
      }
      finally
      {
        try
        {
          conn.close();
        }
        catch (SQLException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
        }
        try
        {
          if (stmt != null)
          {
            stmt.close();
          }
        }
        catch (SQLException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
        }
        try
        {
          if (rs != null)
          {
            rs.close();
          }
        }
        catch (SQLException ex)
        {
          LOG.log(Level.SEVERE, null, ex);
        }
      }
    }
    if (ds != null)
    {
      //Initialize flyway
      initializeFlyway(ds);
      updateDatabase(ds);
    }
    else
    {
      state = DBState.ERROR;
    }

    if (state != DBState.VALID)
    {
      waitForDB();
    }
  }

  public static void waitForDB()
  {
    while (DataBaseManager.getState() != DBState.VALID
            && DataBaseManager.getState() != DBState.UPDATED
            && DataBaseManager.getState() != DBState.ERROR)
    {
      LOG.log(Level.INFO,
              "Waiting for DB initialization. Current state: {0}",
              (DataBaseManager.getState() != null
              ? DataBaseManager.getState().name() : null));
      try
      {
        Thread.sleep(10000);
      }
      catch (InterruptedException ex)
      {
        LOG.log(Level.SEVERE, null, ex);
      }
    }
    LOG.log(Level.INFO, "DB ready, resuming...");
  }

  /**
   * @return the state
   */
  public static DBState getState()
  {
    return state;
  }

  private static void initializeFlyway(DataSource dataSource)
  {
    assert dataSource != null;
    setState(DBState.START_UP);
    Flyway flyway = new Flyway();
    flyway.setDataSource(dataSource);
    MigrationInfo status = flyway.info().current();
    if (status == null)
    {
      setState(DBState.NEED_INIT);
      LOG.info("Initialize the metadata...");
      try
      {
        flyway.init();
        LOG.info("Done!");
      }
      catch (FlywayException fe)
      {
        LOG.log(Level.SEVERE, "Unable to initialize database", fe);
        setState(DBState.ERROR);
      }
    }
    else
    {
      LOG.info("Database has Flyway metadata already...");
      displayDBStatus(status);
    }
  }

  private static void displayDBStatus(MigrationInfo status)
  {
    LOG.log(Level.INFO, "Description: {0}\nState: {1}\nVersion: {2}",
            new Object[]
            {
              status.getDescription(), status.getState(),
              status.getVersion()
            });
  }

  protected static void setState(DBState newState)
  {
    state = newState;
  }

  private static void updateDatabase(DataSource dataSource)
  {
    Flyway flyway = new Flyway();
    try
    {
      flyway.setDataSource(dataSource);
      flyway.setLocations("db.migration");
      LOG.info("Starting migration...");
      flyway.migrate();
      LOG.info("Done!");
    }
    catch (FlywayException fe)
    {
      LOG.log(Level.SEVERE, "Unable to migrate data", fe);
      setState(DBState.ERROR);
    }
    try
    {
      LOG.info("Validating migration...");
      flyway.validate();
      LOG.info("Done!");
      setState(flyway.info().current().getState()
              == MigrationState.SUCCESS ? DBState.VALID : DBState.ERROR);
    }
    catch (FlywayException fe)
    {
      LOG.log(Level.SEVERE, "Unable to validate", fe);
      setState(DBState.ERROR);
    }
  }

  public static EntityManager getEntityManager()
  {
    if (em == null)
    {
      em = getEntityManagerFactory().createEntityManager();
      LOG.log(Level.FINE,
              "Creating EntityManager from: {0}", PU);
      properties = em.getProperties();
    }
    return em;
  }

  /**
   * @return the properties
   */
  public static Map<String, Object> getProperties()
  {
    return properties;
  }

  /**
   * Named query (not for updates)
   *
   * @param query query to execute
   * @return query result
   */
  public static List<Object> namedQuery(String query)
  {
    return namedQuery(query, null, false);
  }

  /**
   * Named query (not for updates)
   *
   * @param query query to execute
   * @param parameters query parameters
   * @return query result
   */
  public static List<Object> namedQuery(String query,
          Map<String, Object> parameters)
  {
    return namedQuery(query, parameters, false);
  }

  @SuppressWarnings("unchecked")
  private static List<Object> namedQuery(String query,
          Map<String, Object> parameters, boolean change)
  {
    EntityTransaction transaction = getEntityManager().getTransaction();
    if (change)
    {
      transaction.begin();
    }
    Query q = getEntityManager().createNamedQuery(query);
    if (parameters != null)
    {
      Iterator<Map.Entry<String, Object>> entries
              = parameters.entrySet().iterator();
      while (entries.hasNext())
      {
        Map.Entry<String, Object> e = entries.next();
        q.setParameter(e.getKey(), e.getValue());
      }
    }
    if (change)
    {
      transaction.commit();
    }
    return q.getResultList();
  }

  public static void close()
  {
    if (em != null)
    {
      em.close();
    }
    if (emf != null)
    {
      emf.close();
    }
    //Set it to null so it's recreated with new Persistence Unit next time is requested.
    emf = null;
    em = null;
  }

  public static EntityTransaction getTransaction()
  {
    return getEntityManager().getTransaction();
  }

  public static List<Object> nativeQuery(String query)
  {
    EntityTransaction transaction = getEntityManager().getTransaction();
    transaction.begin();
    List<Object> resultList
            = getEntityManager().createNativeQuery(query).getResultList();
    transaction.commit();
    return resultList;
  }

  public static void nativeUpdateQuery(String query)
  {
    boolean atomic = false;
    EntityTransaction transaction = getEntityManager().getTransaction();
    if (!getEntityManager().getTransaction().isActive())
    {
      transaction.begin();
      atomic = true;
    }
    getEntityManager().createNativeQuery(query).executeUpdate();
    if (atomic)
    {
      transaction.commit();
    }
  }

  @SuppressWarnings("empty-statement")
  public static void loadDemoData() throws Exception
  {
    Random r = new Random();

    // Add players
    for (int i = 0; i < 10; i++)
    {
      try
      {
        PlayerService.getInstance().savePlayer(new Player("Player " + (i + 1)));
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }

    IGame gameAPI = Lookup.getDefault().lookup(IGame.class);
    Optional<Game> fg
            = GameService.getInstance().findGameByName(gameAPI.getName());
    Game game;
    if (fg.isPresent())
    {
      game = fg.get();
    }
    else
    {
      game = new Game(gameAPI.getName());
    }
    GameService.getInstance().saveGame(game);
    List<Team> teams = TeamService.getInstance().getAll();
    //Load formats
    for (GameFormat format : gameAPI.gameFormats())
    {
      // Check if it exists in the databse
      Optional<Format> f
              = FormatService.getInstance()
                      .findFormatForGame(gameAPI.getName(), format.getName());
      if (!f.isPresent())
      {
        // Let's create it.
        Format newFormat = new Format();
        newFormat.setName(format.getName());
        newFormat.setDescription(format.getDescription());
        newFormat.setGame(game);
        FormatService.getInstance().saveFormat(newFormat);
      }
    }

    // Add a tournaments
    List<TournamentInterface> formats = new ArrayList<>();
    formats.addAll(Lookup.getDefault().lookupAll(TournamentInterface.class));

    for (TournamentInterface format : formats)
    {
      TournamentFormat tf = new TournamentFormat(format.getName(),
              format.getClass().getCanonicalName());
      TournamentService.getInstance().saveTournamentFormat(tf);
    }

    for (int i = 0; i < 10; i++)
    {
      // Set a random start date:
      int startDay = r.nextInt(31);
      Tournament t = new Tournament("Tournament " + (i + 1));
      t.setWinPoints(3);
      t.setLossPoints(0);
      t.setDrawPoints(1);
      t.setStartDate(LocalDateTime.now().minusDays(startDay));
      
      t.setFormat(FormatService.getInstance().getAll().get(0));
      t.setTournamentFormat(TournamentService.getInstance()
              .findFormat(formats.get(r.nextInt(formats.size())).getName()));
      TournamentService.getInstance().saveTournament(t);
    }

    List<Format> formatList = FormatService.getInstance()
            .findFormatByGame(gameAPI.getName());

    // Add matches
    for (int i = 0; i < 10; i++)
    {
      MatchEntry match = new MatchEntry();
      match.setMatchDate(LocalDate.now());
      match.setFormat(FormatService.getInstance().findFormatById(formatList
              .get(r.nextInt(formatList.size())).getFormatPK()).get());
      MatchService.getInstance().saveMatch(match);
      for (int j = 0; j < 2; j++)
      {
        while (!MatchService.getInstance().addTeam(match,
                teams.get(r.nextInt(teams.size()))));
      }

      //Add a result
      boolean win = r.nextBoolean();
      boolean ranked = r.nextBoolean();
      MatchService.getInstance().setResult(match.getMatchHasTeamList().get(0),
              MatchService.getInstance().getResultType(win
                      ? "result.win" : "result.loss").get());
      MatchService.getInstance().setResult(match.getMatchHasTeamList().get(1),
              MatchService.getInstance().getResultType(win
                      ? "result.loss" : "result.win").get());
      MatchService.getInstance().setRanked(match, ranked);

      // Lock the results so records are updated.
      MatchService.getInstance().lockMatchResult(match);
      
      // Update rankings
      MatchService.getInstance().updateRankings(match);
    }
  }

  /**
   * Load stuff from the Lookup.
   *
   * @throws java.lang.Exception
   */
  public static void load() throws Exception
  {
    for (TournamentInterface format : Lookup.getDefault()
            .lookupAll(TournamentInterface.class))
    {
      // Register the tournament formats
      if (TournamentService.getInstance().findFormat(format.getName()) == null)
      {
        TournamentFormat tf = new TournamentFormat(format.getName(),
                format.getClass().getCanonicalName());
        TournamentService.getInstance().addFormat(tf);
      }
    }
    for (IGame gameAPI : Lookup.getDefault().lookupAll(IGame.class))
    {
      // Add game to DB if not there
      Optional<Game> result
              = GameService.getInstance().findGameByName(gameAPI.getName());

      Game game;
      if (result.isPresent())
      {
        game = result.get();
      }
      else
      {
        game = new Game(gameAPI.getName());
        GameService.getInstance().saveGame(game);
      }

      //Load formats
      for (GameFormat format : gameAPI.gameFormats())
      {
        // Check if it exists in the databse
        Optional<Format> f
                = FormatService.getInstance()
                        .findFormatForGame(gameAPI.getName(), format.getName());
        if (!f.isPresent())
        {
          // Let's create it.
          Format newFormat = new Format();
          newFormat.setName(format.getName());
          newFormat.setDescription(format.getDescription());
          newFormat.setGame(game);
          FormatService.getInstance().saveFormat(newFormat);
        }
      }
    }
  }
}
