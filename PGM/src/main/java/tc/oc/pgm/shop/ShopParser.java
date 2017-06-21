package tc.oc.pgm.shop;

import com.google.api.client.util.Sets;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jdom2.Document;
import org.jdom2.Element;
import tc.oc.commons.bukkit.localization.BukkitTranslator;
import tc.oc.pgm.filters.Filter;
import tc.oc.pgm.filters.matcher.StaticFilter;
import tc.oc.pgm.filters.parser.FilterParser;
import tc.oc.pgm.kits.ItemParser;
import tc.oc.pgm.kits.KitParser;
import tc.oc.pgm.map.MapModuleContext;
import tc.oc.pgm.map.MapRootParser;
import tc.oc.pgm.shop.currency.Currency;
import tc.oc.pgm.shop.currency.ExperienceCurrency;
import tc.oc.pgm.shop.currency.MaterialCurrency;
import tc.oc.pgm.shop.purchasable.Purchasable;
import tc.oc.pgm.shop.purchasable.PurchasableKit;
import tc.oc.pgm.shop.purchasable.PurchasableSet;
import tc.oc.pgm.shop.shops.BlockShop;
import tc.oc.pgm.utils.XMLUtils;
import tc.oc.pgm.xml.InvalidXMLException;
import tc.oc.pgm.xml.Node;
import tc.oc.pgm.xml.parser.PrimitiveParser;

import javax.inject.Inject;
import java.util.Set;

public class ShopParser implements MapRootParser {

    private final Document document;
    private final MapModuleContext context;
    private final PrimitiveParser<Purchasable.Type> typeParser;
    private final PrimitiveParser<Double> doubleParser;
    private final PrimitiveParser<Integer> integerParser;
    private final FilterParser filterParser;
    private final BukkitTranslator translator;
    private final KitParser kitParser;
    private final ItemParser itemParser;
    private final BlockShop.Factory blockShopFactory;

    private PurchaseTracker tracker;

    @Inject
    public ShopParser(Document document,
                      MapModuleContext context,
                      PrimitiveParser<Purchasable.Type> typeParser,
                      PrimitiveParser<Double> doubleParser,
                      PrimitiveParser<Integer> integerParser,
                      FilterParser filterParser,
                      BukkitTranslator translator,
                      KitParser kitParser,
                      ItemParser itemParser,
                      BlockShop.Factory blockShopFactory) {
        this.document = document;
        this.context = context;
        this.typeParser = typeParser;
        this.doubleParser = doubleParser;
        this.integerParser = integerParser;
        this.filterParser = filterParser;
        this.translator = translator;
        this.kitParser = kitParser;
        this.itemParser = itemParser;
        this.blockShopFactory = blockShopFactory;
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

        for (Element currency : XMLUtils.flattenElements(shopsRoot, "currencies")) {
            context.features().define(currency, parseCurrency(currency));
        }

        for (Element itemSet : XMLUtils.flattenElements(shopsRoot, "item-sets")) {
            context.features().define(itemSet, parseItemSet(itemSet));
        }

        boolean crossTeam = XMLUtils.parseBoolean(shopsRoot.getAttribute("cross-team"), false);
        boolean persistent = XMLUtils.parseBoolean(shopsRoot.getAttribute("persistent"), false);

        this.tracker = new SimplePurchaseTracker(crossTeam, persistent);

        for (Element shop : XMLUtils.flattenElements(shopsRoot, "shops")) {
            context.features().define(shop, parseShop(shop));
        }
    }

    private PurchasableSet parseItemSet(Element el) throws InvalidXMLException {
        Set<Purchasable> purchasables = Sets.newHashSet();
        for (Element element : el.getChildren()) {
            purchasables.add(new PurchasableKit(XMLUtils.parseFormattedText(Node.fromRequiredAttr(element, "name")),
                    XMLUtils.parseFormattedText(element, "description"),
                    XMLUtils.parseMaterial(Node.fromRequiredAttr(element, "icon")),
                    XMLUtils.parseContainerSlot(Node.fromRequiredAttr(element, "slot")),
                    doubleParser.parse(Node.fromRequiredAttr(element, "price")),
                    context.features().reference(Node.fromRequiredAttr(element, "currency"), Currency.class),
                    typeParser.parse(Node.fromRequiredAttr(element, "scope")),
                    XMLUtils.parseBoolean(Node.fromRequiredAttr(element, "incremental"), false),
                    filterParser.property(element, element.getAttribute("purchase-filter").getValue()).optional(new StaticFilter(Filter.QueryResponse.ALLOW)),
                    kitParser.property(element, "kit").required()));
        }
        return new PurchasableSet.Impl(purchasables);
    }

    private Currency parseCurrency(Element el) throws InvalidXMLException {
        Node node = new Node(el);
        switch (el.getName()) {
            case "material":
                ItemStack stack = itemParser.parse(node);
                Node nameSingle = Node.fromAttr(el, "name-singular", "name");
                Node namePlural = Node.fromAttr(el, "name-plural");
                BaseComponent name = new TranslatableComponent(
                        translator.materialKey(stack.getType())
                                .orElseThrow(() -> new InvalidXMLException("No localized name for material " + stack.getType()))
                );
                BaseComponent single = nameSingle == null ? name : new TextComponent(nameSingle.getValue());
                BaseComponent plural = namePlural == null ? name : new TextComponent(namePlural.getValue());
                return new MaterialCurrency(stack, single, plural, doubleParser.parse(Node.fromAttr(el, "value")));
            case "experience":
            case "exp":
                return new ExperienceCurrency();
            default:
                throw new InvalidXMLException("Specified currency type not found.", el);
        }
    }

    private Shop parseShop(Element el) throws InvalidXMLException {
        final Node node = new Node(el);

        final String title = el.getAttributeValue("title", "Shop");

        final int rows = integerParser.parse(Node.fromAttr(el, "rows"));

        final Filter openFilter = filterParser.property(el, "open-filter").optional(StaticFilter.ALLOW);
        final BaseComponent openFailMessage = XMLUtils.parseFormattedText(el, "open-fail", new TranslatableComponent("shop.openFail"));

        final boolean multiUse = XMLUtils.parseBoolean(el.getAttribute("multi-use"), true);

        Set<PurchasableSet> items = Sets.newHashSet();

        for (String s : el.getAttributeValue("item-set").split(",")) {
            items.add(context.features().reference(node, s, PurchasableSet.class));
        }

        switch (el.getName()) {
            case "block":
                Vector location = XMLUtils.parseVector(Node.fromRequiredAttr(el, "location"));
                return blockShopFactory.create(this.tracker, items, title, rows, openFilter, openFailMessage, multiUse, location);
            default:
                throw new InvalidXMLException("Specified shop type not found.", el);
        }
    }
}
