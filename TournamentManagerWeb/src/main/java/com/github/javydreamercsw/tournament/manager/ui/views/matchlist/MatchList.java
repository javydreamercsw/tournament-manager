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

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
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
@Route(value = "matches", layout = MainLayout.class)
@PageTitle("Match List")
public class MatchList extends TMView
{
  private static final long serialVersionUID = -2389907069192934700L;

  private final TextField searchField = new TextField("", "Search matches");
  private final H2 header = new H2("Formats");
  private final Grid<MatchEntry> grid = new Grid<>();

  private final MatchEditorDialog form = new MatchEditorDialog(
          this::saveMatch, this::deleteMatch);

  public MatchList()
  {
    initView();

    addSearchBar();
    addContent();
  }

  private void initView()
  {
    addClassName("matches-list");
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

    Button newButton = new Button("New match", new Icon("lumo", "plus"));
    newButton.getElement().setAttribute("theme", "primary");
    newButton.addClassName("view-toolbar__button");
    newButton.addClickListener(e -> form.open(new MatchEntry(),
            AbstractEditorDialog.Operation.ADD));

    viewToolbar.add(searchField, newButton);
    add(viewToolbar);
  }

  private void addContent()
  {
    VerticalLayout container = new VerticalLayout();
    container.setClassName("view-container");
    container.setAlignItems(Alignment.STRETCH);

    grid.addColumn(MatchEntry::getFormat).setHeader("Format").setWidth("8em")
            .setResizable(true);
    grid.addColumn(new ComponentRenderer<>(this::createEditButton))
            .setFlexGrow(0);
    grid.setSelectionMode(SelectionMode.NONE);

    container.add(header, grid);
    add(container);
  }

  private Button createEditButton(MatchEntry category)
  {
    Button edit = new Button("Edit", event -> form.open(category,
            AbstractEditorDialog.Operation.EDIT));
    edit.setIcon(new Icon("lumo", "edit"));
    edit.addClassName("match__edit");
    edit.getElement().setAttribute("theme", "tertiary");
    return edit;
  }

  @Override
  public void updateView()
  {
    List<MatchEntry> matches = MatchService.getInstance()
            .findMatchesWithFormat(searchField.getValue());
    grid.setItems(matches);

    if (searchField.getValue().length() > 0)
    {
      header.setText("Search for “" + searchField.getValue() + "”");
    }
    else
    {
      header.setText("Matches");
    }
  }

  private void saveMatch(MatchEntry match,
          AbstractEditorDialog.Operation operation)
  {
    try
    {
      MatchService.getInstance().saveMatch(match);
      
      Notification.show(
              "Match successfully " + operation.getNameInText() + "ed.",
              3000, Position.BOTTOM_START);
      updateView();
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  private void deleteMatch(MatchEntry match)
  {
    List<MatchEntry> matchesInCategory = MatchService.getInstance()
            .findMatch(match.getMatchEntryPK());

    if (matchesInCategory.isEmpty())
    {
      try
      {
        MatchService.getInstance().deleteMatch(match);

        Notification.show("Format successfully deleted.", 3000,
                Position.BOTTOM_START);
        updateView();
      }
      catch (Exception ex)
      {
        Exceptions.printStackTrace(ex);
      }
    }
    else
    {
      Notification.show("Unable to delete match!", 3000,
              Position.BOTTOM_START);
    }
  }
}
