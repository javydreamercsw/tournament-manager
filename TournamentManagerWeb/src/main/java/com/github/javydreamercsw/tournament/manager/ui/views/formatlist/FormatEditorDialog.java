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
package com.github.javydreamercsw.tournament.manager.ui.views.formatlist;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.database.storage.db.Format;
import com.github.javydreamercsw.database.storage.db.server.FormatService;
import com.github.javydreamercsw.database.storage.db.server.MatchService;
import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.validator.StringLengthValidator;

/**
 * A dialog for editing {@link Format} objects.
 */
public class FormatEditorDialog extends AbstractEditorDialog<Format> {
  private static final long serialVersionUID = 2349638969280300323L;

    private final TextField formatNameField = new TextField("Name");
    private final TextArea formatDescField = new TextArea("Description");

    public FormatEditorDialog(BiConsumer<Format, Operation> itemSaver,
            Consumer<Format> itemDeleter) {
        super("format", itemSaver, itemDeleter);

        addNameField();
        addDescriptionField();
    }

    private void addNameField() {
        getFormLayout().add(formatNameField);

        getBinder().forField(formatNameField)
                .withConverter(String::trim, String::trim)
                .withValidator(new StringLengthValidator(
                        "Format name must contain at least 3 printable characters",
                        3, null))
                .withValidator(name -> FormatService.getInstance()
                        .findFormats(name).isEmpty(),
                        "Format name must be unique")
                .bind(Format::getName, Format::setName);
    }
    
    private void addDescriptionField() {
        getFormLayout().add(formatDescField);

        getBinder().forField(formatDescField)
                .withConverter(String::trim, String::trim)
                .bind(Format::getDescription, Format::setDescription);
    }

    @Override
    protected void confirmDelete() {
        int reviewCount = MatchService.getInstance()
                .findMatchesWithFormat(getCurrentItem().getName()).size();
        if (reviewCount > 0) {
            openConfirmationDialog("Delete format",
                    "Are you sure you want to delete the “"
                            + getCurrentItem().getName()
                            + "” format?",
                    "Deleting the format will mark the associated matches as “undefined”. "
                            + "You can edit individual matches to select another format.");
        } else {
            doDelete(getCurrentItem());
        }
    }
}
