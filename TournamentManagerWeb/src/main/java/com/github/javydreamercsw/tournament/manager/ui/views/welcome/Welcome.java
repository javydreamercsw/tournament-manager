package com.github.javydreamercsw.tournament.manager.ui.views.welcome;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.openide.util.Exceptions;
import org.openide.util.Lookup;

import com.github.javydreamercsw.database.storage.db.server.DataBaseManager;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.tournament.manager.api.IGame;
import com.github.javydreamercsw.tournament.manager.ui.MainLayout;
import com.github.javydreamercsw.tournament.manager.ui.views.TMView;
import com.vaadin.flow.component.HasValue.ValueChangeEvent;
import com.vaadin.flow.component.HasValue.ValueChangeListener;
import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Welcome")
public class Welcome extends TMView
{
  private static final long serialVersionUID = 1252548231807630022L;

  static
  {
    try
    {
      InitialContext context = new InitialContext();

      String JNDIDB = (String) context
              .lookup("java:comp/env/tm/JNDIDB");

      DataBaseManager.setPersistenceUnitName(JNDIDB);
      boolean demo = (Boolean) context
              .lookup("java:comp/env/tm/demo");

      // Check if it's configured for demo
      if (demo && PlayerService.getInstance().getAll().isEmpty())
      {
        try
        {
          Notification.show(
                  "Loading demo data...",
                  3000, Position.MIDDLE);

          DataBaseManager.loadDemoData();

          Notification.show(
                  "Loading demo data done!",
                  3000, Position.MIDDLE);
        }
        catch (Exception ex)
        {
          Notification.show(
                  "Error loading demo data!",
                  3000, Position.MIDDLE);
          Exceptions.printStackTrace(ex);
        }
      }
      else
      {
        try
        {
          DataBaseManager.load();
        }
        catch (Exception ex)
        {
          Notification.show(
                  "Error loading demo data!",
                  3000, Position.MIDDLE);
          Exceptions.printStackTrace(ex);
        }
        Notification.show(
                "Loading data done!",
                3000, Position.MIDDLE);
      }
    }
    catch (NamingException ex)
    {
      Notification.show(
              "Error loading demo data!",
              3000, Position.MIDDLE);
      Exceptions.printStackTrace(ex);
    }
  }

  public Welcome()
  {
    addClassName("welcome-list");
    setDefaultHorizontalComponentAlignment(Alignment.STRETCH);
    TextArea mda = new TextArea("Hello world !");
    List<IGame> games = new ArrayList<>();
    games.addAll(Lookup.getDefault().lookupAll(IGame.class));
    ComboBox<IGame> cb = new ComboBox<>();
    cb.setLabel("Select a Game: ");
    cb.setDataProvider(new ListDataProvider<>(games));
    cb.setItemLabelGenerator(new GameLabelGenerator());
    cb.addValueChangeListener(new ValueChangeListener()
    {
      private static final long serialVersionUID = 5377566605252849942L;

      @Override
      public void valueChanged(ValueChangeEvent e)
      {
        IGame gameAPI = cb.getValue();
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
