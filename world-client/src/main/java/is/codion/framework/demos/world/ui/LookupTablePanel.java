/*
 * This file is part of Codion World Demo.
 *
 * Codion World Demo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Codion World Demo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Codion World Demo.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2023, Björn Darri Sigurðsson.
 */
package is.codion.framework.demos.world.ui;

import is.codion.common.state.State;
import is.codion.framework.demos.world.model.LookupTableModel;
import is.codion.swing.common.ui.component.button.ButtonBuilder;
import is.codion.swing.common.ui.component.button.ToggleButtonType;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.control.ToggleControl;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.ui.EntityTablePanel;
import is.codion.swing.framework.ui.icon.FrameworkIcons;

import org.kordamp.ikonli.foundation.Foundation;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static is.codion.swing.common.ui.component.Components.scrollPane;
import static is.codion.swing.common.ui.component.Components.toolBar;
import static javax.swing.BorderFactory.createTitledBorder;

final class LookupTablePanel extends EntityTablePanel {

  private final JScrollPane columnSelectionPanel = scrollPane(createColumnSelectionToolBar())
          .verticalUnitIncrement(16)
          .build();
  private final State columnSelectionPaneVisibleState = State.state(true);

  LookupTablePanel(SwingEntityTableModel lookupModel) {
    super(lookupModel);
    columnSelectionPaneVisibleState.addDataListener(this::setColumnSelectionPanelVisible);
    table().setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    setConditionPanelVisible(true);
    setShowRefreshProgressBar(true);
    setupControls();
  }

  @Override
  protected Controls createPopupMenuControls(List<Controls> additionalPopupMenuControls) {
    return super.createPopupMenuControls(additionalPopupMenuControls)
            .addSeparatorAt(2)
            .addAt(3, Control.builder(this::exportCSV)
                    .name("Export CSV...")
                    .smallIcon(FrameworkIcons.instance().icon(Foundation.PAGE_EXPORT_CSV))
                    .build())
            .addSeparatorAt(6)
            .addAt(7, ToggleControl.builder(columnSelectionPaneVisibleState)
                    .name("Select columns")
                    .build());
  }

  @Override
  protected void layoutPanel(JPanel tablePanel, JPanel southPanel) {
    super.layoutPanel(tablePanel, southPanel);
    add(columnSelectionPanel, BorderLayout.EAST);
  }

  private void exportCSV() throws IOException {
    File fileToSave = Dialogs.fileSelectionDialog()
            .owner(this)
            .selectFileToSave("export.csv");
    LookupTableModel lookupTableModel = tableModel();
    Dialogs.progressWorkerDialog(() -> lookupTableModel.exportCSV(fileToSave))
            .owner(this)
            .title("Exporting data")
            .onResult("Export successful")
            .onException("Export failed")
            .execute();
  }

  private JToolBar createColumnSelectionToolBar() {
    Controls toggleColumnsControls = table().createToggleColumnsControls();

    return toolBar(Controls.controls()
            .add(createSelectAllColumnsControl(toggleColumnsControls))
            .addSeparator()
            .addAll(toggleColumnsControls))
            .orientation(SwingConstants.VERTICAL)
            .toggleButtonType(ToggleButtonType.CHECKBOX)
            .buttonBuilder(ButtonBuilder.builder().includeText(true))
            .border(createTitledBorder("Columns"))
            .build();
  }

  private void setColumnSelectionPanelVisible(boolean visible) {
    columnSelectionPanel.setVisible(visible);
    revalidate();
  }

  private void setupControls() {
    setControl(ControlCode.CLEAR, Control.builder(this::clearTableAndConditions)
            .name("Clear")
            .mnemonic('C')
            .smallIcon(FrameworkIcons.instance().clear())
            .build());
    //Get rid of the default column menu
    setControl(ControlCode.SELECT_COLUMNS, null);
    setControl(ControlCode.RESET_COLUMNS, null);
  }

  private void clearTableAndConditions() {
    tableModel().clear();
    tableModel().conditionModel().clear();
  }

  private static Control createSelectAllColumnsControl(Controls toggleColumnsControls) {
    return Control.builder(() -> toggleColumnsControls.actions().stream()
                    .map(ToggleControl.class::cast)
                    .forEach(toggleControl -> toggleControl.value().set(true)))
            .name("Select all")
            .smallIcon(FrameworkIcons.instance().icon(Foundation.CHECK))
            .build();
  }
}
