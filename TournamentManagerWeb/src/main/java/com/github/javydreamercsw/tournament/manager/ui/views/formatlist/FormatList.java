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
package com.github.javydreamercsw.tournament.manager.ui.views.formatlist;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
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
 * Displays the list of available formats, with a search filter as well as
 * buttons to add a new format or edit existing ones.
 */
@Route(value = "formats", layout = MainLayout.class)
@PageTitle("Format List")
public class FormatList extends TMView
{
  private static final long serialVersionUID = -2389907069192934700L;

  private final TextField searchField = new TextField("", "Search matches");
  private final H2 header = new H2("Matches");
  private final Grid<Format> grid = new Grid<>();

  private final FormatEditorDialog form = new FormatEditorDialog(
          this::saveFormat, this::deleteFormat);

  public FormatList()
  {
    initView();

    addSearchBar();
    addContent();
  }

  private void initView()
  {
    addClassName("formats-list");
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

    Button newButton = new Button("New format", new Icon("lumo", "plus"));
    newButton.getElement().setAttribute("theme", "primary");
    newButton.addClassName("view-toolbar__button");
    newButton.addClickListener(e -> form.open(new Format(),
            AbstractEditorDialog.Operation.ADD));

    viewToolbar.add(searchField, newButton);
    add(viewToolbar);
  }

  private void addContent()
  {
    VerticalLayout container = new VerticalLayout();
    container.setClassName("view-container");
    container.setAlignItems(Alignment.STRETCH);

    grid.addColumn(Format::getName).setHeader("Name").setWidth("8em")
            .setResizable(true);
    grid.addColumn(this::getMatchCount).setHeader("Matches")
            .setWidth("6em");
    grid.addColumn(new ComponentRenderer<>(this::createEditButton))
            .setFlexGrow(0);
    grid.setSelectionMode(SelectionMode.NONE);

    container.add(header, grid);
    add(container);
  }

  private Button createEditButton(Format category)
  {
    Button edit = new Button("Edit", event -> form.open(category,
            AbstractEditorDialog.Operation.EDIT));
    edit.setIcon(new Icon("lumo", "edit"));
    edit.addClassName("format__edit");
    edit.getElement().setAttribute("theme", "tertiary");
    return edit;
  }

  private String getMatchCount(Format category)
  {
    List<MatchEntry> matchesInCategory = MatchService.getInstance()
            .findMatchesWithFormat(category.getName());
    return Integer.toString(matchesInCategory.size());
  }

  @Override
  public void updateView()
  {
    List<Format> formats = new ArrayList<>();
    FormatService.getInstance()
            .findFormatByGame((String) getValue(CURRENT_GAME)).forEach(format ->
    {
      if (format.getGame().getName().equals((String) getValue(CURRENT_GAME)))
      {
        formats.add(format);
      }
    });
    grid.setItems(formats);

    if (searchField.getValue().length() > 0)
    {
      header.setText("Search for “" + searchField.getValue() + "”");
    }
    else
    {
      header.setText("Formats");
    }
  }

  private void saveFormat(Format format,
          AbstractEditorDialog.Operation operation)
  {
    try
    {
      FormatService.getInstance().saveFormat(format);

      Notification.show(
              "Format successfully " + operation.getNameInText() + "ed.",
              3000, Position.BOTTOM_START);
      updateView();
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
      Notification.show("Unable to save format!", 3000,
              Position.BOTTOM_START);
    }
  }

  private void deleteFormat(Format category)
  {
    List<MatchEntry> matchesInCategory = MatchService.getInstance()
            .findMatchesWithFormat(category.getName());

    if (matchesInCategory.isEmpty())
    {
      FormatService.getInstance().deleteFormat(category);

      Notification.show("Format successfully deleted.", 3000,
              Position.BOTTOM_START);
      updateView();
    }
    else
    {
      Notification.show("Unable to delete format!", 3000,
              Position.BOTTOM_START);
    }
  }
}
