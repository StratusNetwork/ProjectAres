package tc.oc.pgm.shop;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.match.inject.MatchBinders;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.xml.parser.EnumParserManifest;

public class ShopManifest extends HybridManifest implements MatchBinders, MapBinders {
    @Override
    protected void configure() {
        install(new EnumParserManifest<Purchasable.Type>(){});
        rootParsers().addBinding().to(ShopParser.class);
        matchListener(SimplePurchaseTracker.class);
    }
}
