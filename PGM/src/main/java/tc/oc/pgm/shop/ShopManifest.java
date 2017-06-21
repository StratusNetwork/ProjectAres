package tc.oc.pgm.shop;

import com.google.inject.assistedinject.FactoryModuleBuilder;
import tc.oc.commons.bukkit.listeners.ButtonManager;
import tc.oc.commons.bukkit.listeners.WindowManager;
import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.commons.core.plugin.PluginFacetBinder;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.match.inject.MatchBinders;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.shops.BlockShop;
import tc.oc.pgm.xml.parser.EnumParserManifest;

public class ShopManifest extends HybridManifest implements MatchBinders, MapBinders {
    @Override
    protected void configure() {
        install(new EnumParserManifest<Purchasable.Type>(){});
        rootParsers().addBinding().to(ShopParser.class);

        final PluginFacetBinder facets = new PluginFacetBinder(binder());
        facets.register(ButtonManager.class);
        facets.register(WindowManager.class);

        install(new FactoryModuleBuilder()
                .build(ShopInterface.Factory.class));
        install(new FactoryModuleBuilder()
                .build(BlockShop.Factory.class));
    }
}
