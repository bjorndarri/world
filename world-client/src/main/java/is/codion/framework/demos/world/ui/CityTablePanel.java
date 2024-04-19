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

import is.codion.framework.demos.world.domain.api.World.City;
import is.codion.framework.demos.world.model.CityTableModel;
import is.codion.framework.demos.world.model.CityTableModel.PopulateLocationTask;
import is.codion.swing.common.ui.control.Control;
import is.codion.swing.common.ui.control.Controls;
import is.codion.swing.common.ui.dialog.Dialogs;
import is.codion.swing.framework.ui.icon.FrameworkIcons;

import org.kordamp.ikonli.foundation.Foundation;

import java.util.List;

final class CityTablePanel extends ChartTablePanel {

	CityTablePanel(CityTableModel tableModel) {
		super(tableModel, tableModel.chartDataset(), "Cities", config ->
						config.editable(attributes ->
										attributes.remove(City.LOCATION)));
	}

	@Override
	protected Controls createPopupMenuControls(List<Controls> additionalPopupMenuControls) {
		return super.createPopupMenuControls(additionalPopupMenuControls)
						.addAt(0, createPopulateLocationControl())
						.addSeparatorAt(1);
	}

	private Control createPopulateLocationControl() {
		CityTableModel cityTableModel = tableModel();

		return Control.builder(this::populateLocation)
						.name("Populate location")
						.enabled(cityTableModel.citiesWithoutLocationSelected())
						.smallIcon(FrameworkIcons.instance().icon(Foundation.MAP))
						.build();
	}

	private void populateLocation() {
		CityTableModel tableModel = tableModel();
		PopulateLocationTask task = tableModel.populateLocationTask();

		Dialogs.progressWorkerDialog(task)
						.owner(this)
						.title("Populating locations")
						.maximumProgress(task.maximumProgress())
						.stringPainted(true)
						.controls(Controls.builder()
										.control(Control.builder(task::cancel)
														.name("Cancel")
														.enabled(task.notCancelled()))
										.build())
						.onException(this::displayPopulateException)
						.execute();
	}

	private void displayPopulateException(Exception exception) {
		Dialogs.exceptionDialog()
						.owner(this)
						.title("Unable to populate location")
						.show(exception);
	}
}
