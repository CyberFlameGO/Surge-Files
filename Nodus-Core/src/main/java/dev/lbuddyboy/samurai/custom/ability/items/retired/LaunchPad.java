package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class LaunchPad extends AbilityItem {

    public LaunchPad() {
        super("LaunchPad");
    }

    private final List<UUID> rocket = new ArrayList<>();
    public static List<String> blockedMaterials = Arrays.asList("BEDROCK", "CHEST", "_GATE", "MOB_SPAWNER", "_DOOR", "WATER", "LAVA");

    @Override
    public long getCooldownTime() {
        return SOTWCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(3);
    }

    @Override
    public ItemStack partnerItem() {
        return ItemBuilder.of(Material.SLIME_BLOCK).data((short) 1)
                .name(getName())
                .addToLore(
                        " ",
                        CC.translate("&g&lDescription"),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click this to be place a",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fslime block below you, then if you",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fstep on it. You'll be launched in the air.",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fThis will despawn after 10 seconds.",
                        " ",
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &4&lBLOCKED BLOCKS&c: " + StringUtils.join(blockedMaterials.stream().map(String::toLowerCase).map(s -> WordUtils.capitalize(s.replace("_", ""))).collect(Collectors.toList()), "s, "),
                        " &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &cYou cannot use this ability on these blocks.",
                        " "
                ).build();
    }

    @Override
    public String getName() {
        return CC.translate("&g&lLaunch Pad");
    }

    @Override
    public int getAmount() {
        return 2;
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Location loc = player.getLocation();
        for (String mat : blockedMaterials) {
            if (loc.getBlock().getType().name().contains(mat)) {
                player.sendMessage(CC.translate("&cCould not use this ability item due to a blocked material being on your body."));
                return false;
            }
        }

        setGlobalCooldown(player);
        setCooldown(player);

        Material before = loc.getBlock().getType();
        BlockData blockData = loc.getBlock().getBlockData().clone();
        loc.getBlock().setType(Material.SLIME_BLOCK);
        loc.getBlock().setMetadata("launchpad", new FixedMetadataValue(Samurai.getInstance(), true));

        new BukkitRunnable() {
            @Override
            public void run() {
                loc.getBlock().removeMetadata("launchpad", Samurai.getInstance());
                loc.getBlock().setBlockData(blockData);
                loc.getBlock().setType(before);
            }
        }.runTaskLater(Samurai.getInstance(), 20 * 10);

        sendActivationMessages(player,
                new String[]{
                        "You have activated " + getName() + CC.WHITE + "!"
                }, null, null);
        return true;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())
            return;

        Player player = event.getPlayer();

        if (to.getBlock().getRelative(BlockFace.DOWN).hasMetadata("launchpad")) {

            player.setVelocity(new Vector(
                    player.getLocation().getDirection().getX() * 1.05,
                    player.getLocation().getY() * 2.45,
                    player.getLocation().getDirection().getZ() * 1.05));

            rocket.add(player.getUniqueId());

            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 200, 0));
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) {
            return;
        }

        if (!isPartnerItem(item))
            return;

        event.getPlayer().sendMessage(CC.translate("&cYou cannot place this block!"));
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageGuardianAngel(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (rocket.contains(event.getEntity().getUniqueId())) {
                Player player = (Player) event.getEntity();

                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {

                    rocket.remove(player.getUniqueId());
                    event.setCancelled(true);
                }
            }
        }
    }

}
