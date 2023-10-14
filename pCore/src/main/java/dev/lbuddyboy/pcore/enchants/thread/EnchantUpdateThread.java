package dev.lbuddyboy.pcore.enchants.thread;

import dev.lbuddyboy.pcore.enchants.CustomEnchant;
import dev.lbuddyboy.pcore.enchants.EnchantHandler;
import dev.lbuddyboy.pcore.enchants.set.ArmorSet;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EnchantUpdateThread extends Thread {

    private long lastErrored;

    @Override
    public void run() {
        while (true) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<CustomEnchant> enchants = new ArrayList<>();

                    for (ItemStack armor : player.getInventory().getArmorContents()) {
                        if (armor == null || armor.getType() == Material.AIR || armor.getAmount() <= 0) continue;

                        enchants.addAll(pCore.getInstance().getEnchantHandler().getCustomEnchants(armor).keySet());
                    }

                    for (CustomEnchant enchant : pCore.getInstance().getEnchantHandler().getEnchants()) {
                        if (enchants.contains(enchant)) continue;
                        if (!enchant.hasEnchantApplied(player)) continue;

                        Bukkit.getScheduler().runTask(pCore.getInstance(), () -> enchant.unApply(player));
                    }

                    if (player.getInventory().getArmorContents() != null) {
                        for (ItemStack content : player.getInventory().getArmorContents()) {
                            for (Map.Entry<CustomEnchant, Integer> entry : pCore.getInstance().getEnchantHandler().getCustomEnchants(content).entrySet()) {
                                if (entry.getKey().hasEnchantApplied(player)) continue;

                                Bukkit.getScheduler().runTask(pCore.getInstance(), () -> {
                                    entry.getKey().apply(player);
                                    entry.getKey().apply(player, entry.getValue());
                                    player.sendMessage(CC.translate("&aApplying: " + entry.getKey().getName()));
                                });
                            }
                        }
                    }

                    if (!player.hasMetadata(EnchantHandler.getARMOR_SET_TAG())) continue;

                    for (ArmorSet set : pCore.getInstance().getEnchantHandler().getArmorSets().values()) {
                        if (!set.hasOn(player)) continue;
                        if (set.hasSetOn(player)) break;

                        set.deactivate(player);
                    }
                }
                sleep(500);
            } catch (Exception e) {
                if (lastErrored + 30_000L > System.currentTimeMillis()) return;

                e.printStackTrace();
                lastErrored = System.currentTimeMillis();
            }
        }
    }

}
