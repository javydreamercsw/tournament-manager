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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.Tournament;
import com.github.javydreamercsw.database.storage.db.TournamentFormat;
import com.github.javydreamercsw.database.storage.db.server.TournamentService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.github.javydreamercsw.tournament.manager.ui.common.TournamentFormatLabelGenerator;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Result;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.converter.Converter;
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
  private final DatePicker signups = new DatePicker();
  private final ComboBox<Integer> dateHour = new ComboBox<>();
  private final ComboBox<Integer> dateMin = new ComboBox<>();

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
    for (int i = 1; i <= 60; i++)
    {
      minutes.add(i);
    }

    LocalDateTime now = LocalDateTime.now();

    dateHour.setPlaceholder("Hour");
    dateHour.setDataProvider(new ListDataProvider<>(minutes.subList(0, 24)));
    if (getCurrentItem() != null)
    {
      dateHour.setValue(getCurrentItem().getSignupDate() == null ? null
              : getCurrentItem().getSignupDate().getHour());
    }
    dateHour.addValueChangeListener(event ->
    {
      updateSignupDate();
    });

    dateMin.setPlaceholder("Minutes");
    dateMin.setDataProvider(new ListDataProvider<>(minutes));
    if (getCurrentItem() != null)
    {
      dateMin.setValue(getCurrentItem().getSignupDate() == null ? null
              : getCurrentItem().getSignupDate().getMinute());
    }
    dateMin.addValueChangeListener(event ->
    {
      updateSignupDate();
    });

    signups.setMin(now.toLocalDate());
    signups.setPlaceholder("Date signups open.");
    if (getCurrentItem() != null)
    {
      signups.setValue(getCurrentItem().getSignupDate() == null ? null
              : getCurrentItem().getSignupDate().toLocalDate());
    }
    getFormLayout().add(signups);
    getFormLayout().add(dateHour);
    getFormLayout().add(dateMin);
    signups.addValueChangeListener(event ->
    {
      updateSignupDate();
    });
    getBinder().forField(signups)
            .withConverter(new Converter<LocalDate, LocalDateTime>()
            {
              private static final long serialVersionUID = -4231752474899375998L;

              @Override
              public Result<LocalDateTime> convertToModel(LocalDate value, ValueContext context)
              {
                LocalTime lt
                        = LocalTime.of(dateHour.getValue() == null ? 0
                                : dateHour.getValue(),
                                dateMin.getValue() == null ? 0
                                : dateMin.getValue());
                return Result.ok(LocalDateTime.of(value, lt));
              }

              @Override
              public LocalDate convertToPresentation(LocalDateTime value, ValueContext context)
              {
                if (value != null)
                {
                  dateHour.setValue(value.getHour());
                  dateMin.setValue(value.getMinute());
                }
                return value == null ? null : value.toLocalDate();
              }
            })
            .bind(Tournament::getSignupDate, Tournament::setSignupDate);

    ComboBox<Integer> signupLength = new ComboBox<>();
    signupLength.setPlaceholder("Signup time limit.");
    signupLength.setDataProvider(new ListDataProvider<>(minutes));

    getFormLayout().add(signupLength);
    getBinder().forField(signupLength)
            .bind(Tournament::getSignupTimeLimit, Tournament::setSignupTimeLimit);

    ComboBox<Integer> roundLength = new ComboBox<>();
    roundLength.setPlaceholder("Round time limit.");
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

//    if (getCurrentItem().getNoShowTimeLimit() == null
//            || (getCurrentItem().getNoShowTimeLimit().getHour()
//            + getCurrentItem().getNoShowTimeLimit().getMinute() == 0))
//    {
//      // Must be valid
//      return false;
//    }
    // All are zero
    return getCurrentItem().getDrawPoints() + getCurrentItem().getLossPoints()
            + getCurrentItem().getWinPoints() > 0;
  }

  private void updateSignupDate()
  {
//    if (signups.getValue() != null
//            && dateHour.getValue() != null
//            && dateMin.getValue() != null)
//    {
//      LocalDateTime ldt = LocalDateTime.of(signups.getValue().getYear(),
//              signups.getValue().getMonthValue(),
//              signups.getValue().getDayOfMonth(),
//              dateHour.getValue(), dateMin.getValue());
//      System.out.println("New Date: " + ldt);
//      getCurrentItem().setSignupDate(ldt);
//    }
  }
}
