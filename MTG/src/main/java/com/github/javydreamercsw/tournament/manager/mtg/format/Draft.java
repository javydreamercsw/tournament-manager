package com.github.javydreamercsw.tournament.manager.mtg.format;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;

@ServiceProvider(service = GameFormat.class)
public class Draft implements GameFormat
{
  @Override
  public String getName()
  {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getDescription()
  {
    return "Want a way to play that offers a level playing field and lets you check out new cards at the same time? Then a limited format like booster draft may be for you. Unlike constructed formats, where you arrive with a carefully constructed deck ready to play, limited formats allow you to build a deck from new cards as part of the game.\n" +
"\n" +
"At the start of booster draft, each player opens a booster pack and picks a single card. (Don’t show the other players what you pick!) Then everyone passes the rest of their pack to player on their left, each player then picks a card from the pack they just received before passing again. This process continues until all the cards in those packs have been drafted. Then each player opens a second pack, but this time, you pass the pack to your right. After all those cards are drafted, you do the same with the third pack, passing to the left again.\n For more details: https://magic.wizards.com/en/game-info/gameplay/formats/booster-draft";
  }
}
