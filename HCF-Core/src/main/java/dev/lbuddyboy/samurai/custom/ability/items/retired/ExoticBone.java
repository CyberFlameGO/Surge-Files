package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockMultiPlaceEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

public final class ExoticBone extends AbilityItem {

    private static final int HITS = 3; // how many hits it takes for the item to activate
    private static final int ANTI_TIME = 30; // how long the victim is anti-d for in seconds
    private static final int ANTI_BOOST_TIME = 20; // how long the victim is anti-d for in seconds

    // block types you cannot interact with while anti-d
    private List<String> materials = Arrays.asList(
            "_GATE",
            "_DOOR",
            "_CHEST",
            "_LEVER",
            "_BUTTON"
    );
    // attacker uuid -> victim id , total hits
    private final Map<Pair<UUID, UUID>, Integer> attackMap = new HashMap<>();
    // anti-d players
    private final Map<UUID, Instant> antiMap = new HashMap<>();

    public ExoticBone() {
        super("ExoticBone");
    }

    // returns true if they are currently anti-d
    private boolean refreshAnti(UUID uuid) {
        Instant instant = antiMap.get(uuid);

        if (instant == null)
            return false;

        Instant currentTime = Instant.now();
        boolean expired = instant.isBefore(currentTime);

        if (expired) {
            antiMap.remove(uuid);
        } else {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                long seconds = Duration.between(currentTime, instant).getSeconds();
                if (seconds > 0) {
                    String time = seconds > 1 ? "seconds" : "second";
                    player.sendMessage(ChatColor.RED + "You cannot interact with blocks for " +
                            ChatColor.BOLD + seconds + ChatColor.RED + " more " + time + ".");
                } else {
                    player.sendMessage(ChatColor.GREEN + "You can now interact with blocks!");
                }
            }
        }

        return !expired;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player entity = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

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

            if (antiMap.containsKey(entity.getUniqueId())) {
                attacker.sendMessage(ChatColor.RED + "That player is already anti-d!");
                return;
            }

            int hits = attackMap.getOrDefault(key, 0);

            if (++hits < HITS) {
                attackMap.put(key, hits);
                return;
            }

            attackMap.remove(key);

            antiMap.put(entity.getUniqueId(), Instant.now().plusSeconds(getAntiTime()));
            setCooldown(attacker);
            consume(attacker, item);

            sendActivationMessages(attacker,
                    new String[]{
                            "Successfully hit " + CC.MAIN + entity.getName() + CC.WHITE + " with an " + getName() + CC.WHITE + "!",
                            "That player can no longer interact with blocks!"
                    },
                    entity,
                    new String[]{
                            CC.MAIN + attacker.getName() + " has hit you " + CC.WHITE + " with an " + getName() + CC.WHITE + "!",
                            "You may not interact with blocks for " + getAntiTime() + " seconds!"
                    });
        }
    }

    // anti-d listeners
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBuild(BlockMultiPlaceEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setBuild(false);
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBreak(BlockBreakEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlaceBucket(PlayerBucketEmptyEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onInteract(PlayerInteractEvent event) {
        if (refreshAnti(event.getPlayer().getUniqueId())) {
            if (event.getClickedBlock() != null) {
                for (String material : this.materials) {
                    if (event.getClickedBlock().getType().name().contains(material)) {
                        event.setCancelled(true);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler // cleanup attack map
    public void onQuit(PlayerQuitEvent event) {
        attackMap.entrySet().removeIf(entry -> entry.getKey().first.equals(event.getPlayer().getUniqueId()));
    }

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 90L : 180L;
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.BONE)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fHit a player 3 times with this item",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fto prevent all block interactions",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f, breaking, and placing for 30 seconds.",
                        " "
                ).modelData(5).build();
    }

    @Override
    public ShapedRecipe getRecipe() {
        return null;
    }

    @Override
    public List<Material> getRecipeDisplay() {
        return null;
    }

    @Override
    public String getName() {
        return CC.translate("&g&lExotic Bone");
    }

    @Override
    public int getAmount() {
        return 1;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

    public int getAntiTime() {
        return SOTWCommand.isPartnerPackageHour() ? ANTI_BOOST_TIME : ANTI_TIME;
    }
}
