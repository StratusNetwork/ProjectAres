package tc.oc.pgm.mutation;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.Material;
import tc.oc.commons.core.chat.Component;
import tc.oc.pgm.PGM;
import tc.oc.pgm.mutation.types.MutationModule;
import tc.oc.pgm.mutation.types.kit.*;
import tc.oc.pgm.mutation.types.other.BlitzMutation;
import tc.oc.pgm.mutation.types.other.RageMutation;
import tc.oc.pgm.mutation.types.targetable.ApocalypseMutation;
import tc.oc.pgm.mutation.types.targetable.BomberMutation;
import tc.oc.pgm.mutation.types.targetable.LightningMutation;
import tc.oc.pgm.mutation.types.uhc.BloodDiamondsScenario;
import tc.oc.pgm.mutation.types.uhc.BloodLapisScenario;
import tc.oc.pgm.mutation.types.uhc.ChickenScenario;
import tc.oc.pgm.mutation.types.uhc.HasteScenario;
import tc.oc.pgm.mutation.types.uhc.SharingScenario;
import tc.oc.pgm.mutation.types.uhc.CutCleanScenario;
import tc.oc.pgm.mutation.types.uhc.DelayedMobsScenario;
import tc.oc.pgm.mutation.types.uhc.FlowerPowerScenario;
import tc.oc.pgm.mutation.types.uhc.GoneFishingScenario;
import tc.oc.pgm.mutation.types.uhc.MoblessScenario;
import tc.oc.pgm.mutation.types.uhc.PortalDoorScenario;
import tc.oc.pgm.mutation.types.uhc.NeophobiaScenario;
import tc.oc.pgm.mutation.types.uhc.RodlessScenario;
import tc.oc.pgm.mutation.types.uhc.SkyHighScenario;
import tc.oc.pgm.mutation.types.uhc.TimberScenario;
import tc.oc.pgm.mutation.types.uhc.TimeBombScenario;
import tc.oc.pgm.mutation.types.uhc.WeakestLinkScenario;
import tc.oc.pgm.mutation.types.uhc.EnchantlessScenario;
import tc.oc.pgm.mutation.types.uhc.BarebonesScenario;

import java.util.stream.Stream;

public enum Mutation {

//    BLITZ         (BlitzMutation.class,         Material.IRON_FENCE, false),
//    RAGE          (RageMutation.class,          Material.SKULL_ITEM, false),
//    HARDCORE      (HardcoreMutation.class,      Material.GOLDEN_APPLE),
//    JUMP          (JumpMutation.class,          Material.FEATHER),
//    EXPLOSIVE     (ExplosiveMutation.class,     Material.FLINT_AND_STEEL),
//    ELYTRA        (ElytraMutation.class,        Material.ELYTRA),
//    PROJECTILE    (ProjectileMutation.class,    Material.TIPPED_ARROW),
//    ENCHANTMENT   (EnchantmentMutation.class,   Material.ENCHANTMENT_TABLE),
//    POTION        (PotionMutation.class,        Material.POTION),
//    EQUESTRIAN    (EquestrianMutation.class,    Material.SADDLE),
//    HEALTH        (HealthMutation.class,        Material.COOKED_BEEF),
//    GLOW          (GlowMutation.class,          Material.GLOWSTONE_DUST, false),
//    STEALTH       (StealthMutation.class,       Material.THIN_GLASS),
//    ARMOR         (ArmorMutation.class,         Material.DIAMOND_CHESTPLATE),
//    MOBS          (MobsMutation.class,          Material.MONSTER_EGG),
//    LIGHTNING     (LightningMutation.class,     Material.JACK_O_LANTERN),
//    BOMBER        (BomberMutation.class,        Material.TNT),
//    BREAD         (BreadMutation.class,         Material.BREAD),
//    BOAT          (BoatMutation.class,          Material.BOAT, false),
//    TOOLS         (ToolsMutation.class,         Material.DIAMOND_PICKAXE),
//    APOCALYPSE    (ApocalypseMutation.class,    Material.NETHER_STAR),
    CUTCLEAN      (CutCleanScenario.class,      Material.IRON_INGOT, false, true),
    SKYHIGH       (SkyHighScenario.class,       Material.EYE_OF_ENDER, false, true),
    TIMEBOMB      (TimeBombScenario.class,      Material.CHEST, false, true),
    RODLESS       (RodlessScenario.class,       Material.FISHING_ROD, false, true),
    NEOPHOBIA     (NeophobiaScenario.class,     Material.WORKBENCH, false, true),
    PORTALDOOR    (PortalDoorScenario.class,    Material.WOOD_DOOR, false, true),
//    INSOMNIA      (InsomniaScenario.class,      Material.BED, false, true),
    MOBLESS       (MoblessScenario.class,       Material.MONSTER_EGG, false, true),
    TIMBER        (TimberScenario.class,        Material.WOOD_AXE, false, true),
    FLOWERPOWER   (FlowerPowerScenario.class,   Material.RED_ROSE, false, true),
    BLOODDIAMONDS (BloodDiamondsScenario.class, Material.DIAMOND_ORE, false, true),
    BLOODLAPIS    (BloodLapisScenario.class,    Material.LAPIS_ORE, false, true),
    GONEFISHING   (GoneFishingScenario.class,   Material.FISHING_ROD, false, true),
    DELAYEDMOBS   (DelayedMobsScenario.class,   Material.WATCH, false, true),
    SHARING       (SharingScenario.class,       Material.STONE_HOE, false, true),
    WEAKESTLINK   (WeakestLinkScenario.class,   Material.WEB, false, true),
    CHICKEN       (ChickenScenario.class,       Material.RAW_CHICKEN, false, true),
    HASTE         (HasteScenario.class,         Material.DIAMOND_PICKAXE, false, true),
    ENCHANTLESS   (EnchantlessScenario.class,   Material.ENCHANTMENT_TABLE, false, true),
    BAREBONES     (BarebonesScenario.class,     Material.BONE, false, true);

    public static final String TYPE_KEY = "mutation.type.";
    public static final String DESCRIPTION_KEY = ".desc";
    public static final String BROADCAST_KEY = ".broadcast";

    private final @Nullable Class<? extends MutationModule> loader;
    private final Material guiDisplay;
    private final boolean pollable;
    private final boolean scenario;

    Mutation(@Nullable Class<? extends MutationModule> loader, Material guiDisplay) {
        this(loader, guiDisplay, true, false);
    }

    Mutation(@Nullable Class<? extends MutationModule> loader, Material guiDisplay, boolean pollable) {
        this(loader, guiDisplay, pollable, false);
    }

    Mutation(@Nullable Class<? extends MutationModule> loader, Material guiDisplay, boolean pollable, boolean scenario) {
        this.loader = loader;
        this.guiDisplay = guiDisplay;
        this.pollable = pollable;
        this.scenario = scenario;
    }

    public Class<? extends MutationModule> loader() {
        return loader;
    }

    public Material getGuiDisplay() {
        return guiDisplay;
    }

    public boolean isPollable() {
        return pollable;
    }

    public boolean isScenario() {
        return scenario;
    }

    public String getName() {
        return TYPE_KEY + name().toLowerCase();
    }

    public String getDescription() {
        return getName() + DESCRIPTION_KEY;
    }

    public String getBroadcast() {
        return getName() + BROADCAST_KEY;
    }

    public BaseComponent getComponent(ChatColor color) {
        return new Component(new TranslatableComponent(getName()), color).hoverEvent(HoverEvent.Action.SHOW_TEXT, new Component(new TranslatableComponent(getDescription()), ChatColor.GRAY));
    }

    public static Function<Mutation, BaseComponent> toComponent(final ChatColor color) {
        return mutation -> mutation.getComponent(color);
    }

    public static Stream<Mutation> fromString(final String name) {
        try {
            return Stream.of(Mutation.valueOf(name));
        } catch(IllegalArgumentException iae) {
            PGM.get().getLogger().warning("Unable to find mutation named '" + name + "'");
            return Stream.empty();
        }
    }

}
