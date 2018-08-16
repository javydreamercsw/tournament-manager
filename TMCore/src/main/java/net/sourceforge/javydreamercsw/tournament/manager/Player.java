package net.sourceforge.javydreamercsw.tournament.manager;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.openide.util.Lookup;

import net.sourceforge.javydreamercsw.tournament.manager.api.TournamentPlayerInterface;
import net.sourceforge.javydreamercsw.tournament.manager.api.Variables;
import net.sourceforge.javydreamercsw.tournament.manager.api.standing.RecordInterface;

/**
 *
 * @author Javier A. Ortiz Bultron <javier.ortiz.78@gmail.com>
 */
public class Player implements TournamentPlayerInterface
{

  private RecordInterface record = null;
  private final Map<String, Object> variables = new HashMap<>();
  private final int id;

  public Player(String name, int id)
  {
    variables.put(Variables.PLAYER_NAME.getDisplayName(), name);
    record = Lookup.getDefault().lookup(RecordInterface.class).getNewInstance();
    this.id = id;
    //TODO: Allow player modification
  }

  /**
   * Get the value for the specified key.
   *
   * @param key Key to look for
   * @return value for the key provided or null if not found.
   */
  @Override
  public Object get(String key)
  {
    return variables.get(key);
  }

  @Override
  public String toString()
  {
    return MessageFormat.format("{0} ({1}-{2}-{3})",
            get(Variables.PLAYER_NAME.getDisplayName()),
            getRecord().getWins(),
            getRecord().getLosses(),
            getRecord().getDraws());
  }

  @Override
  public String getName()
  {
    return ((String) get(Variables.PLAYER_NAME.getDisplayName()));
  }

  @Override
  public int getID()
  {
    return getId();
  }

  @Override
  public RecordInterface getRecord()
  {
    return record;
  }

  @Override
  public TournamentPlayerInterface createInstance(String name, int id, int wins,
          int losses, int draws)
  {
    Player player = new Player(name, id);
    player.getRecord().setWins(wins);
    player.getRecord().setLosses(losses);
    player.getRecord().setDraws(draws);
    return player;
  }

  @Override
  public TournamentPlayerInterface createInstance(String name, int id)
  {
    return createInstance(name, id, 0, 0, 0);
  }

  /**
   * @return the id
   */
  public int getId()
  {
    return id;
  }
}
