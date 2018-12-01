package com.github.javydreamercsw.tournament.manager.ui.views.matchlist;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class ResultForm extends FormLayout
{
  private static final long serialVersionUID = -777301071814867123L;
  private Grid<MatchHasTeam> resultGrid = new Grid<>();

  public ResultForm(MatchEntry me)
  {
    resultGrid.addColumn(new ComponentRenderer<>((mht)
            -> new Label(mht.getTeam().getName()))).setHeader("Team")
            .setWidth("8em").setResizable(true);
    resultGrid.addColumn(new ComponentRenderer<>((mht) ->
    {
      ComboBox<MatchResultType> cb = new ComboBox<>();
      cb.setDataProvider(new ListDataProvider(MatchService.getInstance().getResultTypes()));
      cb.setItemLabelGenerator(new MatchResultTypeLabelGenerator());
      cb.setRequired(true);
      cb.setPreventInvalidInput(true);
      cb.setAllowCustomValue(false);
      if (mht.getMatchResult() != null)
      {
        cb.setValue(mht.getMatchResult().getMatchResultType());
      }
      cb.addValueChangeListener(new ValueChangeListener()
      {
        private static final long serialVersionUID = 5377566605252849942L;

        @Override
        public void valueChanged(ValueChangeEvent e)
        {
          try
          {
            MatchService.getInstance().setResult(mht, cb.getValue());
          }
          catch (Exception ex)
          {
            Exceptions.printStackTrace(ex);
          }
        }
      });
      return cb;
    })).setHeader("Result").setWidth("8em").setResizable(true);
    resultGrid.setItems(me.getMatchHasTeamList());
    add(resultGrid);
  }
}
