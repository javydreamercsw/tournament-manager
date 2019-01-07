package com.github.javydreamercsw.tournament.manager.ui.views.rankings;

import static com.github.javydreamercsw.tournament.manager.ui.views.TMView.CURRENT_GAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import org.openide.util.Lookup;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.GameService;
import com.github.javydreamercsw.database.storage.db.server.RankingManager;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.github.javydreamercsw.tournament.manager.ui.common.FormatLabelGenerator;
import com.github.javydreamercsw.tournament.manager.ui.common.GameLabelGenerator;
import com.github.javydreamercsw.tournament.manager.ui.views.TMView;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;

/**
 * Displays the list of rankings.
 */
@Route(value = "rankings", layout = MainLayout.class)
@PageTitle("Rankings")
public class RankingList extends TMView
{
  private static final long serialVersionUID = 495427102994660040L;
  private final ComboBox<IGame> game = new ComboBox<>("Game");
  private final ComboBox<Format> format = new ComboBox<>("Format");
  private final Grid<TeamHasFormatRecord> rankings = new Grid<>();
  private Map<Integer, List<TeamHasFormatRecord>> ranks = new HashMap<>();

  public RankingList()
  {
    VerticalLayout container = new VerticalLayout();
    // Add the top menu
    HorizontalLayout header = new HorizontalLayout();

    // Add the game selector
    game.setItemLabelGenerator(new GameLabelGenerator());
    game.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 5377566605252849942L;

      @Override
      public void valueChanged(ValueChangeEvent e)
      {
        if (game.getValue() != null)
        {
          List<Format> formats = FormatService.getInstance().findFormatByGame(
                  game.getValue().getName());
          format.setDataProvider(new ListDataProvider<>(formats));
          format.setValue(null);
        }
        else
        {
          format.setItems(new ArrayList<>());
        }
        updateView();
      }
    });
    List<IGame> games = new ArrayList<>();
    games.addAll(Lookup.getDefault().lookupAll(IGame.class));
    game.setDataProvider(new ListDataProvider<>(games));

    header.add(game);

    // Add the format selector
    format.setItemLabelGenerator(new FormatLabelGenerator());
    format.setRequired(true);
    format.setPreventInvalidInput(true);
    format.setAllowCustomValue(false);
    format.addValueChangeListener(event -> updateView());

    header.add(format);

    rankings.addColumn(this::getRowIndex).setWidth("2em")
            .setResizable(true);
    rankings.addColumn(this::getTeam).setHeader("Team").setWidth("4em")
            .setResizable(true);
    rankings.addColumn(TeamHasFormatRecord::getPoints).setHeader("Points")
            .setWidth("4em");
    rankings.addColumn(this::getWins).setHeader("Wins")
            .setWidth("4em");
    rankings.addColumn(this::getLosses).setHeader("Losses")
            .setWidth("4em");
    rankings.addColumn(this::getDraws).setHeader("Draws")
            .setWidth("4em");
    rankings.addColumn(TeamHasFormatRecord::getMean).setHeader("Mean")
            .setWidth("6em");
    rankings.addColumn(TeamHasFormatRecord::getStandardDeviation)
            .setHeader("Standard Deviation")
            .setWidth("6em");
    rankings.setSelectionMode(SelectionMode.NONE);

    container.add(header, rankings);
    add(container);

    // By default select the current game
    Optional<Game> currentGame = GameService.getInstance().findGameByName(
            (String) VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(CURRENT_GAME));
    if (currentGame.isPresent())
    {
      String name = currentGame.get().getName();
      for (IGame ig : Lookup.getDefault().lookupAll(IGame.class))
      {
        if (ig.getName().equals(name))
        {
          game.setValue(ig);
          break;
        }
      }
    }
    game.setEnabled(games.size() > 1);
  }

  private String getRowIndex(TeamHasFormatRecord thfr)
  {
    for (Entry<Integer, List<TeamHasFormatRecord>> entry : ranks.entrySet())
    {
      if (entry.getValue().contains(thfr))
      {
        return String.valueOf(entry.getKey());
      }
    }
    return "UNRANKED";
  }
  
  private String getWins(TeamHasFormatRecord thfr){
    int total = 0;
    return String.valueOf(total);
  }
  
  private String getLosses(TeamHasFormatRecord thfr){
    int total = 0;
    return String.valueOf(total);
  }
  
  private String getDraws(TeamHasFormatRecord thfr){
    int total = 0;
    return String.valueOf(total);
  }

  private String getTeam(TeamHasFormatRecord thfr)
  {
    return thfr.getTeam().getName();
  }

  @Override
  public void updateView()
  {
    //Update rankings based on format
    Format f = format.getValue();
    ranks.clear();
    List<TeamHasFormatRecord> items = new ArrayList<>();
    if (f != null)
    {
      // Get all the rankings for this format.
      ranks = RankingManager.getRankings(f);
      ranks.values().forEach(list -> items.addAll(list));
    }
    else
    {
      if (game.getValue() != null)
      {
        ranks = RankingManager.getRankings(game.getValue().getName());
        ranks.values().forEach(list -> items.addAll(list));
      }
    }
    rankings.setItems(items);
  }
}
