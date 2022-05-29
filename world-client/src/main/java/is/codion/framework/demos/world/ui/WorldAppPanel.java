package is.codion.framework.demos.world.ui;

import is.codion.common.model.CancelException;
import is.codion.common.model.FilteredModel;
import is.codion.common.model.table.ColumnConditionModel;
import is.codion.common.model.table.ColumnConditionModel.AutomaticWildcard;
import is.codion.common.user.User;
import is.codion.framework.db.EntityConnectionProvider;
import is.codion.framework.demos.world.domain.api.World.Continent;
import is.codion.framework.demos.world.domain.api.World.Lookup;
import is.codion.framework.demos.world.model.CountryModel;
import is.codion.framework.demos.world.model.WorldAppModel;
import is.codion.swing.common.ui.laf.LookAndFeelSelectionPanel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityApplicationPanel;
import is.codion.swing.framework.ui.EntityEditPanel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.EntityTableCellRenderer;
import is.codion.swing.framework.ui.ReferentialIntegrityErrorHandling;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static is.codion.swing.common.ui.laf.LookAndFeelProvider.addLookAndFeelProvider;
import static is.codion.swing.common.ui.laf.LookAndFeelProvider.lookAndFeelProvider;
import static java.util.Arrays.asList;

public final class WorldAppPanel extends EntityApplicationPanel<WorldAppModel> {

  private static final String DEFAULT_FLAT_LOOK_AND_FEEL = "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme";

  public WorldAppPanel() {
    super("World");
  }

  @Override
  protected List<EntityPanel> initializeEntityPanels(WorldAppModel applicationModel) {
    CountryModel countryModel = applicationModel.getEntityModel(CountryModel.class);
    CountryPanel countryPanel = new CountryPanel(countryModel);

    SwingEntityModel continentModel = applicationModel.getEntityModel(Continent.TYPE);
    ContinentPanel continentPanel = new ContinentPanel(continentModel);

    SwingEntityModel lookupModel = applicationModel.getEntityModel(Lookup.TYPE);
    EntityPanel lookupPanel = new EntityPanel(lookupModel,
            new LookupTablePanel(lookupModel.getTableModel()));

    return asList(countryPanel, continentPanel, lookupPanel);
  }

  @Override
  protected WorldAppModel initializeApplicationModel(EntityConnectionProvider connectionProvider) {
    return new WorldAppModel(connectionProvider);
  }

  @Override
  protected String getDefaultLookAndFeelName() {
    return DEFAULT_FLAT_LOOK_AND_FEEL;
  }

  public static void main(String[] args) throws CancelException {
    Locale.setDefault(new Locale("en", "EN"));
    Arrays.stream(FlatAllIJThemes.INFOS).forEach(themeInfo ->
            addLookAndFeelProvider(lookAndFeelProvider(themeInfo.getClassName())));
    LookAndFeelSelectionPanel.CHANGE_DURING_SELECTION.set(true);
    ColumnConditionModel.AUTOMATIC_WILDCARD.set(AutomaticWildcard.PREFIX_AND_POSTFIX);
    ColumnConditionModel.CASE_SENSITIVE.set(false);
    EntityPanel.TOOLBAR_BUTTONS.set(true);
    EntityEditPanel.USE_SAVE_CONTROL.set(false);
    FilteredModel.ASYNC_REFRESH.set(true);
    EntityTableCellRenderer.NUMERICAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    ReferentialIntegrityErrorHandling.REFERENTIAL_INTEGRITY_ERROR_HANDLING.set(ReferentialIntegrityErrorHandling.DEPENDENCIES);
    EntityConnectionProvider.CLIENT_DOMAIN_CLASS.set("is.codion.framework.demos.world.domain.WorldImpl");
    SwingUtilities.invokeLater(() -> new WorldAppPanel().starter()
            .frameSize(new Dimension(1280, 720))
            .defaultLoginUser(User.parse("scott:tiger"))
            .start());
  }
}
