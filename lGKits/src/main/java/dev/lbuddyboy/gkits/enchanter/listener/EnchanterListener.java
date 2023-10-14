package dev.lbuddyboy.gkits.enchanter.listener;

import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.enchanter.object.EnchantBook;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 11:10 PM
 * GKits / me.lbuddyboy.gkits.enchanter.listener
 */
public class EnchanterListener implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT"))
            return;
        if (event.getPlayer().getInventory().getItemInMainHand() == null)
            return;

        if (event.getPlayer().getInventory().getItemInMainHand().isSimilar(lGKits.getInstance().getEnchanterManager().getItem())) {
            List<CustomEnchant> ce = lGKits.getInstance().getCustomEnchantManager().getEnchants();
            int random = new Random().nextInt(ce.size());
            CustomEnchant e = ce.get(random);
            int levelRand = ThreadLocalRandom.current().nextInt(e.minLevel(), e.maxLevel());
            EnchantBook enchant = new EnchantBook(e, levelRand);
            ItemUtils.tryFit(event.getPlayer(), enchant.build());
            if (event.getPlayer().getInventory().getItemInMainHand().getAmount() > 1) {
                event.getPlayer().getInventory().getItemInMainHand().setAmount(event.getPlayer().getItemInHand().getAmount() - 1);
            } else {
                event.getPlayer().getInventory().setItemInMainHand(null);
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent event) {
        if (!event.getClick().isLeftClick()) return;

        if (event.getClick().isKeyboardClick()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        ItemStack cursor = event.getCursor();

        if (cursor == null) return;
        if (!ItemUtils.hasDisplayName(cursor)) return;

        ItemStack stack = event.getCurrentItem();

        if (stack == null || stack.getType() == Material.AIR) return;

        String ceName = ChatColor.stripColor(cursor.getItemMeta().getDisplayName().split(" ")[0]);

        CustomEnchant ce = lGKits.getInstance().getCustomEnchantManager().byName(ceName);
        if (ce == null) return;

        for (String allowedType : ce.allowedTypes()) {
            if (!stack.getType().name().contains("_" + allowedType)) continue;

            event.setCancelled(true);

            ItemMeta meta = stack.getItemMeta();
            List<String> newLore = new ArrayList<>();
            int lineWithEnchant = -1;
            int level = 1;
            boolean hasEnchant = false;
            if (ce.name().equals("depthstrider")) {
                hasEnchant = stack.containsEnchantment(Enchantment.DEPTH_STRIDER);
            } else {
                if (ItemUtils.hasLore(stack)) {
                    newLore = meta.getLore();
                    for (String s : meta.getLore()) {
                        try {
                            String[] args = s.split(" ");
                            String sName = ChatColor.stripColor(args[0]).toLowerCase();
                            ++lineWithEnchant;

                            if (sName.equalsIgnoreCase(ce.displayName())) {
                                level = Integer.parseInt(args[1]);
                                hasEnchant = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
            }

            if (ce.name().equals("depthstrider")) {
                stack.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
            } else {
                if (hasEnchant && level >= Integer.parseInt(cursor.getItemMeta().getDisplayName().split(" ")[1])) {
                    player.sendMessage(CC.translate("&cThat enchant is already applied."));
                    return;
                }

                if (hasEnchant) {
                    newLore.set(lineWithEnchant, cursor.getItemMeta().getDisplayName());
                } else {
                    newLore.add(cursor.getItemMeta().getDisplayName());
                }

                AtomicInteger i = new AtomicInteger();
                newLore = newLore.stream().sorted(Comparator.comparingInt(string -> {
                    for (ArmorSet set : lGKits.getInstance().getArmorSets()) {
                        for (String s : CC.translate(set.getLoreFormat())) {
                            if (s.equalsIgnoreCase(string) || string.contains("Armor Set")) {
                                return i.getAndIncrement();
                            }
                        }
                    }
                    for (CustomEnchant customEnchant : lGKits.getInstance().getCustomEnchantManager().getEnchants()) {
                        if (string.contains(customEnchant.displayName())) {
                            return customEnchant.topRarity().getWeight();
                        }
                    }

                    return 100000;
                })).collect(Collectors.toList());

                Collections.reverse(newLore);

                meta.setLore(CC.translate(newLore));
                stack.setItemMeta(meta);
                NBTItem item = new NBTItem(stack);

                for (String s : newLore) {
                    for (CustomEnchant customEnchant : lGKits.getInstance().getCustomEnchantManager().getEnchants()) {
                        if (s.contains(customEnchant.displayName())) {
                            item.setInteger("lgkits-" + customEnchant.name(), Integer.valueOf(cursor.getItemMeta().getDisplayName().split(" ")[1]));
                        }
                    }
                }

                event.setCurrentItem(item.getItem());
            }

            Bukkit.getScheduler().runTaskLater(lGKits.getInstance(), () -> {
                if (cursor.getAmount() <= 1) {
                    player.setItemOnCursor(null);
                    event.getView().setCursor(null);
                } else {
                    cursor.setAmount(cursor.getAmount() - 1);
                }
                player.updateInventory();
            }, 2);

            return;
        }
    }
}

