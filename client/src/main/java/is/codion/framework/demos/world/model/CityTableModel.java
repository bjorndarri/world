package is.codion.framework.demos.world.model;

import is.codion.common.db.exception.DatabaseException;
import is.codion.common.event.Event;
import is.codion.common.event.EventDataListener;
import is.codion.common.state.StateObserver;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.world.domain.api.World.City;
import is.codion.framework.demos.world.domain.api.World.Country;
import is.codion.framework.domain.entity.Attribute;
import is.codion.framework.domain.entity.Entities;
import is.codion.framework.domain.entity.Entity;
import is.codion.framework.domain.entity.exception.ValidationException;
import is.codion.swing.common.model.worker.ProgressWorker.ProgressReporter;
import is.codion.swing.framework.model.SwingEntityTableModel;
import is.codion.swing.framework.model.SwingEntityTableSortModel;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jxmapviewer.viewer.GeoPosition;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

public final class CityTableModel extends SwingEntityTableModel {

  public static final String OPENSTREETMAP_ORG_SEARCH = "https://nominatim.openstreetmap.org/search/";

  private final DefaultPieDataset<String> chartDataset = new DefaultPieDataset<>();
  private final Event<Collection<Entity>> displayLocationEvent = Event.event();

  CityTableModel(EntityConnectionProvider connectionProvider) {
    super(City.TYPE, connectionProvider, new CityTableSortModel(connectionProvider.getEntities()));
    getSelectionModel().addSelectedItemsListener(displayLocationEvent::onEvent);
    addRefreshSuccessfulListener(this::refreshChartDataset);
  }

  public PieDataset<String> getChartDataset() {
    return chartDataset;
  }

  public void addDisplayLocationListener(EventDataListener<Collection<Entity>> listener) {
    displayLocationEvent.addDataListener(listener);
  }

  public void fetchLocationForSelected(ProgressReporter<String> progressReporter,
                                       StateObserver cancelFetchLocationObserver)
          throws IOException, DatabaseException, ValidationException, URISyntaxException, InterruptedException {
    List<Entity> updatedCities = new ArrayList<>();
    List<Entity> selectedCitiesWithoutLocation = getSelectionModel().getSelectedItems().stream()
            .filter(city -> city.isNull(City.LOCATION))
            .toList();
    for (Entity city : selectedCitiesWithoutLocation) {
      if (!cancelFetchLocationObserver.get()) {
        progressReporter.publish(city.toString());
        fetchLocation(city);
        updatedCities.add(city);
        progressReporter.setProgress(100 * updatedCities.size() / selectedCitiesWithoutLocation.size());
      }
    }
    displayLocationEvent.onEvent(getSelectionModel().getSelectedItems());
  }

  private void fetchLocation(Entity city) throws IOException, DatabaseException, ValidationException,
          URISyntaxException, InterruptedException {
    JSONArray jsonArray = toJSONArray(createHttpRequest(city));
    if (jsonArray.length() > 0) {
      updateLocation(city, (JSONObject) jsonArray.get(0));
    }
  }

  private HttpRequest createHttpRequest(final Entity city) throws URISyntaxException, UnsupportedEncodingException {
    return HttpRequest.newBuilder()
            .uri(createURI(city))
            .GET()
            .build();
  }

  private URI createURI(final Entity city) throws URISyntaxException, UnsupportedEncodingException {
    return new URI(OPENSTREETMAP_ORG_SEARCH +
            URLEncoder.encode(city.get(City.NAME), UTF_8.name()) + "," +
            URLEncoder.encode(city.getForeignKey(City.COUNTRY_FK).get(Country.NAME), UTF_8.name()) + "?format=json");
  }

  private void updateLocation(Entity city, JSONObject cityInformation) throws DatabaseException, ValidationException {
    city.put(City.LOCATION, new GeoPosition(cityInformation.getDouble("lat"), cityInformation.getDouble("lon")));
    getEditModel().update(singletonList(city));
  }

  private static JSONArray toJSONArray(HttpRequest request) throws IOException, InterruptedException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(HttpClient.newBuilder()
            .build()
            .send(request, HttpResponse.BodyHandlers.ofInputStream())
            .body(), UTF_8))) {
      return new JSONArray(reader.lines().collect(joining()));
    }
  }

  private void refreshChartDataset() {
    chartDataset.clear();
    Entity.castTo(City.class, getVisibleItems())
            .forEach(city -> chartDataset.setValue(city.name(), city.population()));
  }

  private static final class CityTableSortModel extends SwingEntityTableSortModel {

    private CityTableSortModel(Entities entities) {
      super(entities);
    }

    @Override
    protected Comparable<?> getComparable(Entity entity, Attribute<?> attribute) {
      if (attribute.equals(City.LOCATION)) {
        return entity.getOptional(attribute)
                .map(Object::toString)
                .orElse(null);
      }

      return super.getComparable(entity, attribute);
    }
  }
}
