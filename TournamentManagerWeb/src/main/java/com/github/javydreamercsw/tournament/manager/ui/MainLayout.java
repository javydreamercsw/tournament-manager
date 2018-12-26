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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.util.Exceptions;

import com.github.javydreamercsw.tournament.manager.ui.views.formatlist.FormatList;
import com.github.javydreamercsw.tournament.manager.ui.views.matchlist.MatchList;
import com.github.javydreamercsw.tournament.manager.ui.views.playerlist.PlayerList;
import com.github.javydreamercsw.tournament.manager.ui.views.rankings.RankingList;
import com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist.TournamentList;
import com.github.javydreamercsw.tournament.manager.ui.views.welcome.Welcome;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.Viewport;
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
  private static final Logger LOG
          = Logger.getLogger(MainLayout.class.getSimpleName());

  static
  {
    try
    {
      InitialContext context = new InitialContext();
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

    RouterLink players = new RouterLink(null, PlayerList.class);
    players.add(new Icon(VaadinIcon.USERS), new Text("Players"));
    players.addClassName("main-layout__nav-item");
    
    RouterLink rankings = new RouterLink(null, RankingList.class);
    rankings.add(new Icon(VaadinIcon.TABLE), new Text("Rankings"));
    rankings.addClassName("main-layout__nav-item");

    List<Component> components = new ArrayList<>();
    components.add(welcome);
    components.add(players);
    components.add(rankings);

    if (demo)
    {
      RouterLink tournaments = new RouterLink(null, TournamentList.class);
      tournaments.add(new Icon(VaadinIcon.TROPHY), new Text("Tournaments"));
      tournaments.addClassName("main-layout__nav-item");
      components.add(tournaments);
    }

    RouterLink matches = new RouterLink(null, MatchList.class);
    matches.add(new Icon(VaadinIcon.LIST), new Text("Matches"));
    matches.addClassName("main-layout__nav-item");
    components.add(matches);

    RouterLink formats = new RouterLink(null, FormatList.class);
    formats.add(new Icon(VaadinIcon.ARCHIVES), new Text("Formats"));
    formats.addClassName("main-layout__nav-item");

    components.add(formats);

    Div navigation = new Div(components.toArray(new Component[0]));
    navigation.addClassName("main-layout__nav");

    Div header = new Div(title, navigation);
    header.addClassName("main-layout__header");
    add(header);

    addClassName("main-layout");
  }

  @Override
  public void configurePage(InitialPageSettings settings)
  {
    settings.addMetaTag("apple-mobile-web-app-capable", "yes");
    settings.addMetaTag("apple-mobile-web-app-status-bar-style", "black");
  }
}
