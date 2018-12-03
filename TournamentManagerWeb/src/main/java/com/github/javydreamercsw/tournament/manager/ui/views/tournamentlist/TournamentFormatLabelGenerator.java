package com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist;

import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.vaadin.flow.component.ItemLabelGenerator;

class TournamentFormatLabelGenerator implements ItemLabelGenerator<TournamentFormat>
{
  private static final long serialVersionUID = -738603579674658479L;

  @Override
  public String apply(TournamentFormat f)
  {
    return f.getFormatName();
  } 
}
