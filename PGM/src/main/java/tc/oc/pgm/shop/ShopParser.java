package tc.oc.pgm.shop;

import com.google.api.client.util.Sets;
import org.jdom2.Document;
import org.jdom2.Element;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.filters.matcher.StaticFilter;
import tc.oc.pgm.filters.parser.FilterParser;
import tc.oc.pgm.kits.ItemParser;
import tc.oc.pgm.kits.KitParser;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.map.MapRootParser;
import tc.oc.pgm.shop.currency.Currency;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.purchasable.PurchasableKit;
import tc.oc.pgm.shop.purchasable.PurchasableSet;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;
import tc.oc.pgm.xml.parser.PrimitiveParser;

import javax.inject.Inject;
import java.util.Set;

public class ShopParser implements MapRootParser {

    private final Document document;
    private final MapModuleContext context;
    private final PrimitiveParser<Shop.Type> typeParser;
    private final PrimitiveParser<Double> doubleParser;
    private final FilterParser filterParser;
    private final KitParser kitParser;
    private final ItemParser itemParser;

    @Inject
    public ShopParser(Document document,
                      MapModuleContext context,
                      PrimitiveParser<Shop.Type> typeParser,
                      PrimitiveParser<Double> doubleParser,
                      FilterParser filterParser,
                      KitParser kitParser,
                      ItemParser itemParser) {
        this.document = document;
        this.context = context;
        this.typeParser = typeParser;
        this.doubleParser = doubleParser;
        this.filterParser = filterParser;
        this.kitParser = kitParser;
        this.itemParser = itemParser;
    }

    @Override
    public void parse() throws InvalidXMLException {
        Element shopsRoot = document.getRootElement().getChild("purchase");
        if (shopsRoot == null)
            return;

        if (shopsRoot.getChild("item-sets") == null)
            throw new InvalidXMLException("No item sets are defined.", shopsRoot);
        if (shopsRoot.getChild("currencies") == null)
            throw new InvalidXMLException("No currencies are defined.", shopsRoot);
        if (shopsRoot.getChild("shops") == null)
            throw new InvalidXMLException("No shops are defined.", shopsRoot);

        for (Element itemSet : shopsRoot.getChild("item-sets").getChildren()) {
            parseItemSet(itemSet);
        }
        for (Element currency : shopsRoot.getChild("currencies").getChildren()) {
            parseCurrency(currency);
        }
        for (Element shop : shopsRoot.getChild("shops").getChildren()) {
            parseShop(shop);
        }
    }

    private PurchasableSet parseItemSet(Element el) throws InvalidXMLException {
        Set<Purchasable> purchasables = Sets.newHashSet();
        for (Element element : el.getChildren()) {
            purchasables.add(new PurchasableKit(XMLUtils.parseMaterialData(Node.fromAttr(element, "icon")),
                    doubleParser.parse(Node.fromAttr(element, "price")),
                    context.features().reference(Node.fromAttr(element, "currency"), Currency.class),
                    kitParser.parse(new Node(element))));
        }
    }

    private Currency parseCurrency(Element el) throws InvalidXMLException {

    }

    private Shop parseShop(Element el) throws InvalidXMLException {
        final Node node = new Node(el);

        final Shop.Type type = typeParser.parse(node, el.getAttributeValue("type"));

        final String title = el.getAttributeValue("title", "Shop");

        final Filter openFilter = filterParser.property(el, "open-filter").optional(StaticFilter.ALLOW);
        final Filter globalPurchaseFilter = filterParser.property(el, "purchase-filter").optional(null);

        final boolean multiUse = XMLUtils.parseBoolean(el.getAttribute("multi-use"), true);

        PurchasableSet items = context.features().reference(node, el.getAttributeValue("item-set"), PurchasableSet.class);


    }
}
