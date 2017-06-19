package tc.oc.pgm.shop;

import tc.oc.commons.core.inject.HybridManifest;
import tc.oc.pgm.map.inject.MapBinders;
import tc.oc.pgm.xml.parser.EnumParserManifest;

public class ShopManifest extends HybridManifest implements MapBinders {
    @Override
    protected void configure() {
        install(new EnumParserManifest<Shop.Type>(){});
        rootParsers().addBinding().to(ShopParser.class);
    }
}
