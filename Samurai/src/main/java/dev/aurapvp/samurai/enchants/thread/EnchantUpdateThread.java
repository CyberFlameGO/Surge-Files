package dev.aurapvp.samurai.enchants.thread;

import dev.aurapvp.samurai.enchants.CustomEnchant;
import dev.aurapvp.samurai.enchants.EnchantHandler;
import dev.aurapvp.samurai.enchants.set.ArmorSet;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
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

                        enchants.addAll(Samurai.getInstance().getEnchantHandler().getCustomEnchants(armor).keySet());
                    }

                    for (CustomEnchant enchant : Samurai.getInstance().getEnchantHandler().getEnchants()) {
                        if (enchants.contains(enchant)) continue;
                        if (!enchant.hasEnchantApplied(player)) continue;

                        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> enchant.unApply(player));
                    }

                    if (player.getInventory().getArmorContents() != null) {
                        for (ItemStack content : player.getInventory().getArmorContents()) {
                            for (Map.Entry<CustomEnchant, Integer> entry : Samurai.getInstance().getEnchantHandler().getCustomEnchants(content).entrySet()) {
                                if (entry.getKey().hasEnchantApplied(player)) continue;

                                Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                                    entry.getKey().apply(player);
                                    entry.getKey().apply(player, entry.getValue());
                                    player.sendMessage(CC.translate("&aApplying: " + entry.getKey().getName()));
                                });
                            }
                        }
                    }

                    if (!player.hasMetadata(EnchantHandler.getARMOR_SET_TAG())) continue;

                    for (ArmorSet set : Samurai.getInstance().getArmorSetHandler().getArmorSets().values()) {
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
