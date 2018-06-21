package tc.oc.pgm.achievements.rewards;

import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SetMultimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import tc.oc.api.bukkit.users.OnlinePlayers;
import tc.oc.api.docs.PlayerId;
import tc.oc.api.minecraft.users.UserStore;
import tc.oc.commons.bukkit.chat.Audiences;
import tc.oc.commons.bukkit.chat.BukkitSound;
import tc.oc.commons.bukkit.nick.IdentityProvider;
import tc.oc.commons.bukkit.util.PlayerStates;
import tc.oc.commons.core.chat.Audience;
import tc.oc.commons.core.chat.Component;
import tc.oc.commons.core.concurrent.Flexecutor;
import tc.oc.minecraft.scheduler.Sync;
import tc.oc.pgm.match.Match;
import tc.oc.pgm.match.MatchPlayer;
import tc.oc.pgm.match.Repeatable;
import tc.oc.pgm.match.inject.MatchScoped;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton @MatchScoped
public class RewardsManager {
    // Utilities to identify players/handle messages
    private final Server server;
    private final OnlinePlayers onlinePlayers;
    private final Audiences audiences;
    private final IdentityProvider identityProvider;
    private final PlayerStates playerStates;
    private final UserStore userStore;

    private final Match match;
    private final ConsoleCommandSender console;

    private final Flexecutor flexecutor;

    private SetMultimap<Player, ItemStack> delegatedRewards;

    @Inject public RewardsManager(Server server,
                                  OnlinePlayers onlinePlayers,
                                  Audiences audiences,
                                  IdentityProvider identityProvider,
                                  PlayerStates playerStates,
                                  UserStore userStore,
                                  Match match,
                                  ConsoleCommandSender console,
                                  @Sync Flexecutor flexecutor) {
        this.server = server;
        this.onlinePlayers = onlinePlayers;
        this.audiences = audiences;
        this.identityProvider = identityProvider;
        this.playerStates = playerStates;
        this.userStore = userStore;
        this.match = match;
        this.console = console;
        this.flexecutor = flexecutor;

        this.delegatedRewards = MultimapBuilder.hashKeys().hashSetValues(64).build();
    }

    // ---------------------
    // ------ Rewards ------
    // ---------------------

    /**
     * For rewarding players with a configured amount of XP(TBD) for completing (an) achievement(s)
     * @return true If player can be awarded
     * @return false If player cannot be awarded, i.e. player not online
     */
    // TODO: Integration with backend
    private boolean giveXPReward(PlayerId playerId, int amount) { return true; }

    /**
     * For rewarding players with a configured amount of currency(TBD) for completing (an) achievement(s)
     * @return true If player can be awarded
     * @return false If player cannot be awarded, i.e. player not online
     */
    // TODO: Integration with backend
    private boolean giveCurrencyReward(PlayerId playerId, int amount) { return true; }

    public boolean giveItemRewards(Player player, Map<Material, Integer> materials) {
        if(!player.isOnline()) return false;
        PlayerInventory playerInventory = player.getInventory();
        // Magic value for when there is no empty space in the inventory
        if(playerInventory.firstEmpty() == -1) return false;
        materials.entrySet().stream().filter(entry -> entry.getValue() <= 64).forEach(itemToGive -> {
            // We store the items that are unable to be given in a map and only award them the items at the earliest convenience
            // This second check is necessary because we need to make sure that there is space for each and every ItemStack that we wish to add
            if(playerInventory.firstEmpty() == -1) {
                // Retrieve the set of items that have been delegated for each player
                Set<ItemStack> delegatedItems = delegatedRewards.get(player);
                // If the item **to be added** is unique(i.e. not present in the Set of delegatedItems),
                // we add it to the set of delegatedItems for the player and update this Set in the
                // SetMultiMap for that player
                delegatedItems.stream().map(ItemStack::getType).filter(addedMaterials -> !delegatedItems.contains(itemToGive.getKey())).forEach(uniqueMaterial -> {
                    delegatedItems.add(new ItemStack(uniqueMaterial, itemToGive.getValue()));
                    delegatedRewards.replaceValues(player, delegatedItems);
                });
                // Now, if the item **to be added** is not unique, we simply update the amount of that ItemStack in the Set of delegatedItems,
                // where the current upper-limit of the amount of an ItemStack to be given is 64
                delegatedItems.stream().map(ItemStack::getType).filter(addedMaterials -> delegatedItems.contains(itemToGive.getKey())).forEach(reoccuringMaterial ->
                    delegatedItems.forEach(storedItemStack -> {
                        int currentStoredAmount;
                        // We retrieve the ItemStack with the reoccuring Material
                        if(storedItemStack.getType() == itemToGive.getKey()) {
                            // Now, we track the stored amount in the aforementioned ItemStack and add it to the amount *to be given*
                            currentStoredAmount = storedItemStack.getAmount() + itemToGive.getValue();
                            // We then remove the old ItemStack and update the amount with due respect to the upper-bound
                            delegatedItems.remove(storedItemStack);
                            delegatedItems.add(currentStoredAmount <= 64 ? new ItemStack(itemToGive.getKey(), currentStoredAmount) : new ItemStack(itemToGive.getKey(), 64));
                    }
                }));
            } else {
                playerInventory.addItem(new ItemStack(itemToGive.getKey(), itemToGive.getValue()));
            }
        });
        return true;
    }

    public boolean giveItemReward(Player player, Material material, int amount) {
        Map<Material, Integer> toBeAdded = new HashMap<>();
        toBeAdded.put(material, amount);
        return giveItemRewards(player, toBeAdded);
    }

    public boolean giveItemReward(Player player, Material material) {
        return giveItemReward(player, material, 1);
    }

    /**
     * For running a command whenever a player completes (an) achievement(s)
     * @return true If player can be awarded
     * @return false If player cannot be awarded, i.e. player not online
     */
    // TODO: Better name?
    public boolean giveCommandReward(Player player, String string) {
        String preparedCommand = string.replace("%player%", player.getName());
        return server.dispatchCommand(console, string);
    }

    // Responsible for updating the delegated list whenever any player's inventory has space
    // We don't really have a clean way to do this other than loop through everyone's inventory
    // and making a check every time
    @Repeatable
    public void updateDelegatedList() {
        if(match.isFinished()) return;

        Set<Map.Entry<Player, Collection<ItemStack>>> entrySet = delegatedRewards.asMap().entrySet();
        entrySet.removeIf(entry -> !entry.getKey().isOnline());

        // Additionally, we clear up the delegatedRewards Collection if there is nothing else to return to each remaining player
        entrySet.removeIf(entry -> entry.getValue().isEmpty());

        // We must not update the player's inventory if the player is dead, else the item will not be given when the player respawns
        match.participants().filter(participant -> playerStates.isDead(participant.getBukkit()))
                            .map(MatchPlayer::getBukkit)
                            .filter(player -> delegatedRewards.containsKey(player))
                            .filter(player -> player.getInventory().firstEmpty() != -1)
                            .forEach(player -> {
                                Set<ItemStack> delegatedItems = delegatedRewards.get(player);
                                // We remove the ItemStack in the delegatedItems Set if we are able to add that ItemStack to the player's inventory
                                delegatedItems.forEach(itemStack -> delegatedItems.removeIf(item -> player.getInventory().addItem(item).isEmpty()));
                                // Finally, we update the delegatedItems Set in delegatedRewards
                                delegatedRewards.replaceValues(player, delegatedItems);
                            });
    }

    public boolean giveReward(PlayerId playerId, Reward reward) {
        switch (reward.getTypes()) {
            case XP:
                return giveXPReward(playerId, reward.getAmount());
            case ITEM:
                return giveItemReward((Player) userStore.get(playerId), reward.getMaterial(), reward.getAmount());
            case DROPLETS:
                return giveCurrencyReward(playerId, reward.getAmount());
        }
        return false;
    }

    // TODO: Localised messages/UI
    private void notEnoughSpace(Player player) {}

    // -------------------------------------------------------
    // ------- Messages/Alerts/Congratulatory messages -------
    // -------------------------------------------------------

    /**
     * Scary-ish message to inform players/receivers if they have not received their reward for whatever reason
     */
    public void alert(Player player, String key, ChatColor color, boolean sound) {
        Audience audience = audiences.get(player);
        audience.sendWarning(new Component(key).color(color), sound);
    }

    // Alerts by default appear green and does not produce any sound
    public void alert(Player player, String key, boolean sound) {
        alert(player, key, ChatColor.GREEN, sound);
    }

    // Alerts by default appear green and does not produce any sound
    public void alert(Player player, String key, ChatColor color) {
        alert(player, key, color, false);
    }

    // Alerts by default appear green and does not produce any sound
    public void alert(Player player, String key) {
        alert(player, key, false);
    }

    /**
     * Messages that are sent when players completed an achievement/received their award
     */
    public void awardMessage(Player player, String key, ChatColor color, boolean sound) {
        Audience audience = audiences.get(player);
        if (sound) audience.playSound(new BukkitSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.25f, 1.25f));
        audience.sendMessage(new Component(key).color(color));
    }

    public void awardMessage(Player player, String key, boolean sound) {
        awardMessage(player, key, ChatColor.GREEN, sound);
    }

    public void awardMessage(Player player, String key, ChatColor color) {
        awardMessage(player, key, color, true);
    }

    public void awardMessage(Player player, String key) {
        alert(player, key, true);
    }
}
