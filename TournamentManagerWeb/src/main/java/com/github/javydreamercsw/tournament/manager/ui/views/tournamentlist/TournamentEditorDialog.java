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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.github.javydreamercsw.tournament.manager.ui.CustomDateTimePicker;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.github.javydreamercsw.tournament.manager.ui.common.TournamentFormatLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * A dialog for editing {@link Format} objects.
 */
public class TournamentEditorDialog extends AbstractEditorDialog<Tournament>
{
  private static final long serialVersionUID = 1075907611022458493L;
  private final TextField tournamentNameField = new TextField("Name");
  private final ComboBox<Float> winPoints = new ComboBox<>("Points per win");
  private final ComboBox<Float> lossPoints = new ComboBox<>("Points per loss");
  private final ComboBox<Float> drawPoints = new ComboBox<>("Points per draw");
  private final ComboBox<TournamentFormat> format = new ComboBox<>("Format");
  private final CustomDateTimePicker signupDate = new CustomDateTimePicker();

  public TournamentEditorDialog(BiConsumer<Tournament, Operation> itemSaver,
          Consumer<Tournament> itemDeleter)
  {
    super("tournament", itemSaver, itemDeleter);

    addNameField();
    addNameWinPoints();
    addNameLossPoints();
    addNameDrawPoints();
    addTournamentFormat();
    addSettings();
  }

  @Override
  protected void confirmDelete()
  {
    doDelete(getCurrentItem());
  }

  private void addSettings()
  {
    List<Integer> minutes = new ArrayList<>();
    for (int i = 0; i <= 60; i++)
    {
      minutes.add(i);
    }

    LocalDateTime now = LocalDateTime.now();

    signupDate.setMin(now.toLocalDate());
    signupDate.setLabel("Signups Start Date");
    if (getCurrentItem() != null)
    {
      signupDate.setValue(getCurrentItem().getSignupDate() == null ? null
              : getCurrentItem().getSignupDate().toLocalDate());
    }
    getFormLayout().add(signupDate);
    signupDate.addValueChangeListener(event ->
    {
      validate();
    });
    getBinder().forField(signupDate)
            .bind(Tournament::getSignupDate, Tournament::setSignupDate);

    ComboBox<Integer> signupLength = new ComboBox<>();
    signupLength.setLabel("Signup time limit (Minutes)");
    signupLength.setDataProvider(new ListDataProvider<>(minutes));

    getFormLayout().add(signupLength);
    getBinder().forField(signupLength)
            .bind(Tournament::getSignupTimeLimit, Tournament::setSignupTimeLimit);

    ComboBox<Integer> roundLength = new ComboBox<>();
    roundLength.setLabel("Round time limit (Minutes)");
    roundLength.setDataProvider(new ListDataProvider<>(minutes));

    getFormLayout().add(roundLength);
    getBinder().forField(roundLength)
            .bind(Tournament::getRoundTimeLimit, Tournament::setRoundTimeLimit);
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
              boolean valid = true;
              for (Tournament t
                      : TournamentService.getInstance().findTournaments(name))
              {
                if (t.getName().equals(name)
                        && !t.getTournamentPK()
                                .equals(getCurrentItem().getTournamentPK()))
                {
                  // Same name with different id.
                  valid = false;
                  break;
                }
              }
              return valid;
            }, "Tournament name must be unique")
            .bind(Tournament::getName, Tournament::setName);
  }

  private void addNameWinPoints()
  {
    getFormLayout().add(winPoints);

    winPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(winPoints)
            .bind(Tournament::getWinPoints, Tournament::setWinPoints);
  }

  private void addNameLossPoints()
  {
    getFormLayout().add(lossPoints);

    lossPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(lossPoints)
            .bind(Tournament::getLossPoints, Tournament::setLossPoints);
  }

  private void addNameDrawPoints()
  {
    getFormLayout().add(drawPoints);

    drawPoints.addValueChangeListener(listener -> validate());

    getBinder().forField(drawPoints)
            .bind(Tournament::getDrawPoints, Tournament::setDrawPoints);
  }

  private void addTournamentFormat()
  {
    getFormLayout().add(format);

    List<TournamentFormat> formats = new ArrayList<>();
    formats.addAll(TournamentService.getInstance().getFormats());

    format.setDataProvider(new ListDataProvider<>(formats));
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
    if (tournamentNameField.getValue() == null || 
            tournamentNameField.getValue().isBlank())
    {
      System.out.println("Invalid name: " + tournamentNameField.getValue());
      // No name
      return false;
    }
    if (format.getValue() == null)
    {
      System.out.println("Missing format");
      // No format
      return false;
    }
    if (winPoints.getValue() <= lossPoints.getValue()
            || winPoints.getValue() <= drawPoints.getValue())
    {
      System.out.println("Invalid points");
      // Win points are less than loss and/or draw.
      return false;
    }

//    if (getCurrentItem().getNoShowTimeLimit() == null
//            || (getCurrentItem().getNoShowTimeLimit().getHour()
//            + getCurrentItem().getNoShowTimeLimit().getMinute() == 0))
//    {
//      // Must be valid
//      return false;
//    }
    // All are zero
    return drawPoints.getValue() + lossPoints.getValue()
            + winPoints.getValue() > 0;
  }
}
