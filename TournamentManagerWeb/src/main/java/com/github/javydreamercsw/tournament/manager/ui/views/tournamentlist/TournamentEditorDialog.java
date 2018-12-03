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
package com.github.javydreamercsw.tournament.manager.ui.views.tournamentlist;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * A dialog for editing {@link Format} objects.
 */
public class TournamentEditorDialog extends AbstractEditorDialog<Tournament>
{
  private static final long serialVersionUID = 1075907611022458493L;
  private final TextField tournamentNameField = new TextField("Name");
  private final TextField winPoints = new TextField("Points per win");
  private final TextField lossPoints = new TextField("Points per loss");
  private final TextField drawPoints = new TextField("Points per draw");
  private final ComboBox<TournamentFormat> format = new ComboBox<>("Format");

  public TournamentEditorDialog(BiConsumer<Tournament, Operation> itemSaver,
          Consumer<Tournament> itemDeleter)
  {
    super("tournament", itemSaver, itemDeleter);

    addNameField();
    addNameWinPoints();
    addNameLossPoints();
    addNameDrawPoints();
    addTournamentFormat();
  }

  @Override
  protected void confirmDelete()
  {
    doDelete(getCurrentItem());
  }

  private void addNameField()
  {
    getFormLayout().add(tournamentNameField);

    getBinder().forField(tournamentNameField)
            .withConverter(String::trim, String::trim)
            .withValidator(new StringLengthValidator(
                    "Tournament name must contain at least 3 printable characters",
                    3, null))
            .withValidator(name ->
            {
              List<Tournament> results
                      = TournamentService.getInstance().findTournaments(name);
              return results.isEmpty()
                      || (results.size() == 1
                      && getCurrentItem().getTournamentPK()
                              .equals(results.get(0).getTournamentPK()));
            },
                    "Tournament name must be unique")
            .bind(Tournament::getName, Tournament::setName);
  }

  private void addNameWinPoints()
  {
    getFormLayout().add(winPoints);

    winPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(winPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getWinPoints, Tournament::setWinPoints);
  }

  private void addNameLossPoints()
  {
    getFormLayout().add(lossPoints);

    lossPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(lossPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getLossPoints, Tournament::setLossPoints);
  }

  private void addNameDrawPoints()
  {
    getFormLayout().add(drawPoints);

    drawPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(drawPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getDrawPoints, Tournament::setDrawPoints);
  }

  private void addTournamentFormat()
  {
    getFormLayout().add(format);

    List<TournamentFormat> formats = new ArrayList<>();
    formats.addAll(TournamentService.getInstance().getFormats());

    format.setDataProvider(new ListDataProvider(formats));
    format.setItemLabelGenerator(new TournamentFormatLabelGenerator());
    format.setRequired(true);
    format.setPreventInvalidInput(true);
    format.setAllowCustomValue(false);
    format.addValueChangeListener(listener -> validate());

    format.setEnabled(formats.size() > 1);
    if (formats.size() == 1)
    {
      // Select the only option
      format.setValue(formats.get(0));
    }

    getBinder().forField(format)
            .bind(Tournament::getTournamentFormat,
                    Tournament::setTournamentFormat);
  }

  @Override
  protected boolean isValid()
  {
    if (getCurrentItem().getTournamentFormat() == null)
    {
      // No format
      return false;
    }
    if (getCurrentItem().getWinPoints() <= getCurrentItem().getLossPoints()
            || getCurrentItem().getWinPoints() <= getCurrentItem().getDrawPoints())
    {
      // Win points are less than loss and/or draw.
      return false;
    }

    // All are zero
    return getCurrentItem().getDrawPoints() + getCurrentItem().getLossPoints()
            + getCurrentItem().getWinPoints() > 0;
  }
}
