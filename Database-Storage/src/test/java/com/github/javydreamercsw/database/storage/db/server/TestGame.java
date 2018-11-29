package com.github.javydreamercsw.database.storage.db.server;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;
import com.github.javydreamercsw.tournament.manager.api.IGame;

@ServiceProvider(service = IGame.class)
public class TestGame implements IGame
{
  @Override
  public String getName()
  {
    return "Test Game";
  }

  @Override
  public List<GameFormat> gameFormats()
  {
    List<GameFormat> formats = new ArrayList<>();
    formats.add(new GameFormat() {
      @Override
      public String getName()
      {
        return "Dummy Format";
      }

      @Override
      public String getDescription()
      {
        return "Dummy Format Description";
      }
    });
    return formats;
  }
}
