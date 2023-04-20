package is.codion.framework.demos.world.ui;

import is.codion.common.model.CancelException;
import is.codion.common.model.table.ColumnConditionModel;
import is.codion.common.model.table.ColumnConditionModel.AutomaticWildcard;
import is.codion.common.user.User;
import is.codion.framework.demos.world.domain.api.World.Continent;
import is.codion.framework.demos.world.domain.api.World.Lookup;
import is.codion.framework.demos.world.model.CountryModel;
import is.codion.framework.demos.world.model.WorldAppModel;
import is.codion.swing.common.ui.laf.LookAndFeelSelectionPanel;
import is.codion.swing.framework.model.SwingEntityModel;
import is.codion.swing.framework.ui.EntityApplicationPanel;
import is.codion.swing.framework.ui.EntityPanel;
import is.codion.swing.framework.ui.EntityTableCellRenderer;
import is.codion.swing.framework.ui.ReferentialIntegrityErrorHandling;
import is.codion.swing.framework.ui.icons.FrameworkIcons;

import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import org.kordamp.ikonli.foundation.Foundation;

import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static is.codion.swing.common.ui.laf.LookAndFeelProvider.addLookAndFeelProvider;
import static is.codion.swing.common.ui.laf.LookAndFeelProvider.lookAndFeelProvider;
import static is.codion.swing.framework.ui.EntityApplicationBuilder.entityApplicationBuilder;
import static java.util.Arrays.asList;

public final class WorldAppPanel extends EntityApplicationPanel<WorldAppModel> {

  private static final String DEFAULT_FLAT_LOOK_AND_FEEL = "com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme";

  public WorldAppPanel(WorldAppModel appModel) {
    super(appModel);
  }

  @Override
  protected List<EntityPanel> createEntityPanels() {
    CountryModel countryModel = applicationModel().entityModel(CountryModel.class);
    CountryPanel countryPanel = new CountryPanel(countryModel);

    SwingEntityModel continentModel = applicationModel().entityModel(Continent.TYPE);
    ContinentPanel continentPanel = new ContinentPanel(continentModel);

    SwingEntityModel lookupModel = applicationModel().entityModel(Lookup.TYPE);
    EntityPanel lookupPanel = new EntityPanel(lookupModel,
            new LookupTablePanel(lookupModel.tableModel()));

    return asList(countryPanel, continentPanel, lookupPanel);
  }

  public static void main(String[] args) throws CancelException {
    Locale.setDefault(new Locale("en", "EN"));
    Arrays.stream(FlatAllIJThemes.INFOS).forEach(themeInfo ->
            addLookAndFeelProvider(lookAndFeelProvider(themeInfo.getClassName())));
    LookAndFeelSelectionPanel.CHANGE_ON_SELECTION.set(true);
    ColumnConditionModel.AUTOMATIC_WILDCARD.set(AutomaticWildcard.PREFIX_AND_POSTFIX);
    ColumnConditionModel.CASE_SENSITIVE.set(false);
    EntityPanel.TOOLBAR_BUTTONS.set(true);
    EntityTableCellRenderer.NUMERICAL_HORIZONTAL_ALIGNMENT.set(SwingConstants.CENTER);
    ReferentialIntegrityErrorHandling.REFERENTIAL_INTEGRITY_ERROR_HANDLING.set(ReferentialIntegrityErrorHandling.DISPLAY_DEPENDENCIES);
    FrameworkIcons.instance().addIcon(Foundation.MAP);
    FrameworkIcons.instance().addIcon(Foundation.PAGE_EXPORT_CSV);
    SwingUtilities.invokeLater(() -> entityApplicationBuilder(WorldAppModel.class, WorldAppPanel.class)
            .applicationName("World")
            .domainClassName("is.codion.framework.demos.world.domain.WorldImpl")
            .applicationVersion(WorldAppModel.VERSION)
            .defaultLookAndFeelClassName(DEFAULT_FLAT_LOOK_AND_FEEL)
            .frameSize(new Dimension(1280, 720))
            .defaultLoginUser(User.parse("scott:tiger"))
            .start());
  }
}
