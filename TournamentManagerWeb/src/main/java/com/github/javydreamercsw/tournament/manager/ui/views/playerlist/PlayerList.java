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
package com.github.javydreamercsw.tournament.manager.ui.views.playerlist;

import java.util.List;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Record;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
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
@Route(value = "players", layout = MainLayout.class)
@PageTitle("Player List")
public class PlayerList extends TMView
{
  private static final long serialVersionUID = -2389907069192934700L;

  private final TextField searchField = new TextField("", "Search players");
  private final H2 header = new H2("Players");
  private final Grid<Player> grid = new Grid<>();

  private final PlayerEditorDialog form = new PlayerEditorDialog(
          this::savePlayer, this::deletePlayer);

  public PlayerList()
  {
    initView();

    addSearchBar();
    addContent();
  }

  private void initView()
  {
    addClassName("players-list");
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

    Button newButton = new Button("New player", new Icon("lumo", "plus"));
    newButton.getElement().setAttribute("theme", "primary");
    newButton.addClassName("view-toolbar__button");
    newButton.addClickListener(e -> form.open(new Player(""),
            AbstractEditorDialog.Operation.ADD));

    viewToolbar.add(searchField, newButton);
    add(viewToolbar);
  }

  private void addContent()
  {
    VerticalLayout container = new VerticalLayout();
    container.setClassName("view-container");
    container.setAlignItems(Alignment.STRETCH);

    grid.addColumn(Player::getName).setHeader("Name").setWidth("8em")
            .setResizable(true);
    grid.addColumn(this::getWinCount).setHeader("Wins")
            .setWidth("6em");
    grid.addColumn(this::getLossCount).setHeader("Losses")
            .setWidth("6em");
    grid.addColumn(this::getDrawCount).setHeader("Draws")
            .setWidth("6em");
    grid.addColumn(new ComponentRenderer<>(this::createEditButton))
            .setFlexGrow(0);
    grid.setSelectionMode(SelectionMode.NONE);

    container.add(header, grid);
    add(container);
  }

  private Button createEditButton(Player player)
  {
    Button edit = new Button("Edit", event -> form.open(player,
            AbstractEditorDialog.Operation.EDIT));
    edit.setIcon(new Icon("lumo", "edit"));
    edit.addClassName("player__edit");
    edit.getElement().setAttribute("theme", "tertiary");
    return edit;
  }

  private String getWinCount(Player player)
  {
    int wins = 0;
    for (Record r : player.getRecordList())
    {
      wins += r.getWins();
    }
    return Integer.toString(wins);
  }

  private String getLossCount(Player player)
  {
    int losses = 0;
    for (Record r : player.getRecordList())
    {
      losses += r.getLoses();
    }
    return Integer.toString(losses);
  }

  private String getDrawCount(Player player)
  {
    int losses = 0;
    for (Record r : player.getRecordList())
    {
      losses += r.getLoses();
    }
    return Integer.toString(losses);
  }

  @Override
  public void updateView()
  {
    List<Player> players = PlayerService.getInstance()
            .findPlayers(searchField.getValue());
    grid.setItems(players);

    if (searchField.getValue().trim().length() > 0)
    {
      header.setText("Search for “" + searchField.getValue() + "”");
    }
    else
    {
      header.setText("Players");
    }
  }

  private void savePlayer(Player player,
          AbstractEditorDialog.Operation operation)
  {
    try
    {
      PlayerService.getInstance().savePlayer(player);
      
      Notification.show(
              "Player successfully " + operation.getNameInText() + "ed.",
              3000, Position.BOTTOM_START);
      updateView();
    }
    catch (Exception ex)
    {
      Exceptions.printStackTrace(ex);
      Notification.show("Unable to save player!", 3000,
              Position.BOTTOM_START);
    }
  }

  private void deletePlayer(Player player)
  {
    List<Player> matchesInCategory = PlayerService.getInstance()
            .findPlayers(player.getName());

    if (!matchesInCategory.isEmpty()
            && player.getTeamList().size() <= 1)
    {
      PlayerService.getInstance().deletePlayer(player);

      Notification.show("Player successfully deleted.", 3000,
              Position.BOTTOM_START);
      updateView();
    }
    else
    {
      Notification.show("Unable to delete player!", 3000,
              Position.BOTTOM_START);
      if (player.getTeamList().size() > 1)
      {
        StringBuilder sb = new StringBuilder();
        player.getTeamList().forEach(team ->
        {
          if (team.getPlayerList().size() > 1)
          {
            sb.append(team.getName()).append("\n");
          }
        });
        Notification.show("This payer is member of multi-person teams that need "
                + "to be deleted before deleting this user!\n"
                + "Delete the following teams:\n" + sb.toString(), 3000,
                Position.BOTTOM_START);
      }
    }
  }
}
