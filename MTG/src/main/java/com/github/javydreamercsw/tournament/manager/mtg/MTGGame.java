package com.github.javydreamercsw.tournament.manager.mtg;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;
import com.github.javydreamercsw.tournament.manager.api.IGame;

@ServiceProvider(service = IGame.class)
public class MTGGame implements IGame
{
  @Override
  public String getName()
  {
    return "Magic the Gathering";
  }

  @Override
  public List<GameFormat> gameFormats()
  {
    List<GameFormat> formats= new ArrayList<>();
    formats.addAll(Lookup.getDefault().lookupAll(GameFormat.class));
    return formats;
  }
}
