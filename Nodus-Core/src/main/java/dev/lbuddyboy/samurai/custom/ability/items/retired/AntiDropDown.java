package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class AntiDropDown extends AbilityItem {

    private static final int HITS = 3; // how many hits it takes for the item to activate
    private static Cooldown antiDropDowned = new Cooldown();
    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
    private final Map<UUID, Integer> initialY = new HashMap<>();
    private final Map<UUID, List<Location>> prevs = new HashMap<>();

    public AntiDropDown() {
        super("AntiDropDown");
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player entity && event.getDamager() instanceof Player attacker) {

            if (attacker.getWorld().getEnvironment() == World.Environment.THE_END || attacker.getWorld().getEnvironment() == World.Environment.NETHER)
                return;

            Pair<UUID, UUID> key = new Pair<>(attacker.getUniqueId(), entity.getUniqueId());

            ItemStack item = attacker.getItemInHand();
            boolean partnerItem = isPartnerItem(item);

            if (attackMap.containsKey(key) && !partnerItem) {
                attackMap.remove(key);
                return;
            }

            if (!partnerItem) {
                return;
            }

            if (isOnCooldown(attacker)) {
                attacker.sendMessage(getCooldownMessage(attacker));
                return;
            }

            if (antiDropDowned.onCooldown(entity.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "That player is already anti-d!");
                return;
            }

            if (Samurai.getInstance().getServerHandler().isWarzone(attacker.getLocation())) {
                event.setCancelled(true);
                attacker.sendMessage(CC.translate("&cYou cannot use ability items in the warzone."));
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            initialY.put(entity.getUniqueId(), entity.getLocation().getBlockY() - 1);
            antiDropDowned.applyCooldown(entity, 15);
            setCooldown(attacker);
            consume(attacker, item);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.MAIN + entity.getName() + CC.WHITE + " with an " + getName() + CC.WHITE + "!",
                            "That player can no longer change y-level!"
                    },
                    entity,
                    new String[]{
                            CC.MAIN + attacker.getName() + " has hit you " + CC.WHITE + " with an " + getName() + CC.WHITE + "!",
                            "You may not change y-level for 15 seconds!"
                    });

            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                for (Map.Entry<UUID, List<Location>> entry : prevs.entrySet()) {
                    entry.getValue().forEach(location -> location.getBlock().setType(Material.AIR));
                }
                prevs.remove(entity.getUniqueId());
            }, 20 * 15);

        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!antiDropDowned.onCooldown(player)) return;
        int y = initialY.get(player.getUniqueId());

        if (event.getTo() != null && event.getTo().getBlockY() > y) {
            return;
        }

        Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        for (int x = -2; x < 2; x++) {
            for (int z = -2; z < 2; z++) {
                Block other = below.getRelative(x, 0, z);
                if (other.getType() != Material.AIR) continue;

                List<Location> locations = prevs.containsKey(player.getUniqueId()) ? prevs.get(player.getUniqueId()) : new ArrayList<>();

                prevs.put(player.getUniqueId(), locations);

                other.setType(Material.RED_STAINED_GLASS);
                locations.add(other.getLocation());
            }
        }

    }
    
    @EventHandler // cleanup attack map
    public void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 45L : 90L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BLAZE_ROD)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fHit a player 3 times with this item",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fto prevent them from going anything",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fpast or below their current y level",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &ffor 15 seconds.",
                        " "
                ).modelData(6).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lAnti Drop Down");
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}

