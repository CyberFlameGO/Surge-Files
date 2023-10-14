package dev.lbuddyboy.samurai.map.shards.menu.upgrades;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.upgrades.Upgrades;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class EnchantmentUpgradeButton extends Button {

    private final Material material;
    private final Enchantment enchantment;
    private final int cost;
    private final ItemStack icon;

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Map<Material, Upgrades> map = Samurai.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
        Upgrades upgrades = map.get(material);

        if (upgrades != null && upgrades.getEnchantmentList().contains(enchantment)) {
            player.sendMessage(CC.RED + "You already have this unlocked!");
            return;
        }

        if (!Samurai.getInstance().getShardMap().removeShards(player.getUniqueId(), cost)) {
            player.sendMessage(CC.RED + "You need " + cost + " shards to buy this upgrade!");
            return;
        }

        map.computeIfAbsent(material, mat -> new Upgrades()).getEnchantmentList().add(enchantment);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        ItemBuilder esmee = ItemBuilder.copyOf(icon.clone())
                .addToLore("")
                .addToLore(CC.YELLOW + " " + CC.WHITE + "Cost: " + CC.GOLD + cost + " Shards");

        Map<Material, Upgrades> map = Samurai.getInstance().getMapHandler().getKitUpgradesHandler().getOrComputeUpgrades(player);
        Upgrades upgrades = map.get(material);

        esmee.addToLore("");

        if (upgrades != null && upgrades.getEnchantmentList().contains(enchantment)) {
            esmee.addToLore(CC.RED + "You already have this purchased!");
        } else {
            esmee.addToLore(CC.GREEN + "Click to purchase this upgrade!");
        }

        return esmee.build();
    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}
