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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.github.javydreamercsw.tournament.manager.web.backend.TournamentService;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.data.validator.StringLengthValidator;

import net.sourceforge.javydreamercsw.database.storage.db.Format;
import net.sourceforge.javydreamercsw.database.storage.db.Tournament;

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

  public TournamentEditorDialog(BiConsumer<Tournament, Operation> itemSaver,
          Consumer<Tournament> itemDeleter)
  {
    super("tournament", itemSaver, itemDeleter);

    addNameField();
    addNameWinPoints();
    addNameLossPoints();
    addNameDrawPoints();
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
            .withValidator(name -> TournamentService.getInstance()
            .findTournaments(name).isEmpty(),
                    "Tournament name must be unique")
            .bind(Tournament::getName, Tournament::setName);
  }

  private void addNameWinPoints()
  {
    getFormLayout().add(winPoints);

    getBinder().forField(winPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getWinPoints, Tournament::setWinPoints);
  }

  private void addNameLossPoints()
  {
   getFormLayout().add(lossPoints);

    getBinder().forField(lossPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getLossPoints, Tournament::setLossPoints);
  }

  private void addNameDrawPoints()
  {
    getFormLayout().add(drawPoints);

    getBinder().forField(drawPoints)
            .withConverter(
                    new StringToIntegerConverter("Must enter a number"))
            .bind(Tournament::getDrawPoints, Tournament::setDrawPoints);
  }
}
