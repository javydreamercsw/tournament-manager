package com.github.javydreamercsw.tournament.manager.ui.common;

import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.vaadin.flow.component.ItemLabelGenerator;

public class GameLabelGenerator implements ItemLabelGenerator<IGame>
{
  private static final long serialVersionUID = -4396467477758231860L;

  @Override
  public String apply(IGame g)
  {
    return g.getName();
  }
}
