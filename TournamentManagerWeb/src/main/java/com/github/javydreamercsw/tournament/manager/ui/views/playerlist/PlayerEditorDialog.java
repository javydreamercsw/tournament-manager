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
package com.github.javydreamercsw.tournament.manager.ui.views.playerlist;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Player;
import com.github.javydreamercsw.database.storage.db.server.PlayerService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * A dialog for editing {@link Format} objects.
 */
public class PlayerEditorDialog extends AbstractEditorDialog<Player>
{
  private static final long serialVersionUID = 4724212789545035906L;

  private final TextField formatNameField = new TextField("Name");

  public PlayerEditorDialog(BiConsumer<Player, Operation> itemSaver,
          Consumer<Player> itemDeleter)
  {
    super("player", itemSaver, itemDeleter);

    addNameField();
  }

  private void addNameField()
  {
    getFormLayout().add(formatNameField);

    getBinder().forField(formatNameField)
            .withConverter(String::trim, String::trim)
            .withValidator(new StringLengthValidator(
                    "Player name must contain at least 3 printable characters",
                    3, null))
            .withValidator(name -> PlayerService.getInstance()
            .findPlayers(name).isEmpty(),
                    "Player name must be unique")
            .bind(Player::getName, Player::setName);
  }

  @Override
  protected void confirmDelete()
  {
    if (getCurrentItem().getRecordList().size() > 0
            || getCurrentItem().getTeamList().size() > 0)
    {
      openConfirmationDialog("Delete player",
              "Are you sure you want to delete the “"
              + getCurrentItem().getName()
              + "” player?",
              "You will lose all it's data.");
    }
    else
    {
      doDelete(getCurrentItem());
    }
  }
}
