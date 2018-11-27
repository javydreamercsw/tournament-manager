package com.github.javydreamercsw.tournament.manager.ui.views.welcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.vaadin.maxime.MarkdownArea;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Game;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.GameService;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.github.javydreamercsw.tournament.manager.ui.views.TMView;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Tournament List")
public class Welcome extends TMView
{
  private static final long serialVersionUID = 1252548231807630022L;

  public Welcome()
  {
    addClassName("welcome-list");
    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    MarkdownArea mda = new MarkdownArea("Hello world !");
    List<IGame> games = new ArrayList<>();
    games.addAll(Lookup.getDefault().lookupAll(IGame.class));
    ComboBox<IGame> cb = new ComboBox<>();
    cb.setLabel("Select a Game: ");
    cb.setDataProvider(new ListDataProvider(games));
    cb.setItemLabelGenerator(new GameLabelGenerator());
    cb.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 5377566605252849942L;

      @Override
      public void valueChanged(ValueChangeEvent e)
      {
        IGame gameAPI = cb.getValue();
        // Update everything to the new game.

        // Add game to DB if not there
        Optional<Game> result
                = GameService.getInstance().findGameByName(gameAPI.getName());

        Game game;
        if (result.isPresent())
        {
          game = result.get();
        }
        else
        {
          game = new Game(gameAPI.getName());
          GameService.getInstance().saveGame(game);
        }

        //Load formats
        gameAPI.gameFormats().forEach(format ->
        {
          // Check if it exists in the databse
          Optional<Format> f
                  = FormatService.getInstance()
                          .findFormatForGame(gameAPI.getName(), format.getName());
          if (!f.isPresent())
          {
            try
            {
              // Let's create it.
              Format newFormat = new Format();
              newFormat.setName(format.getName());
              newFormat.setDescription(format.getDescription());
              newFormat.setGame(game);
              FormatService.getInstance().saveFormat(newFormat);
            }
            catch (Exception ex)
            {
              Exceptions.printStackTrace(ex);
            }
          }
        });
        saveValue(CURRENT_GAME, gameAPI.getName());
      }
    });
    cb.setEnabled(games.size() > 1);
    if (games.size() == 1)
    {
      // Select the only option
      cb.setValue(games.get(0));
    }

    add(mda);
    add(cb);
  }

  @Override
  public void updateView()
  {
    // Nothing to do
  }

  private class GameLabelGenerator implements ItemLabelGenerator<IGame>
  {
    private static final long serialVersionUID = -4396467477758231860L;

    @Override
    public String apply(IGame g)
    {
      return g.getName();
    }
  }
}
