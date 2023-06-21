package is.codion.framework.demos.world.model;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.event.Event;
import is.codion.common.event.EventDataListener;
import is.codion.common.state.State;
import is.codion.common.state.StateObserver;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.world.domain.api.World.City;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.exception.ValidationException;
import is.codion.swing.common.model.worker.ProgressWorker.ProgressReporter;
import is.codion.swing.framework.model.SwingEntityTableModel;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import static java.util.Collections.singletonList;

public final class CityTableModel extends SwingEntityTableModel {

  private final DefaultPieDataset<String> chartDataset = new DefaultPieDataset<>();
  private final Event<Collection<Entity>> displayLocationEvent = Event.event();
  private final State citiesWithoutLocationSelectedState = State.state();

  CityTableModel(EntityConnectionProvider connectionProvider) {
    super(new CityEditModel(connectionProvider));
    selectionModel().addSelectedItemsListener(displayLocationEvent::onEvent);
    selectionModel().addSelectionListener(this::updateCitiesWithoutLocationSelected);
    refresher().addRefreshListener(this::refreshChartDataset);
  }

  public PieDataset<String> chartDataset() {
    return chartDataset;
  }

  public void addDisplayLocationListener(EventDataListener<Collection<Entity>> listener) {
    displayLocationEvent.addDataListener(listener);
  }

  public StateObserver citiesWithoutLocationSelectedObserver() {
    return citiesWithoutLocationSelectedState.observer();
  }

  public void fetchLocationForSelected(ProgressReporter<String> progressReporter,
                                       StateObserver cancelFetchLocationObserver)
          throws IOException, DatabaseException, ValidationException {
    Collection<Entity> updatedCities = new ArrayList<>();
    Collection<City> selectedCitiesWithoutLocation = selectionModel().getSelectedItems().stream()
            .filter(city -> city.isNull(City.LOCATION))
            .map(city -> city.castTo(City.class))
            .toList();
    CityEditModel editModel = editModel();
    Iterator<City> citiesWithoutLocation = selectedCitiesWithoutLocation.iterator();
    while (citiesWithoutLocation.hasNext() && !cancelFetchLocationObserver.get()) {
      City city = citiesWithoutLocation.next();
      progressReporter.publish(city.country().name() + " - " + city.name());
      editModel.setLocation(city);
      updatedCities.add(city);
      progressReporter.setProgress(100 * updatedCities.size() / selectedCitiesWithoutLocation.size());
      displayLocationEvent.onEvent(singletonList(city));
    }
    displayLocationEvent.onEvent(selectionModel().getSelectedItems());
  }

  private void refreshChartDataset() {
    chartDataset.clear();
    Entity.castTo(City.class, visibleItems()).forEach(city ->
            chartDataset.setValue(city.name(), city.population()));
  }

  private void updateCitiesWithoutLocationSelected() {
    citiesWithoutLocationSelectedState.set(selectionModel().getSelectedItems().stream()
            .anyMatch(city -> city.isNull(City.LOCATION)));
  }
}