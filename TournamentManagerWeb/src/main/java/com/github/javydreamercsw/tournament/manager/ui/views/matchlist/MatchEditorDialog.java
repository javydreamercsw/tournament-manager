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

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.server.TeamService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;

/**
 * A dialog for editing {@link Format} objects.
 */
public class MatchEditorDialog extends AbstractEditorDialog<MatchEntry>
{
  private static final long serialVersionUID = 2349638969280300323L;
  private final Grid<Team> grid = new Grid<>();

  public MatchEditorDialog(BiConsumer<MatchEntry, Operation> itemSaver,
          Consumer<MatchEntry> itemDeleter)
  {
    super("match", itemSaver, itemDeleter);
    addTeams();
  }

  @Override
  protected void confirmDelete()
  {
    doDelete(getCurrentItem());
  }

  private void addTeams()
  { 
    getFormLayout().add(grid);

    grid.addColumn(Team::getName).setHeader("Name").setWidth("8em")
            .setResizable(true);

    grid.addColumn(team ->
    {
      return new Checkbox("", new TeamSelectionListener(team));
    });
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
      System.out.println(team.getName() + "Selected? " + e.getSource().getValue());
    }
  }

  @Override
  public void open()
  {
    List<Team> teams = TeamService.getInstance().findTeams("");
    grid.setItems(teams);
    System.out.println("Amount of Teams: "+teams.size());
    super.open();
  }  
}
