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
package com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist;

import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.IllegalOrphanException;
import com.github.javydreamercsw.database.storage.db.controller.exceptions.NonexistentEntityException;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.github.javydreamercsw.tournament.manager.api.TournamentException;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.github.javydreamercsw.tournament.manager.ui.views.TMView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Displays the list of available categories, with a search filter as well as
 * buttons to add a new category or edit existing ones.
 */
@Route(value = "tournaments", layout = MainLayout.class)
@PageTitle("Tournament List")
public class TournamentList extends TMView
{
  private static final long serialVersionUID = -2389907069192934700L;

  private final TextField searchField = new TextField("", "Search tournaments");
  private final H2 header = new H2("Tournaments");
  private final Grid<Tournament> grid = new Grid<>();

  private final TournamentEditorDialog form = new TournamentEditorDialog(
          this::saveTournament, this::deleteTournament);

  public TournamentList()
  {
    initView();

    addSearchBar();
    addContent();
  }

  private void initView()
  {
    addClassName("tournaments-list");
    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
  }

  private void addSearchBar()
  {
    Div viewToolbar = new Div();
    viewToolbar.addClassName("view-toolbar");

    searchField.setPrefixComponent(new Icon("lumo", "search"));
    searchField.addClassName("view-toolbar__search-field");
    searchField.addValueChangeListener(e -> updateView());
    searchField.setValueChangeMode(ValueChangeMode.EAGER);

    Button newButton = new Button("New tournament", new Icon("lumo", "plus"));
    newButton.getElement().setAttribute("theme", "primary");
    newButton.addClassName("view-toolbar__button");
    newButton.addClickListener(e -> form.open(new Tournament(""),
            AbstractEditorDialog.Operation.ADD));

    viewToolbar.add(searchField, newButton);
    add(viewToolbar);
  }

  private String getRoundCount(Tournament t)
  {
    return Integer.toString(t.getRoundList().size());
  }

  private String getTeamCount(Tournament t)
  {
    return Integer.toString(t.getTournamentHasTeamList().size());
  }

  private String getFormat(Tournament t)
  {
    return t.getTournamentFormat().getFormatName();
  }

  private void addContent()
  {
    VerticalLayout container = new VerticalLayout();
    container.setClassName("view-container");
    container.setAlignItems(Alignment.STRETCH);

    grid.addColumn(Tournament::getName).setHeader("Name").setWidth("8em")
            .setResizable(true);
    grid.addColumn(this::getRoundCount).setHeader("Rounds")
            .setWidth("6em");
    grid.addColumn(this::getTeamCount).setHeader("Teams")
            .setWidth("6em");
    grid.addColumn(this::getFormat).setHeader("Format")
            .setWidth("6em");
    grid.addColumn(new ComponentRenderer<>(this::createEditButton))
            .setFlexGrow(0);
    grid.addColumn(new ComponentRenderer<>(this::createControlButton))
            .setFlexGrow(0);
    grid.setSelectionMode(SelectionMode.NONE);

    container.add(header, grid);
    add(container);
  }

  private Button createControlButton(Tournament tournament)
  {
    if (TournamentService.getInstance().hasStarted(tournament))
    {
      Button view = new Button("Manage", event ->
      {
        TournamentManager tm = new TournamentManager(tournament);
        tm.open();
      });
      view.setIcon(new Icon("lumo", "view"));
      view.addClassName("tournament__view");
      view.getElement().setAttribute("theme", "tertiary");
      return view;
    }
    else
    {
      Button start = new Button("Start", event ->
      {
        try
        {
          TournamentService.getInstance().startTournament(tournament);
        }
        catch (TournamentException ex)
        {
          Exceptions.printStackTrace(ex);
          Notification.show(
                  "Unable to start tournament!",
                  3000, Position.BOTTOM_START);
        }
      });
      start.setIcon(new Icon("lumo", "start"));
      start.addClassName("tournament__start");
      start.getElement().setAttribute("theme", "tertiary");
      return start;
    }
  }

  private Button createEditButton(Tournament tournament)
  {
    Button edit = new Button("Edit", event -> form.open(tournament,
            AbstractEditorDialog.Operation.EDIT));
    edit.setIcon(new Icon("lumo", "edit"));
    edit.addClassName("tournament__edit");
    edit.getElement().setAttribute("theme", "tertiary");
    edit.setEnabled(!TournamentService.getInstance().hasStarted(tournament));
    return edit;
  }

  @Override
  public void updateView()
  {
    List<Tournament> matches = TournamentService.getInstance()
            .findTournaments(searchField.getValue());
    grid.setItems(matches);

    if (searchField.getValue().length() > 0)
    {
      header.setText("Search for “" + searchField.getValue() + "”");
    }
    else
    {
      header.setText("Tournaments");
    }
  }

  private void saveTournament(Tournament t,
          AbstractEditorDialog.Operation operation)
  {
    try
    {
      TournamentService.getInstance().saveTournament(t);

      Notification.show(
              "Tournament successfully " + operation.getNameInText() + "ed.",
              3000, Position.BOTTOM_START);
      updateView();
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  private void deleteTournament(Tournament t)
  {
    if (TournamentService.getInstance().findTournament(t.getTournamentPK()) != null)
    {
      try
      {
        TournamentService.getInstance().deleteTournament(t);

        Notification.show("Tournament successfully deleted.", 3000,
                Position.BOTTOM_START);
        updateView();
      }
      catch (IllegalOrphanException | NonexistentEntityException ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      Notification.show("Unable to delete tournament!", 3000,
              Position.BOTTOM_START);
    }
  }
}
