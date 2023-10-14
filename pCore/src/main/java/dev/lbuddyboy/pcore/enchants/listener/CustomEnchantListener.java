package dev.lbuddyboy.pcore.enchants.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.enchants.EnchantHandler;
import dev.lbuddyboy.pcore.enchants.set.ArmorSet;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.event.ArmorEquipEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class CustomEnchantListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Tasks.delayed(() -> {
            for (ItemStack content : player.getInventory().getArmorContents()) {
                if (content == null || content.getType() == Material.AIR) continue;
                Map<CustomEnchant, Integer> enchants = pCore.getInstance().getEnchantHandler().getCustomEnchants(content);

                if (enchants.isEmpty()) continue;

                enchants.forEach((key, value) -> key.apply(event.getPlayer()));
                enchants.forEach((key, value) -> key.apply(event.getPlayer(), value));
            }

            ItemStack held = player.getInventory().getItemInHand();
            Map<CustomEnchant, Integer> enchants = pCore.getInstance().getEnchantHandler().getCustomEnchants(held);
            if (enchants.isEmpty()) return;

            enchants.forEach((key, value) -> key.apply(event.getPlayer()));
            enchants.forEach((key, value) -> key.apply(event.getPlayer(), value));

            for (ArmorSet set : pCore.getInstance().getEnchantHandler().getArmorSets().values()) {
                if (!set.hasSetOn(player)) continue;
                if (set.hasOn(player)) continue;

                set.activate(player);
            }

        });
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        for (ItemStack content : player.getInventory().getArmorContents()) {
            Map<CustomEnchant, Integer> enchants = pCore.getInstance().getEnchantHandler().getCustomEnchants(content);
            if (enchants.isEmpty()) continue;

            enchants.forEach((key, value) -> key.unApply(event.getPlayer()));
        }
    }

    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        Tasks.delayedAsync(() -> {
            if (!player.hasMetadata(EnchantHandler.getARMOR_SET_TAG())) {
                for (ArmorSet set : pCore.getInstance().getEnchantHandler().getArmorSets().values()) {
                    if (!set.hasSetOn(player)) continue;
                    if (set.hasOn(player)) continue;

                    set.activate(player);
                }
            }
        });
    }

    // Use this if the other methods become too laggy

/*    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHoldPrevious(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getPreviousSlot());
        if (stack == null) return;
        if (!stack.getType().name().endsWith("_SWORD")
                && !stack.getType().name().endsWith("_AXE")
                && !stack.getType().name().endsWith("_SHOVEL")
                && !stack.getType().name().endsWith("_PICKAXE")
                && !stack.getType().name().endsWith("_HOE")
                && !stack.getType().name().equalsIgnoreCase("BOW")
        ) return;
        Map<CustomEnchant, Integer> enchants = EnchantConstants.getEnchants(stack);
        enchants.forEach((key, value) -> key.unApply(event.getPlayer()));

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHoldNew(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = player.getInventory().getItem(event.getNewSlot());
        if (stack == null) return;
        if (!stack.getType().name().endsWith("_SWORD")
                && !stack.getType().name().endsWith("_AXE")
                && !stack.getType().name().endsWith("_SHOVEL")
                && !stack.getType().name().endsWith("_PICKAXE")
                && !stack.getType().name().endsWith("_HOE")
                && !stack.getType().name().equalsIgnoreCase("BOW")
        ) return;
        Map<CustomEnchant, Integer> enchants = EnchantConstants.getEnchants(stack);
        enchants.forEach((key, value) -> {
            key.apply(event.getPlayer());
            key.apply(event.getPlayer(), value);
        });

    }*/

}
