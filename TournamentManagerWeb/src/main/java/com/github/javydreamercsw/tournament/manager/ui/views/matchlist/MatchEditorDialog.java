/*
 * Copyright 2000-2017 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.github.javydreamercsw.tournament.manager.ui.views.matchlist;

import static com.github.javydreamercsw.tournament.manager.ui.views.TMView.CURRENT_GAME;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.MatchHasTeam;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.server.VaadinService;

/**
 * A dialog for editing {@link Format} objects.
 */
public class MatchEditorDialog extends AbstractEditorDialog<MatchEntry>
{
  private static final long serialVersionUID = 2349638969280300323L;
  private final Grid<Team> grid = new Grid<>();
  private final ComboBox<Format> cb = new ComboBox<>();
  private final DatePicker datePicker = new DatePicker();

  public MatchEditorDialog(BiConsumer<MatchEntry, Operation> itemSaver,
          Consumer<MatchEntry> itemDeleter)
  {
    super("match", itemSaver, itemDeleter);
    addTeams();
    addFormat();
    addDate();
    validate();
  }

  @Override
  protected void confirmDelete()
  {
    boolean canDelete = getCurrentItem().getRound() != null;

    if (canDelete)
    {
      // Check if it has results already.
      for (MatchHasTeam mht : getCurrentItem().getMatchHasTeamList())
      {
        if (mht.getMatchResult().getMatchResultPK().getId() == 1)
        {
          canDelete = false;
          break;
        }
      }
    }
    if (!canDelete)
    {
      openConfirmationDialog("Delete match",
              "Are you sure you want to delete this match?",
              "You will lose all it's data.");
    }
    else
    {
      doDelete(getCurrentItem());
    }
  }

  private void addTeams()
  {
    getFormLayout().add(grid);

    grid.addColumn(Team::getName).setHeader("Name").setWidth("8em")
            .setResizable(true);

    grid.addColumn(new ComponentRenderer<>(team ->
    {
      Checkbox checkbox = new Checkbox("", new TeamSelectionListener(team));

      // Preselect if the team is already in the match
      for (MatchHasTeam mht : getCurrentItem().getMatchHasTeamList())
      {
        if (Objects.equals(mht.getTeam().getId(), team.getId()))
        {
          checkbox.setValue(true);
          break;
        }
      }
      return checkbox;
    })).setHeader("Selected");
  }

  private void addFormat()
  {
    List<Format> formats = FormatService.getInstance().findFormatByGame(
            (String) VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(CURRENT_GAME));

    cb.setLabel("Select a Format: ");
    cb.setDataProvider(new ListDataProvider(formats));
    cb.setItemLabelGenerator(new FormatLabelGenerator());
    cb.setRequired(true);
    cb.setPreventInvalidInput(true);
    cb.setAllowCustomValue(false);
    cb.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 5377566605252849942L;

      @Override
      public void valueChanged(ValueChangeEvent e)
      {
        validate();
      }
    });

    getBinder().forField(cb).bind(MatchEntry::getFormat, MatchEntry::setFormat);

    cb.setEnabled(formats.size() > 1);
    if (formats.size() == 1)
    {
      // Select the only option
      cb.setValue(formats.get(0));
    }

    getFormLayout().add(cb);
  }

  private void addDate()
  {
    getBinder().forField(datePicker)
            .bind(MatchEntry::getMatchDate, MatchEntry::setMatchDate);

    datePicker.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 5377566605252849942L;

      @Override
      public void valueChanged(ValueChangeEvent e)
      {
        validate();
      }
    });
    getFormLayout().add(datePicker);

    if (datePicker.getValue() == null)
    {
      datePicker.setValue(LocalDate.now());
    }
  }

  @Override
  protected boolean isValid()
  {
    return cb.getValue() != null
            && datePicker.getValue() != null
            && getCurrentItem().getMatchHasTeamList().size() >= 2;
  }

  private class TeamSelectionListener implements
          ValueChangeListener<ComponentValueChangeEvent<Checkbox, Boolean>>
  {
    private static final long serialVersionUID = 1991737314876349305L;
    private final Team team;

    public TeamSelectionListener(Team team)
    {
      this.team = team;
    }

    @Override
    public void valueChanged(ComponentValueChangeEvent<Checkbox, Boolean> e)
    {
      if (e.getSource().getValue())
      {
        try
        {
          // Add it
          MatchService.getInstance().addTeam(getCurrentItem(), team);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }
      else
      {
        try
        {
          // Remove it
          MatchService.getInstance().removeTeam(getCurrentItem(), team);
        }
        catch (NonexistentEntityException ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }
      validate();
    }
  }

  @Override
  public void open()
  {
    List<Team> teams = TeamService.getInstance().getAll();
    grid.setItems(teams);
    super.open();
  }

  private class FormatLabelGenerator implements ItemLabelGenerator<Format>
  {
    private static final long serialVersionUID = -738603579674658479L;

    @Override
    public String apply(Format f)
    {
      return f.getName();
    }
  }
}
