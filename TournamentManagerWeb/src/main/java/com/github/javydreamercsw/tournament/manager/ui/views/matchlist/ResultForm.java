package com.github.javydreamercsw.tournament.manager.ui.views.matchlist;

import com.github.javydreamercsw.tournament.manager.ui.common.MatchResultTypeLabelGenerator;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.MatchResultType;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;

public class ResultForm extends FormLayout
{
  private static final long serialVersionUID = -777301071814867123L;
  private Grid<MatchHasTeam> resultGrid = new Grid<>();
  private Button lock = new Button("Lock Results");
  private final MatchEntry entry;

  public ResultForm(MatchList ml,Dialog dialog, MatchEntry me)
  {
    this.entry = me;
    resultGrid.addColumn(new ComponentRenderer<>((mht)
            -> new Label(mht.getTeam().getName()))).setHeader("Team")
            .setWidth("8em").setResizable(true);
    resultGrid.addColumn(new ComponentRenderer<>((mht) ->
    {
      ComboBox<MatchResultType> cb = new ComboBox<>();
      cb.setDataProvider(new ListDataProvider<>(MatchService.getInstance().getResultTypes()));
      cb.setItemLabelGenerator(new MatchResultTypeLabelGenerator());
      cb.setRequired(true);
      cb.setPreventInvalidInput(true);
      cb.setAllowCustomValue(false);
      if (mht.getMatchResult() != null)
      {
        cb.setValue(mht.getMatchResult().getMatchResultType());
        cb.setEnabled(!mht.getMatchResult().getLocked());
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
            validate();
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
    lock.addClickListener(event ->
    {
      MatchService.getInstance().findMatch(entry.getMatchEntryPK())
              .getMatchHasTeamList().forEach((mht) ->
      {
        try
        {
          MatchService.getInstance().lockMatchResult(mht.getMatchResult());
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      });
      dialog.close();
      ml.updateView();
    });
    add(lock);
    add(resultGrid);
    validate();
  }

  private void validate()
  {
    boolean valid = true;
    if (entry != null)
    {
      for (MatchHasTeam mht : MatchService.getInstance()
              .findMatch(entry.getMatchEntryPK()).getMatchHasTeamList())
      {
        if (mht.getMatchResult() == null || mht.getMatchResult().getLocked())
        {
          valid = false;
          break;
        }
      }
    }
    lock.setEnabled(valid);
  }
}
