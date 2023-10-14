package dev.lbuddyboy.samurai.map.kits;

import dev.lbuddyboy.samurai.map.kits.upgrades.Upgrades;
import dev.lbuddyboy.samurai.map.shards.menu.upgrades.ShardShopUpgradeKitMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Kit {

    private String name;
    private String original;

    private ItemStack[] inventoryContents = new ItemStack[36];
    private ItemStack[] armorContents = new ItemStack[4];

    public Kit(String name) {
        this.name = name;
    }

    public Kit(DefaultKit original) {
        if (original != null) {
            this.name = original.getName();
            this.original = original.getName();
            this.inventoryContents = Arrays.copyOf(original.getInventoryContents(), original.getInventoryContents().length);
            this.armorContents = Arrays.copyOf(original.getArmorContents(), original.getArmorContents().length);
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void apply(Player player) {
        ItemStack[] inventory = new ItemStack[inventoryContents.length];

        for (int i = 0; i < inventory.length; i++) {
            inventory[i] = inventoryContents[i] == null ? null : inventoryContents[i].clone();
        }

        ItemStack[] armor = new ItemStack[armorContents.length];

        for (int i = 0; i < armorContents.length; i++) {
            armor[i] = armorContents[i] == null ? null : armorContents[i].clone();
        }

        doKitUpgradesMagic(player, inventory);
        doKitUpgradesMagic(player, armor);

        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);

        player.updateInventory();
    }

    public void update(PlayerInventory inventory) {
        inventoryContents = inventory.getStorageContents();
        armorContents = inventory.getArmorContents();
    }

    public int countHeals() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
    }

    public int countDebuffs() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.DEBUFF_POTION_PREDICATE);
    }

    public int countFood() {
        return ItemUtils.countStacksMatching(inventoryContents, ItemUtils.EDIBLE_PREDICATE);
    }

    public int countPearls() {
        return ItemUtils.countStacksMatching(inventoryContents, v -> v.getType() == Material.ENDER_PEARL);
    }

    private void doKitUpgradesMagic(Player player, ItemStack[] items) {
        Map<Material, Upgrades> map = Samurai.getInstance().getMapHandler().getKitUpgradesHandler().getUpgrades(player);
        if (map == null) return;

        for (ItemStack item : items) {
            if (item == null) continue;

            doKitUpgradesMagic(map, item);
        }
    }

    public static void doKitUpgradesMagic(Map<Material, Upgrades> map, ItemStack item) {
        Upgrades upgrades = map.get(item.getType());
        if (upgrades == null) return;

        for (Enchantment enchantment : upgrades.getEnchantmentList()) {
            item.addUnsafeEnchantment(enchantment, ShardShopUpgradeKitMenu.LEVEL_MAP.get(enchantment.getName()));
        }

        for (String enchantment : upgrades.getCustomEnchantmentList()) {
            int level = ShardShopUpgradeKitMenu.LEVEL_MAP.get(enchantment);

            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) lore = new ArrayList<>();
            lore.add(CC.GOLD + enchantment + " " + level);
            meta.setLore(lore);

            item.setItemMeta(meta);
        }
    }

    public DefaultKit getOriginal() {
        if (original == null) {
            return null;
        }

        return Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(original);
    }

    public ItemStack[] getAllContents() {
        ItemStack[] items = new ItemStack[40];
        System.arraycopy(inventoryContents, 0, items, 0, inventoryContents.length);
        System.arraycopy(armorContents, 0, items, inventoryContents.length, armorContents.length);
        return items;
    }

}
