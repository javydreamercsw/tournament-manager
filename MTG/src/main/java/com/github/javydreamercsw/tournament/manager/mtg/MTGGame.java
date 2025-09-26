package com.github.javydreamercsw.tournament.manager.mtg;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import java.util.ArrayList;
import java.util.List;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = IGame.class)
public class MTGGame implements IGame {
  @Override
  public String getName() {
    return "Magic the Gathering";
  }

  @Override
  public List<GameFormat> gameFormats() {
    return new ArrayList<>(Lookup.getDefault().lookupAll(GameFormat.class));
  }
}
