package dev.lbuddyboy.pcore.essential.rollback;

import com.google.gson.JsonObject;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.locator.ItemCache;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.*;

import static org.bukkit.Material.DIRT;

@RequiredArgsConstructor
@Data
public class PlayerDeath {

    private final UUID id, victim, killer;
    private final String deathCause;
    private final long deathTime;
    private final ItemStack[] victimArmor, victimInventory, killerArmor, killerInventory;
    private final double victimFoodLevel, killerFoodLevel, killerHealth;
    private final String victimIpAddress, killerIpAddress;
    private final int victimKills, victimDeaths, killerKills, killerDeaths;

    private UUID resolver = null;
    private List<String> resolveProof = new ArrayList<>();
    private long resolveTime = -1L;

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("id", this.id.toString());
        object.addProperty("victim", this.victim.toString());
        object.addProperty("killer", this.killer == null ? null : this.killer.toString());
        object.addProperty("deathCause", this.deathCause);
        object.addProperty("deathTime", this.deathTime);
        object.addProperty("victimArmor", ItemUtils.itemStackArrayToBase64(this.victimArmor));
        object.addProperty("victimInventory", ItemUtils.itemStackArrayToBase64(this.victimInventory));
        object.addProperty("killerArmor", ItemUtils.itemStackArrayToBase64(this.killerArmor));
        object.addProperty("killerInventory", ItemUtils.itemStackArrayToBase64(this.killerInventory));
        object.addProperty("victimFoodLevel", this.victimFoodLevel);
        object.addProperty("killerFoodLevel", this.killerFoodLevel);
        object.addProperty("killerHealth", this.killerHealth);
        object.addProperty("victimIpAddress", this.victimIpAddress);
        object.addProperty("killerIpAddress", this.killerIpAddress);
        object.addProperty("victimKills", this.victimKills);
        object.addProperty("victimDeaths", this.victimDeaths);
        object.addProperty("killerKills", this.killerKills);
        object.addProperty("killerDeaths", this.killerDeaths);

        if (isResolved()) {
            object.addProperty("resolver", this.resolver.toString());
            object.addProperty("resolveTime", this.resolveTime);
        }

        object.addProperty("resolveProof", GSONUtils.getGSON().toJson(this.resolveProof, GSONUtils.STRING));

        return object;
    }

    public static PlayerDeath deserialize(JsonObject object) {
        PlayerDeath death = new PlayerDeath(
                UUID.fromString(object.get("id").getAsString()),
                UUID.fromString(object.get("victim").getAsString()),
                object.get("killer").isJsonNull() ? null : UUID.fromString(object.get("killer").getAsString()),
                object.get("deathCause").getAsString(),
                object.get("deathTime").getAsLong(),
                ItemUtils.itemStackArrayFromBase64(object.get("victimArmor").getAsString()),
                ItemUtils.itemStackArrayFromBase64(object.get("victimInventory").getAsString()),
                ItemUtils.itemStackArrayFromBase64(object.get("killerArmor").getAsString()),
                ItemUtils.itemStackArrayFromBase64(object.get("killerInventory").getAsString()),
                object.get("victimFoodLevel").getAsDouble(),
                object.get("killerFoodLevel").getAsDouble(),
                object.get("killerHealth").getAsDouble(),
                object.get("victimIpAddress").getAsString(),
                object.get("killerIpAddress").getAsString(),
                object.get("victimKills").getAsInt(),
                object.get("victimDeaths").getAsInt(),
                object.get("killerKills").getAsInt(),
                object.get("killerDeaths").getAsInt()
        );

        if (object.has("resolver")) {
            death.setResolver(UUID.fromString(object.get("resolver").getAsString()));
            death.setResolveTime(object.get("resolveTime").getAsLong());
        }

        death.setResolveProof(GSONUtils.getGSON().fromJson(object.get("resolveProof").getAsString(), GSONUtils.STRING));

        return death;
    }

    public String getDiedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        return sdf.format(new Date(this.deathTime));
    }

    public String getResolveTime() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.setTimeZone(TimeZone.getTimeZone("EST"));
        return sdf.format(new Date(this.resolveTime));
    }

    public boolean isResolved() {
        return this.resolver != null;
    }

    public void performAntiDupe(ItemStack[] items) {
        for (ItemStack content : items) {
            if (content == null || content.getType() == Material.AIR) continue;

            ItemCache itemCache = pCore.getInstance().getLocatorHandler().fetchCache(content);
            if (itemCache == null) continue;
            ItemLocation location = itemCache.getLocation();
            if (location == null) continue;

            if (location.getType() == LocationType.PLAYER_INVENTORY) {
                OfflinePlayer holderOffline = Bukkit.getOfflinePlayer(location.getHolderUUID());

                if (holderOffline.isOnline()) {
                    Player holder = holderOffline.getPlayer();

                    int i = -1;
                    Map<Integer, ItemStack> stacks = new HashMap<>();
                    for (ItemStack stack : holder.getInventory().getContents()) {
                        i++;
                        if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                        NBTItem nbtItemGround = new NBTItem(stack);
                        NBTItem nbtItemContent = new NBTItem(content);

                        if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                            stacks.put(i, stack);
                        }
                    }

                    stacks.keySet().forEach(j -> holder.getInventory().setItem(j, null));
                    continue;
                }

                OfflineData offlineData = pCore.getInstance().getOfflineHandler().fetchCache(holderOffline.getUniqueId());

                if (offlineData == null) continue;

                int i = -1;
                for (ItemStack stack : offlineData.getArmor()) {
                    ++i;
                    if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                    NBTItem nbtItemGround = new NBTItem(stack);
                    NBTItem nbtItemContent = new NBTItem(content);

                    if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                        offlineData.getArmor()[i] = new ItemStack(Material.AIR);
                    }
                }

                i = -1;
                for (ItemStack stack : offlineData.getContents()) {
                    i++;
                    if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                    NBTItem nbtItemGround = new NBTItem(stack);
                    NBTItem nbtItemContent = new NBTItem(content);

                    if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                        offlineData.getContents()[i] = new ItemStack(Material.AIR);
                    }
                }

                pCore.getInstance().getOfflineHandler().updateCache(holderOffline.getUniqueId(), offlineData);
            } else if (location.getType() == LocationType.HOVERING) {
                OfflinePlayer holderOffline = Bukkit.getOfflinePlayer(location.getHolderUUID());

                if (holderOffline.isOnline()) {
                    Player holder = holderOffline.getPlayer();

                    holder.setItemOnCursor(null);
                    holder.closeInventory();
                }

            } else if (location.getType() == LocationType.GROUND_ITEM) {
                Bukkit.getWorlds().forEach(world -> world.getEntitiesByClasses(Item.class).forEach(entity -> {
                    if (!(entity instanceof Item)) return;
                    Item item = (Item) entity;

                    NBTItem nbtItemGround = new NBTItem(item.getItemStack());
                    NBTItem nbtItemContent = new NBTItem(content);

                    if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                        item.remove();
                    }
                }));
            } else if (location.getType() == LocationType.BLOCK_INVENTORY) {
                if (!(location.getLocation().getBlock().getState() instanceof InventoryHolder)) continue;
                InventoryHolder holder = (InventoryHolder) location.getLocation().getBlock().getState();

                int i = -1;
                Map<Integer, ItemStack> stacks = new HashMap<>();
                for (ItemStack stack : holder.getInventory().getContents()) {
                    i++;
                    if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                    NBTItem nbtItemGround = new NBTItem(stack);
                    NBTItem nbtItemContent = new NBTItem(content);

                    if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                        stacks.put(i, stack);
                    }
                }

                stacks.keySet().forEach(j -> holder.getInventory().setItem(j, null));
            } else if (location.getType() == LocationType.DOUBLE_CHEST) {
                Block block = location.getLocation().getBlock();
                BlockState chestState = block.getState();
                if (chestState instanceof Chest) {
                    Chest chest = (Chest) chestState;
                    Inventory inventory = chest.getInventory();
                    if (inventory instanceof DoubleChestInventory) {
                        int i = -1;
                        Map<Integer, ItemStack> inventoryItems = new HashMap<>();
                        NBTItem nbtItemContent = new NBTItem(content);

                        for (ItemStack stack : inventory.getContents()) {
                            i++;
                            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                            NBTItem nbtItemGround = new NBTItem(stack);

                            if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                                inventoryItems.put(i, stack);
                                System.out.println(i + ":" + nbtItemGround.getUUID("id"));
                            }
                        }
                        i = -1;

                        inventoryItems.keySet().forEach(j -> inventory.setItem(j, null));
                        System.out.println("did it");
                    }
                }
            } else if (location.getType() == LocationType.ENDER_CHEST) {
                OfflinePlayer holderOffline = Bukkit.getOfflinePlayer(location.getHolderUUID());
                ItemStack[] cache = pCore.getInstance().getEnderchestHandler().fetchCache(holderOffline.getUniqueId());
                int i = -1;
                Map<Integer, ItemStack> stacks = new HashMap<>();
                NBTItem nbtItemContent = new NBTItem(content);

                for (ItemStack stack : cache) {
                    i++;
                    if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;
                    NBTItem nbtItemGround = new NBTItem(stack);

                    if (nbtItemGround.hasTag("id") && nbtItemContent.hasTag("id") && nbtItemGround.getUUID("id").equals(nbtItemContent.getUUID("id"))) {
                        stacks.put(i, stack);
                        System.out.println(i + ":" + nbtItemGround.getUUID("id"));
                    }
                }

                stacks.keySet().forEach(j -> cache[j] = null);
                pCore.getInstance().getEnderchestHandler().updateCache(holderOffline.getUniqueId(), cache);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getOpenInventory() != null && !player.getOpenInventory().getTitle().equalsIgnoreCase(holderOffline.getName() + "'s EnderChest")) continue;

                    player.getOpenInventory().getTopInventory().setContents(cache);
                }
            }

            /*
            Update the Locations
             */

            NBTItem item = new NBTItem(content);

            pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, this.victim, LocationType.PLAYER_INVENTORY));

        }
    }

}
