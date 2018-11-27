package com.github.javydreamercsw.tournament.manager.mtg.format;

import org.openide.util.lookup.ServiceProvider;

import com.github.javydreamercsw.tournament.manager.api.GameFormat;

@ServiceProvider(service = GameFormat.class)
public class Commander implements GameFormat
{
  @Override
  public String getName()
  {
    return this.getClass().getSimpleName();
  }

  @Override
  public String getDescription()
  {
    return "Commander is an exciting, unique way to play Magic that is all about awesome legendary creatures, big plays, and battling your friends in epic multiplayer games! In Commander, each player chooses a legendary creature as the commander of their deck. They then play with a 99-card deck that contains only cards of their commander's colors. Also, other than basic lands, each deck can only use one copy of any card. During the game, you can cast your commander multiple times, meaning your favorite Legendary Creature can come back again and again to lead the charge as you battle for victory!\n"
            + "\n"
            + "These Commander decks introduce an ability called eminence that lets your commander lead from the command zone, even before they have entered the battlefield. The eminence ability appears on characters from all parts of Magic lore, allowing you to call on the power and skill of some of the best leaders in the Multiverse.\n"
            + "More details here: https://magic.wizards.com/en/content/commander-format";
  }
}
