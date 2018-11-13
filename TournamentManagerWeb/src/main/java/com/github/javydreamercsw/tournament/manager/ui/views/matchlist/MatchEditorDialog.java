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
package com.github.javydreamercsw.tournament.manager.ui.views.matchlist;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.github.javydreamercsw.tournament.manager.ui.common.AbstractEditorDialog;
import com.github.javydreamercsw.tournament.manager.web.backend.Format;

import net.sourceforge.javydreamercsw.database.storage.db.MatchEntry;

/**
 * A dialog for editing {@link Format} objects.
 */
public class MatchEditorDialog extends AbstractEditorDialog<MatchEntry>
{
  private static final long serialVersionUID = 2349638969280300323L;

  public MatchEditorDialog(BiConsumer<MatchEntry, Operation> itemSaver,
          Consumer<MatchEntry> itemDeleter)
  {
    super("match", itemSaver, itemDeleter);

    //TODO: add fields
  }

  @Override
  protected void confirmDelete()
  {
    doDelete(getCurrentItem());
  }
}
