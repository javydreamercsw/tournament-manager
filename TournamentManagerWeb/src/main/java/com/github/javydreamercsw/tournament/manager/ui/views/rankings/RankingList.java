package com.github.javydreamercsw.tournament.manager.ui.views.rankings;

import static com.github.javydreamercsw.tournament.manager.ui.views.TMView.CURRENT_GAME;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.TeamHasFormatRecord;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.RankingManager;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.github.javydreamercsw.tournament.manager.ui.common.FormatLabelGenerator;
import com.github.javydreamercsw.tournament.manager.ui.views.TMView;
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
  private final ComboBox<Format> format = new ComboBox<>("Format");
  private final Grid<TeamHasFormatRecord> rankings = new Grid<>();
  private Map<Integer, List<TeamHasFormatRecord>> ranks = new HashMap<>();

  public RankingList()
  {
    VerticalLayout container = new VerticalLayout();
    // Add the top menu
    HorizontalLayout header = new HorizontalLayout();

    // Add the format selector
    List<Format> formats = FormatService.getInstance().findFormatByGame(
            (String) VaadinService.getCurrentRequest().getWrappedSession()
                    .getAttribute(CURRENT_GAME));

    format.setDataProvider(new ListDataProvider<>(formats));
    format.setItemLabelGenerator(new FormatLabelGenerator());
    format.setRequired(true);
    format.setPreventInvalidInput(true);
    format.setAllowCustomValue(false);
    format.addValueChangeListener(event -> updateView());
    format.setValue(formats.get(0));

    header.add(format);

    rankings.addColumn(this::getRowIndex).setWidth("2em")
            .setResizable(true);
    rankings.addColumn(this::getTeam).setHeader("Team").setWidth("4em")
            .setResizable(true);
    rankings.addColumn(TeamHasFormatRecord::getPoints).setHeader("Points")
            .setWidth("4em");
    rankings.addColumn(TeamHasFormatRecord::getMean).setHeader("Mean")
            .setWidth("6em");
    rankings.addColumn(TeamHasFormatRecord::getStandardDeviation)
            .setHeader("Standard Deviation")
            .setWidth("6em");
    rankings.setSelectionMode(SelectionMode.NONE);

    container.add(header, rankings);
    add(container);
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
    return "TBD";
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
    if (f != null)
    {
      // Get all the rankings for this format.
      List<TeamHasFormatRecord> items = new ArrayList<>();
      ranks = RankingManager.getRankings(f);
      ranks.values().forEach(list->items.addAll(list));
      rankings.setItems(items);
    }
  }
}
