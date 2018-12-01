package com.github.javydreamercsw.tournament.manager.mtg.format;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;

@ServiceProvider(service = GameFormat.class)
public class Brawl implements GameFormat
{
  @Override
  public String getName()
  {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getDescription()
  {
    return "Choose your champion! Brawl is a little like Standard, a little like Commander, and a uniquely exciting deck-brewing challenge. Build a deck around a specific legendary creature or planeswalker from the Standard card pool, and battle against friends in one-on-one or multiplayer free-for-all games.\n For more details: https://magic.wizards.com/en/game-info/gameplay/formats/brawl";
  }
}
