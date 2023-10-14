package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

@CommandAlias("lastinv")
@CommandPermission("foxtrot.lastinv")
public class LastInvCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void lastInv(Player sender, @Name("player") UUID player) {
        Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().runRedisCommand((redis) -> {
                if (!redis.exists("lastInv:contents:" + player.toString())) {
                    sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + FrozenUUIDCache.name(player));
                    return null;
                }

                ItemStack[] contents = Samurai.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + player.toString()), ItemStack[].class);
                ItemStack[] armor = Samurai.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + player.toString()), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);

                Samurai.getInstance().getServer().getScheduler().runTask(Samurai.getInstance(), () -> {
                    sender.getInventory().setContents(contents);
                    sender.getInventory().setArmorContents(armor);
                    sender.updateInventory();

                    sender.sendMessage(ChatColor.GREEN + "Loaded " + FrozenUUIDCache.name(player) + "'s last inventory.");
                });

                return null;
            });
        });
    }

    public static void lastInvToOther(Player sender, Player other) {
        Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().runRedisCommand((redis) -> {
                if (!redis.exists("lastInv:contents:" + other.getUniqueId().toString())) {
                    sender.sendMessage(ChatColor.RED + "No last inventory recorded for " + other.getName());
                    return null;
                }

                ItemStack[] contents = Samurai.PLAIN_GSON.fromJson(redis.get("lastInv:contents:" + other.getUniqueId().toString()), ItemStack[].class);
                ItemStack[] armor = Samurai.PLAIN_GSON.fromJson(redis.get("lastInv:armorContents:" + other.getUniqueId().toString()), ItemStack[].class);

                cleanLoot(contents);
                cleanLoot(armor);

                Samurai.getInstance().getServer().getScheduler().runTask(Samurai.getInstance(), () -> {
                    other.getInventory().setContents(contents);
                    other.getInventory().setArmorContents(armor);
                    other.updateInventory();

                    other.sendMessage(ChatColor.GREEN + "Loaded " + other.getName() + "'s last inventory.");
                });

                return null;
            });
        });
    }

    public static void cleanLoot(ItemStack[] stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }

    public static void cleanLoot(List<ItemStack> stack) {
        for (ItemStack item : stack) {
            if (item != null && item.hasItemMeta() && item.getItemMeta().hasLore()) {
                ItemMeta meta = item.getItemMeta();

                List<String> lore = item.getItemMeta().getLore();
                lore.remove(ChatColor.DARK_GRAY + "PVP Loot");
                meta.setLore(lore);

                item.setItemMeta(meta);
            }
        }
    }

    public static void recordInventory(Player player) {
        recordInventory(player.getUniqueId(), player.getInventory().getContents(), player.getInventory().getArmorContents());
    }

    public static void recordInventory(UUID player, ItemStack[] contents, ItemStack[] armor) {
        Samurai.getInstance().getServer().getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> {
            Samurai.getInstance().runRedisCommand((redis) -> {
                redis.set("lastInv:contents:" + player.toString(), Samurai.PLAIN_GSON.toJson(contents));
                redis.set("lastInv:armorContents:" + player.toString(), Samurai.PLAIN_GSON.toJson(armor));
                return null;
            });
        });
    }

}