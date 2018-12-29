package com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist;

import java.util.ArrayList;
import java.util.List;

import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentPK;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.data.provider.ListDataProvider;

public class TournamentManager extends Dialog
{
  private static final long serialVersionUID = -9151481749063166946L;
  private final TournamentPK id;
  private final Label title;
  private final ComboBox<Integer> cb = new ComboBox<>("Round");

  public TournamentManager(Tournament tournament)
  {
    this.id = tournament.getTournamentPK();
    setSizeFull();
    setCloseOnEsc(true);
    setCloseOnOutsideClick(false);
    title = new Label("Tournament: " + tournament.getName());
    add(title);
    cb.addValueChangeListener(listener -> update());
    add(cb);

    NativeButton closeButton = new NativeButton("Close", event ->
    {
      close();
    });
    add(closeButton);
  }

  private void update()
  {
    Tournament t = TournamentService.getInstance().findTournament(id);
    List<Integer> rounds = new ArrayList<>();
    for (int i = 1; i <= t.getRoundList().size(); i++)
    {
      rounds.add(i);
    }
    cb.setDataProvider(new ListDataProvider<>(rounds));
    cb.setValue(t.getRoundList().size());
  }
}
