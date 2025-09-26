package com.github.javydreamercsw.tournament.manager.ui.common;

import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;
import com.vaadin.flow.component.ItemLabelGenerator;
import org.openide.util.NbBundle;

public class MatchResultTypeLabelGenerator implements ItemLabelGenerator<MatchResultType> {
  private static final long serialVersionUID = 9199484070469842894L;

  @Override
  public String apply(MatchResultType mrt) {
    return NbBundle.getMessage(DataBaseManager.class, mrt.getType());
  }
}
