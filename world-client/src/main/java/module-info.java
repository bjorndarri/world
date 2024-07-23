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
/**
 * Client.
 */
module is.codion.framework.demos.world.client {
	requires is.codion.framework.json.domain;
	requires is.codion.swing.framework.ui;
	requires is.codion.plugin.jasperreports;
	requires is.codion.framework.demos.world.domain.api;
	requires com.formdev.flatlaf.intellijthemes;
	requires org.kordamp.ikonli.foundation;
	requires org.jfree.jfreechart;
	requires jasperreports;
	requires jasperreports.pdf;
	requires jasperreports.fonts;
	requires org.apache.commons.logging;
	requires com.github.librepdf.openpdf;
	requires org.jxmapviewer.jxmapviewer2;
	requires org.json;

	exports is.codion.framework.demos.world.ui
					to is.codion.swing.framework.ui;

	//for loading reports from classpath
	opens is.codion.framework.demos.world.model;
}