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
package com.github.javydreamercsw.tournament.manager.ui;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.MatchEntry;
import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.Team;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.database.storage.db.server.TeamService;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.ui.views.formatlist.FormatList;
import com.github.javydreamercsw.tournament.manager.ui.views.matchlist.MatchList;
import com.github.javydreamercsw.tournament.manager.ui.views.playerlist.PlayerList;
import com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist.TournamentList;
import com.github.javydreamercsw.tournament.manager.ui.views.welcome.Welcome;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.page.Viewport;
import com.vaadin.flow.router.HighlightConditions;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.InitialPageSettings;
import com.vaadin.flow.server.PageConfigurator;

/**
 * The main layout contains the header with the navigation buttons, and the
 * child views below that.
 */
@HtmlImport("frontend://styles/shared-styles.html")
@Viewport("width=device-width, minimum-scale=1.0, initial-scale=1.0, user-scalable=yes")
public class MainLayout extends Div
        implements RouterLayout, PageConfigurator
{
  private static final long serialVersionUID = 1412472530637429687L;
  private static boolean demo = false;

  static
  {
    try
    {
      InitialContext context = new InitialContext();
      String JNDIDB = (String) context
              .lookup("java:comp/env/tm/JNDIDB");
      DataBaseManager.setPersistenceUnitName(JNDIDB);

      demo = (Boolean) context
              .lookup("java:comp/env/tm/demo");
    }
    catch (NamingException ex)
    {
      Exceptions.printStackTrace(ex);
    }
  }

  public MainLayout() throws NamingException
  {
    H2 title = new H2("Tournament Buddy");
    title.addClassName("main-layout__title");

    RouterLink welcome = new RouterLink(null, Welcome.class);
    welcome.add(new Icon(VaadinIcon.HOME), new Text("Welcome"));
    welcome.addClassName("main-layout__nav-item");

    RouterLink tournaments = new RouterLink(null, TournamentList.class);
    tournaments.add(new Icon(VaadinIcon.TROPHY), new Text("Tournaments"));
    tournaments.addClassName("main-layout__nav-item");

    RouterLink matches = new RouterLink(null, MatchList.class);
    matches.add(new Icon(VaadinIcon.LIST), new Text("Matches"));
    matches.addClassName("main-layout__nav-item");
    // Only show as active for the exact URL, but not for sub paths
    matches.setHighlightCondition(HighlightConditions.sameLocation());

    RouterLink formats = new RouterLink(null, FormatList.class);
    formats.add(new Icon(VaadinIcon.ARCHIVES), new Text("Formats"));
    formats.addClassName("main-layout__nav-item");

    RouterLink players = new RouterLink(null, PlayerList.class);
    players.add(new Icon(VaadinIcon.USERS), new Text("Players"));
    players.addClassName("main-layout__nav-item");

    Div navigation = new Div(welcome, players, tournaments, matches, formats);
    navigation.addClassName("main-layout__nav");

    Div header = new Div(title, navigation);
    header.addClassName("main-layout__header");
    add(header);

    addClassName("main-layout");

    // Check if it's configured for demo
    if (demo && PlayerService.getInstance().findPlayers("").isEmpty())
    {
      Notification.show(
              "Loading demo data...",
              3000, Position.MIDDLE);
      // Add players
      for (int i = 0; i < 10; i++)
      {
        try
        {
          PlayerService.getInstance().savePlayer(new Player("Player " + (i + 1)));
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }

      // Add a tournaments
      for (int i = 0; i < 10; i++)
      {
        Tournament t = new Tournament("Tournament " + (i + 1));
        t.setWinPoints(3);
        t.setLossPoints(0);
        t.setDrawPoints(1);
        TournamentService.getInstance().saveTournament(t);
      }

      List<Team> teams = TeamService.getInstance().findTeams("");
      List<Format> formatList = FormatService.getInstance()
              .findFormatByGame(Lookup.getDefault().lookup(IGame.class).getName());
      Random r = new Random();
      // Add matches
      for (int i = 0; i < 10; i++)
      {
        try
        {
          MatchEntry match = new MatchEntry();
          match.setMatchDate(LocalDate.now());
          MatchService.getInstance().saveMatch(match);
          MatchService.getInstance().addTeam(match,
                  teams.get(r.nextInt(teams.size())));
          MatchService.getInstance().addTeam(match,
                  teams.get(r.nextInt(teams.size())));
          match.setFormat(FormatService.getInstance().findFormatById(formatList
                  .get(r.nextInt(formatList.size())).getFormatPK()).get());
          MatchService.getInstance().saveMatch(match);
        }
        catch (Exception ex)
        {
          Exceptions.printStackTrace(ex);
        }
      }

      Notification.show(
              "Loading demo data done!",
              3000, Position.MIDDLE);
    }
  }

  @Override
  public void configurePage(InitialPageSettings settings)
  {
    settings.addMetaTag("apple-mobile-web-app-capable", "yes");
    settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
  }
}
