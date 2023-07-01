package is.codion.framework.demos.world.model;

import is.codion.common.value.Value;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.world.domain.api.World.City;
import is.codion.framework.domain.entity.Entity;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.model.SwingEntityTableModel;

public final class CountryModel extends SwingEntityModel {

  private final Value<Double> averageCityPopulationValue = Value.value();

  CountryModel(EntityConnectionProvider connectionProvider) {
    super(new CountryTableModel(connectionProvider));
    SwingEntityModel cityModel = new SwingEntityModel(new CityTableModel(connectionProvider));
    SwingEntityModel countryLanguageModel = new SwingEntityModel(new CountryLanguageTableModel(connectionProvider));
    addDetailModels(cityModel, countryLanguageModel);

    cityModel.tableModel().refresher().addRefreshListener(() ->
            averageCityPopulationValue.set(averageCityPopulation()));
    CountryEditModel countryEditModel = editModel();
    countryEditModel.setAverageCityPopulationObserver(averageCityPopulationValue.observer());
  }

  private Double averageCityPopulation() {
    if (editModel().isEntityNew()) {
      return null;
    }

    SwingEntityTableModel cityTableModel = detailModel(City.TYPE).tableModel();
    Entity country = editModel().entity();

    return Entity.castTo(City.class, cityTableModel.items()).stream()
            .filter(city -> city.isInCountry(country))
            .mapToInt(City::population)
            .average()
            .orElse(0d);
  }
}
