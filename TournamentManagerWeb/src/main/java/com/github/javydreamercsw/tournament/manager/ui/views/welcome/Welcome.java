package com.github.javydreamercsw.tournament.manager.ui.views.welcome;

import java.util.ArrayList;
import java.util.List;

import org.openide.util.Lookup;
import org.vaadin.maxime.MarkdownArea;

import com.github.javydreamercsw.tournament.manager.api.Game;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Tournament List")
public class Welcome extends VerticalLayout
{
  private static final long serialVersionUID = 1252548231807630022L;

  public Welcome()
  {
    addClassName("welcome-list");
    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    MarkdownArea mda = new MarkdownArea("Hello world !");
    List<Game> games = new ArrayList<>();
    games.addAll(Lookup.getDefault().lookupAll(Game.class));
    ComboBox cb = new ComboBox();
    cb.setLabel("Select a Game: ");
    cb.setDataProvider(new ListDataProvider(games));
    cb.setItemLabelGenerator(new GameLabelGenerator());
    
    add(mda);
    add(cb);
  }
  
  private class GameLabelGenerator implements ItemLabelGenerator<Game>{
    private static final long serialVersionUID = -4396467477758231860L;
    @Override
    public String apply(Game g)
    {
      return g.getName();
    }
  }
}
