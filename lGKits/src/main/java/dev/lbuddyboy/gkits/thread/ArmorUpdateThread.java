package dev.lbuddyboy.gkits.thread;

import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.lGKits;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArmorUpdateThread extends Thread {

    private long lastErrored;

    @Override
    public void run() {
        while (true) {
            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<CustomEnchant> enchants = new ArrayList<>();

                    for (ItemStack armor : player.getInventory().getArmorContents()) {
                        if (armor == null || armor.getType() == Material.AIR || armor.getAmount() <= 0) continue;

                        enchants.addAll(lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(armor).keySet());
                    }

                    for (CustomEnchant enchant : lGKits.getInstance().getCustomEnchantManager().getEnchants()) {
                        if (enchants.contains(enchant)) continue;
                        if (!enchant.hasEnchant(player)) continue;

                        Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> enchant.deactivate(player));
                    }

                    if (player.getInventory().getArmorContents() != null) {
                        for (ItemStack content : player.getInventory().getArmorContents()) {
                            for (Map.Entry<CustomEnchant, Integer> entry : lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(content).entrySet()) {
                                if (entry.getKey().hasEnchant(player)) continue;

                                Bukkit.getScheduler().runTask(lGKits.getInstance(), () -> entry.getKey().activate(player, content, 1));
                            }
                        }
                    }

                    if (!player.hasMetadata("lgkits-custom-set")) continue;

                    for (ArmorSet set : lGKits.getInstance().getArmorSets()) {
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
