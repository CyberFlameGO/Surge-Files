package dev.aurapvp.samurai.enchants.impl;

import de.tr7zw.nbtapi.NBTItem;
import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.IntRange;
import dev.aurapvp.samurai.util.StringUtils;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class Strength extends CustomEnchant implements Listener {

    private String procMessage = "&4&l[STRENGTH] &cYour strength custom enchantment has just worn off. Your armors normal durability will now be effected.";

    @Override
    public void init() {
        registerListener(this);
        if (getConfig().contains("activated-message")) {
            this.procMessage = getConfig().getString("activated-message");
            return;
        }

        getConfig().set("activated-message", "&4&l[STRENGTH] &cYour strength custom enchantment has just worn off. Your armors normal durability will now be effected.");
    }

    @Override
    public String getName() {
        return "strength";
    }

    @Override
    public List<String> getDescription() {
        if (!getConfig().contains("description")) return Arrays.asList(
                "&fWhen applied any of the following",
                "&fyou will receive more durability",
                "&fbased on the enchant level!",
                "&fApplies to: &e" + StringUtils.join(getApplicable())
        );
        return getConfig().getStringList("description");
    }

    @Override
    public String getDisplayName() {
        return getConfig().getString("displayName", "Strength");
    }

    @Override
    public String getColor() {
        return getConfig().getString("color", "&5");
    }

    @Override
    public IntRange getRange() {
        String[] parts = getConfig().getString("range", "1-3").split("-");
        return new IntRange(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    @Override
    public Rarity getRarity() {
        return Samurai.getInstance().getEnchantHandler().getRarity(getConfig().getString("rarity", "Epic")).get();
    }

    @Override
    public double getChance() {
        return getConfig().getDouble("chance", 50.0);
    }

    @Override
    public List<String> getApplicable() {
        if (!getConfig().contains("applicable")) return Arrays.asList("HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS", "SWORD", "PICKAXE", "AXE", "SPADE", "HOE", "BOW");
        return validateApplicable(getConfig().getStringList("applicable"));
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (!hasEnchant(item)) return;

        NBTItem nbtItem = new NBTItem(item);
        int level = Samurai.getInstance().getEnchantHandler().getCustomEnchants(item).get(this);

        boolean armor = false;
        int slot = -1;

        for (ItemStack gear : player.getInventory().getArmorContents()) {
            slot++;
            if (gear != null && gear.isSimilar(item)) {
                armor = true;
                break;
            }
        }

        if (!armor) slot = player.getInventory().getHeldItemSlot();

        if (nbtItem.hasTag(getName() + "-durability")) {
            int extra = nbtItem.getInteger(getName() + "-durability");
            if (extra < 0) return;

            if (extra == 0) {
                player.sendMessage(CC.translate(this.procMessage));
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 2.0f, 2.0f);
            }
            nbtItem.setInteger(getName() + "-durability", extra - event.getDamage());
        } else {
            nbtItem.setInteger(getName() + "-durability", 50 * level);
        }

        event.setDamage(0);
        set(player, nbtItem.getItem(), slot, armor);
    }

    private void set(Player player, ItemStack item, int slot, boolean armor) {
        if (!armor) {
            player.getInventory().setItem(slot, item);
            return;
        }
        EquipmentSlot equipmentSlot = null;

        if (slot == 0) {
            player.getInventory().setBoots(item);
        } else if (slot == 1) {
            player.getInventory().setLeggings(item);
        } else if (slot == 2) {
            player.getInventory().setChestplate(item);
        } else if (slot == 3) {
            player.getInventory().setHelmet(item);
        }
    }

}
