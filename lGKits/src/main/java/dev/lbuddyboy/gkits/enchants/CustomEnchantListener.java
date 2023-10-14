package dev.lbuddyboy.gkits.enchants;

import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.command.ArmorSetsCommand;
import dev.lbuddyboy.gkits.enchants.event.ArmorEquipEvent;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.InventoryUtils;
import dev.lbuddyboy.gkits.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 8:05 PM
 * GKits / me.lbuddyboy.gkits.enchants.event
 */
public class CustomEnchantListener implements Listener {

    @Getter
    @Setter
    private static boolean disabled = false;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(lGKits.getInstance(), () -> {
            try {
                for (ItemStack stack : event.getPlayer().getInventory().getArmorContents()) {
                    for (CustomEnchant enchant : lGKits.getInstance().getCustomEnchantManager().getCustomEnchants(stack).keySet()) {
                        enchant.activate(event.getPlayer(), stack, 1);
                    }
                }
            } catch (Exception ignored) {

            }
        }, 10);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack stack = event.getItem();
        if (stack == null) return;
        if (!stack.isSimilar(ArmorSetsCommand.randomArmorSet)) return;

        event.setCancelled(true);

        try {
            ArmorSet set = randomSet();

            set.reward(event.getPlayer());
            InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), stack, 1);
        } catch (Exception e) {
            event.getPlayer().sendMessage(CC.translate("&cThere was an error opening that armor crate."));
        }

    }

    public ArmorSet randomSet() {
        return lGKits.getInstance().getArmorSets().get(ThreadLocalRandom.current().nextInt(0, lGKits.getInstance().getArmorSets().size()));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        for (ArmorSet set : lGKits.getInstance().getArmorSets()) {
            if (set.hasOn(event.getPlayer()))
                set.deactivate(event.getPlayer());
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            ItemStack stack = event.getEntity().getKiller().getItemInHand();
            if (hasEnchant(stack, "Behead I")) {
                ItemStack deathSkull = new ItemStack(Material.LEGACY_SKULL_ITEM);
                SkullMeta skullMeta = (SkullMeta) deathSkull.getItemMeta();

                ArrayList<String> lore = new ArrayList<>();

                lore.add("§6" + event.getEntity().getName());
                lore.add("§fSlain By:");
                lore.add("§6" + event.getEntity().getKiller().getName());

                DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");

                lore.add("§f" + sdf.format(new Date()).replace(" AM", "").replace(" PM", ""));

                skullMeta.setOwner(event.getEntity().getName());
                skullMeta.setDisplayName("§6" + event.getEntity().getName() + "'s Skull");
                skullMeta.setLore(lore);
                deathSkull.setItemMeta(skullMeta);

                event.getDrops().add(deathSkull);

            }
        }
        if (event.getEntity().getKiller() != null) {
            if (event.getEntity().getKiller().hasMetadata("recover")) {
                Player killer = event.getEntity().getKiller();
                killer.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 10, 3));
            }
        }
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        if (event.getEntity().hasMetadata("saturation")) {
            event.setFoodLevel(20);
        }
    }
    @EventHandler
    public void onArmorEquip(ArmorEquipEvent event) {
        Player player = event.getPlayer();
        lGKits.getInstance().getExecutorService().execute(() -> {
            lGKits.getInstance().getCustomEnchantManager().updateCE(player, event.getOldArmorPiece(), event.getNewArmorPiece());

            if (!player.hasMetadata("lgkits-custom-set")) {
                for (ArmorSet set : lGKits.getInstance().getArmorSets()) {
                    if (!set.hasSetOn(player)) continue;
                    if (set.hasOn(player)) continue;

                    set.activate(player);
                }
            }
        });
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null) return;
        if (!isArmor(item)) return;
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        ItemStack old = null;

        if (item.getType().name().endsWith("_HELMET") && player.getInventory().getHelmet() != null) {
            old = player.getInventory().getHelmet();
        } else if (item.getType().name().endsWith("_CHESTPLATE") && player.getInventory().getChestplate() != null) {
            old = player.getInventory().getChestplate();
        } else if (item.getType().name().endsWith("_LEGGINGS") && player.getInventory().getLeggings() != null) {
            old = player.getInventory().getLeggings();
        } else if (item.getType().name().endsWith("_BOOTS") && player.getInventory().getBoots() != null) {
            old = player.getInventory().getBoots();
        }

        if (old == null) return;

        lGKits.getInstance().getCustomEnchantManager().updateCE(player, old, item);
    }

    private final boolean isArmor(final ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().name();
        if (typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS")) {
            return true;
        }

        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        if (disabled) return;
        ItemStack stack = event.getPlayer().getItemInHand();

        if (event.getPlayer().hasMetadata("breaking")) {
            return;
        }

        if (materialDropMap.containsKey(event.getBlock().getType())) {
            if (hasEnchant(stack, "autosmelt")) {
                ItemUtils.tryFit(event.getPlayer(), new ItemStack(materialDropMap.get(event.getBlock().getType())));
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);
                return;
            }
        }

        if (!blockedMaterials.contains(event.getBlock().getType().name())) {
            if (hasEnchant(stack, lGKits.getInstance().getCustomEnchantManager().byName("blast").displayName())) {
                performBlast(event.getBlock().getLocation().clone(), event.getPlayer());
            }
        }
    }

    private Map<Material, Material> materialDropMap = new HashMap<>();

    public CustomEnchantListener() {
        materialDropMap.put(Material.SAND, Material.GLASS);
        materialDropMap.put(Material.IRON_ORE, Material.IRON_INGOT);
        materialDropMap.put(Material.GOLD_ORE, Material.GOLD_INGOT);
        materialDropMap.put(Material.COBBLESTONE, Material.STONE);
    }

    public void performBlast(Location l, Player player) {
        int lev = 2 - 1;
        List<Block> blocks = new ArrayList<>(2 * 2 * 2);
        for (int x = -lev; x <= lev; x++) {
            for (int y = -lev; y <= lev; y++) {
                for (int z = -lev; z <= lev; z++) {
                    blocks.add(l.getBlock().getRelative(x, y, z));
                }
            }
        }

        player.setMetadata("breaking", new FixedMetadataValue(lGKits.getInstance(), true));

        for (Block block : blocks) {
            if (blockedMaterials.contains(block.getType().name())) continue;
            BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
            Bukkit.getPluginManager().callEvent(blockBreakEvent);
            if (!blockBreakEvent.isCancelled()) {
                block.breakNaturally(player.getInventory().getItemInMainHand());
            }
        }
        player.removeMetadata("breaking", lGKits.getInstance());

    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        ItemStack armor = event.getItem();

        if (hasEnchant(armor, "repair")) {
            armor.setDurability((short) 0);
            event.setCancelled(true);
        }
    }

    private boolean hasEnchant(ItemStack stack, String enchant) {
        CustomEnchant ce = lGKits.getInstance().getCustomEnchantManager().byName(enchant);
        if (ce == null) return false;
        if (ItemUtils.hasLore(stack)) {
            for (String s : stack.getItemMeta().getLore()) {
                try {
                    String[] args = ChatColor.stripColor(s).split(" ");
                    if (args[0].equals(ce.displayName())) {
                        return true;
                    }
                } catch (Exception ignored) {

                }
            }
        }
        return false;
    }

    private final List<String> blockedMaterials = Arrays.asList("BEDROCK", "CHEST", "TRAPPED_CHEST", "FENCE_GATE", "MOB_SPAWNER", "DOOR", "WATER", "LAVA");

}
