package com.github.javydreamercsw.tournament.manager.ui.common;

import com.github.javydreamercsw.database.storage.db.Format;
import com.vaadin.flow.component.ItemLabelGenerator;

public class FormatLabelGenerator implements ItemLabelGenerator<Format>
{
  private static final long serialVersionUID = -738603579674658479L;

  @Override
  public String apply(Format f)
  {
    return f.getName();
  } 
}
