package com.github.javydreamercsw.tournament.manager.mtg.format;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;

@ServiceProvider(service = GameFormat.class)
public class Standard implements GameFormat
{
  @Override
  public String getName()
  {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getDescription()
  {
    return "Standard is a dynamic format where you build decks and play using cards in your collection from recently released Magic sets. Evolving gameplay and fresh strategies make it one of the most fun and popular ways to play Magic.\n For more details: https://magic.wizards.com/en/content/standard-formats-magic-gathering";
  }
}
