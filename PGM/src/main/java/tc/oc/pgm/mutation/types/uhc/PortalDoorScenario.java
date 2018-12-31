package tc.oc.pgm.mutation.types.uhc;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tc.oc.commons.bukkit.util.BlockUtils;
import tc.oc.commons.bukkit.util.WorldBorderUtils;
import tc.oc.commons.core.formatting.PeriodFormats;
import tc.oc.commons.core.util.Comparables;
import tc.oc.pgm.events.WorldBorderChangeEvent;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.MatchScope;
import tc.oc.pgm.mutation.Mutation;
import tc.oc.pgm.mutation.types.UHCMutation;
import tc.oc.pgm.worldborder.WorldBorderMatchModule;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class PortalDoorScenario extends UHCMutation.Impl {

    private final static int BANNED_RADIUS = 1;
    private final static int FLOWING_RADIUS = 3;
    private final static Duration DOOR_COOLDOWN = Duration.ofSeconds(10);
    private final static int DOOR_DAMAGE_COUNT = 4;
    private final static int DAMAGE = 1;

    private List<Material> doors;
    private List<Material> fallingBlocks;
    private List<Material> bannedBlocks;

    private Map<Location, UUID> placedDoors;

    private Map<UUID, Duration> cooldowns;
    private Map<UUID, Integer> doorCount;

    public PortalDoorScenario(Match match, Mutation mutation) {
        super(match, mutation);
        addDoors();
    }

    private void addDoors() {
        doors = new ArrayList<>();
        doors.add(Material.ACACIA_DOOR);
        doors.add(Material.DARK_OAK_DOOR);
        doors.add(Material.BIRCH_DOOR);
        doors.add(Material.IRON_DOOR);
        doors.add(Material.JUNGLE_DOOR);
        doors.add(Material.SPRUCE_DOOR);
        doors.add(Material.WOOD_DOOR);
        doors.add(Material.WOODEN_DOOR);

        fallingBlocks = new ArrayList<>();
        fallingBlocks.add(Material.ANVIL);
        fallingBlocks.add(Material.SAND);
        fallingBlocks.add(Material.GRAVEL);
        fallingBlocks.add(Material.CONCRETE_POWDER);
        fallingBlocks.add(Material.OBSIDIAN);
        fallingBlocks.add(Material.DRAGON_EGG);

        bannedBlocks = new ArrayList<>();
        bannedBlocks.add(Material.LAVA);
        bannedBlocks.add(Material.STATIONARY_LAVA);
        bannedBlocks.add(Material.WATER);
        bannedBlocks.add(Material.STATIONARY_WATER);
        bannedBlocks.add(Material.OBSIDIAN);

        placedDoors = new HashMap<>();

        cooldowns = new HashMap<>();

        doorCount = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(BlockPlaceEvent event) {
        MatchPlayer player = match().getPlayer(event.getPlayer());
        if (placedDoors.size() > 0) {
            for (int x = -BANNED_RADIUS; x <= BANNED_RADIUS; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -BANNED_RADIUS; z <= BANNED_RADIUS; z++) {
                        if (x == 0 && z == 0) {
                            continue;
                        }
                        Location newLocation = event.getBlock().getLocation().add(x, y, z);
                        if (doors.contains(newLocation.getBlock().getType())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
        if (!doors.contains(event.getBlock().getType())) {

            return;
        }
        for (int x = -BANNED_RADIUS; x <= BANNED_RADIUS; x++) {
            int y = 0;
            for (int z = -BANNED_RADIUS; z <= BANNED_RADIUS; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }
                Location newLocation = event.getBlock().getLocation().add(x, y, z);
                if (doors.contains(newLocation.getBlock().getType()) || bannedBlocks.contains(newLocation.getBlock().getType())) {
                    if (player != null) {
                        player.sendMessage(message("mutation.type.portaldoor.notsafe"));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }

        double airCount = 0;
        double count = 0;

        for (int x = -2; x <= 2; x++) {
            for (int y = -2; y <= -1; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location newLocation = event.getBlock().getLocation().add(x, y, z);
                    if (newLocation.getBlock().getType().equals(Material.AIR)) {
                        airCount++;
                    }
                    count++;
                }
            }
        }

        for (int x = -1; x <= 1; x++) {
            int y = -1;
            for (int z = -1; z <= 1; z++) {
                Location newLocation = event.getBlock().getLocation().add(x, y, z);
                if (newLocation.getBlock().getType().equals(Material.AIR)) {
                    if (player != null) {
                        player.sendMessage(message("mutation.type.portaldoor.notsafe"));
                    }
                    event.setCancelled(true);
                    return;
                }
            }
        }

        if (airCount/count >= 0.25) {
            if (player != null) {
                player.sendMessage(message("mutation.type.portaldoor.notsafe"));
            }
            event.setCancelled(true);
            return;
        }

        WorldBorderMatchModule borderMatchModule = match().getMatchModule(WorldBorderMatchModule.class);
        if (borderMatchModule != null && borderMatchModule.getAppliedBorder() != null) {
            if (!WorldBorderUtils.isInsideBorder(borderMatchModule.getAppliedBorder().getCenter(), borderMatchModule.getAppliedBorder().getSize(), event.getBlock().getLocation())) {
                if (player != null) {
                    player.sendMessage(message("mutation.type.portaldoor.notsafe"));
                }
                event.setCancelled(true);
                return;
            }
        }

        if (!placedDoors.containsKey(event.getBlock().getLocation())) {
            Location location = event.getBlock().getLocation();

            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    Location below = location.clone().add(x, -1, z);
                    if (fallingBlocks.contains(below.getBlock().getType())) {
                        if (player != null) {
                            player.sendMessage(message("mutation.type.portaldoor.notsafe"));
                        }
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            //height limit
            if (location.getY() >= 128) {
                event.setCancelled(true);
                return;
            }

            UUID uuid = event.getPlayer().getUniqueId();
            if (doorCount.containsKey(uuid) && doorCount.get(uuid) >= DOOR_DAMAGE_COUNT) {
                if (player != null) {
                    player.sendMessage(message("mutation.type.portaldoor.limit"));
                }
                event.setCancelled(true);
                return;
//                damage(event.getPlayer(), event.getPlayer().getHealth() - DAMAGE);
            }

            placedDoors.put(location, uuid);

            doorCount.put(uuid, doorCount.containsKey(uuid) ? doorCount.get(uuid) + 1 : 1);

            if (player != null) {
                player.sendMessage(message("mutation.type.portaldoor.created"));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerBucketEmptyEvent event) {
        if (placedDoors.size() > 0) {
            for (int x = -FLOWING_RADIUS; x <= FLOWING_RADIUS; x++) {
                for (int y = -FLOWING_RADIUS; y <= FLOWING_RADIUS; y++) {
                    for (int z = -FLOWING_RADIUS; z <= FLOWING_RADIUS; z++) {
                        Location newLocation = event.getBlockClicked().getLocation().add(x, y, z);
                        if (doors.contains(newLocation.getBlock().getType())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(BlockFromToEvent event) {
        Material type = event.getBlock().getType();
        if (placedDoors.size() > 0 && bannedBlocks.contains(type)) {
            for (int x = -FLOWING_RADIUS; x <= FLOWING_RADIUS; x++) {
                for (int y = -FLOWING_RADIUS; y <= FLOWING_RADIUS; y++) {
                    for (int z = -FLOWING_RADIUS; z <= FLOWING_RADIUS; z++) {
                        Location newLocation = event.getToBlock().getLocation().add(x, y, z);
                        if (doors.contains(newLocation.getBlock().getType())) {
                            event.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event) {
        if (doors.contains(event.getBlock().getType())) {
            return;
        }
        for (int x = -2; x <= 2; x++) {
            for (int y = 1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Location newLocation = event.getBlock().getLocation().add(x, y, z);
                    if (doors.contains(newLocation.getBlock().getType())) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDoorBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        Location above = location.clone().add(0, 1, 0);

        if (doors.contains(above.getBlock().getType()) && !event.getBlock().getType().equals(Material.WOODEN_DOOR)) {
            event.setCancelled(true);
            return;
        }

        List<Location> locations = new ArrayList<>();
        locations.add(event.getBlock().getLocation());
        locations.add(event.getBlock().getLocation().clone().add(0, 1, 0));
        locations.add(event.getBlock().getLocation().subtract(0, 1, 0));
        for (Location badDoor : locations) {
            if (placedDoors.containsKey(badDoor)) {
                UUID uuid = placedDoors.get(badDoor);
                doorCount.put(uuid, doorCount.get(uuid) - 1);
                placedDoors.remove(badDoor);
                MatchPlayer player = match().getPlayer(uuid);
                if (player != null) {
                    player.sendMessage(message("mutation.type.portaldoor.removed", ChatColor.RED));
                }
            }
            placedDoors.remove(location);
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(PlayerMoveEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        MatchPlayer player = match().getPlayer(event.getPlayer());
        if (placedDoors.size() > 1 && inDoor(event.getTo()) && !inDoor(event.getFrom())) {
            if (!cooldowns.containsKey(uuid)) {
                if (teleport(event.getActor(), event.getTo())) {
                    if (player != null && player.isObserving()) {
                        return;
                    }
                    cooldowns.put(uuid, match().runningTime());
                }
                return;
            }

            Duration cooldown = cooldowns.get(event.getPlayer().getUniqueId());
            Duration currentTime = match().runningTime();
            Duration difference = currentTime.minus(cooldown);
            if (Comparables.greaterOrEqual(difference, DOOR_COOLDOWN)) {
                if (teleport(event.getActor(), event.getTo())) {
                    if (player != null && player.isObserving()) {
                        return;
                    }
                    cooldowns.put(uuid, match().runningTime());
                }
            } else {
                if (player != null) {
                    player.sendMessage(message("mutation.type.portaldoor.cooldown", ChatColor.RED, PeriodFormats.formatColons(DOOR_COOLDOWN.minus(difference))));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerMove(WorldBorderChangeEvent event) {
        cleanDoors();
    }

    private void cleanDoors() {
        List<Location> badDoors = new ArrayList<>();
        for (Location location : placedDoors.keySet()) {
            WorldBorderMatchModule borderMatchModule = match().getMatchModule(WorldBorderMatchModule.class);
            if (borderMatchModule != null && borderMatchModule.getAppliedBorder() != null) {
                if (!WorldBorderUtils.isInsideBorder(borderMatchModule.getAppliedBorder().getCenter(), borderMatchModule.getAppliedBorder().getSize(), location)) {
                    badDoors.add(location);
                }
            }
        }

        for (Location badDoor : badDoors) {
            UUID uuid = placedDoors.get(badDoor);
            doorCount.put(uuid, doorCount.get(uuid) - 1);
            placedDoors.remove(badDoor);
            MatchPlayer player = match().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(message("mutation.type.portaldoor.removed", ChatColor.RED));
            }
        }
    }

    private boolean teleport(Player player, Location to) {
        cleanDoors();
        int tries = 100;
        List<Location> doors = new ArrayList<>(placedDoors.keySet());

        while (tries > 0) {
            tries--;

            Random random = new Random();

            Location location = doors.get(random.nextInt(doors.size()));
            if (location.getBlockX() == to.getBlockX() &&
                    location.getBlockY() == to.getBlockY() &&
                    location.getBlockZ() == to.getBlockZ()) {
                continue;
            }

            WorldBorderMatchModule borderMatchModule = match().getMatchModule(WorldBorderMatchModule.class);
            if (borderMatchModule != null && borderMatchModule.getAppliedBorder() != null) {
                if (!WorldBorderUtils.isInsideBorder(borderMatchModule.getAppliedBorder().getCenter(), borderMatchModule.getAppliedBorder().getSize(), location)) {
                    continue;
                }
            }

            player.teleport(BlockUtils.base(location));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 3, 1));
            float kb = player.getKnockbackReduction();
            player.setKnockbackReduction(1);
            match().getScheduler(MatchScope.RUNNING).createDelayedTask(Duration.ofSeconds(3), () -> player.setKnockbackReduction(kb));
            return true;
        }
        return false;
    }

    private boolean inDoor(Location location) {
        return placedDoors.keySet().stream().anyMatch(doorLocation -> location.getBlockX() == doorLocation.getBlockX() &&
                location.getBlockY() == doorLocation.getBlockY() &&
                location.getBlockZ() == doorLocation.getBlockZ());
    }
}
