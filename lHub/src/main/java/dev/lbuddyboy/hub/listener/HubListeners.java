package dev.lbuddyboy.hub.listener;

import dev.lbuddyboy.flash.Flash;
import dev.lbuddyboy.flash.FlashLanguage;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 1:27 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.listener
 */
public class HubListeners implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (FlashLanguage.JAIL_SERVER.getBoolean() && Flash.getInstance().getJailHandler().isInJail(event.getPlayer())) {
            if (event.getClickedBlock().getType() == Material.getMaterial(FlashLanguage.JAIL_BREAK_MATERIAL.getString()))
                return;
        }

        event.setCancelled(!lHub.getInstance().getSettingsHandler().getBuildModes().contains(event.getPlayer()));
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        event.setCancelled(!lHub.getInstance().getSettingsHandler().getBuildModes().contains(event.getPlayer()));
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (FlashLanguage.JAIL_SERVER.getBoolean() && Flash.getInstance().getJailHandler().isInJail(player)) {
            if (block.getType() == Material.getMaterial(FlashLanguage.JAIL_BREAK_MATERIAL.getString())) return;
        }

        event.setCancelled(!lHub.getInstance().getSettingsHandler().getBuildModes().contains(event.getPlayer()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (lHub.getInstance().getConfig().getBoolean("join-message.enabled")) {
            for (String message : lHub.getInstance().getConfig().getStringList("join-message.message")) {
                player.sendMessage(CC.translate(message
                        .replaceAll("%player%", player.getDisplayName())
                ));
            }
        }

        if (!FlashLanguage.JAIL_SERVER.getBoolean()) {
            for (Player online : Bukkit.getOnlinePlayers()) {
                online.hidePlayer(player);
                player.hidePlayer(online);
            }
        }

        event.setJoinMessage(null);
        player.teleport(lHub.getInstance().getSettingsHandler().getSpawnLocation());
    }

/*	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() != GameMode.CREATIVE
				&& player.getLocation().subtract(0.0, 0.1, 0.0).getBlock().getType() != Material.AIR
				&& !player.isFlying()) {
			player.setAllowFlight(true);
		}
	}*/

/*	@EventHandler
	public void onFly(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();
		if (lHub.getInstance().getSettingsHandler().isDoubleJump()) {
			if (player.getGameMode() == GameMode.CREATIVE) return;

			event.setCancelled(true);
			player.setAllowFlight(false);
			player.setFlying(false);
			player.setVelocity(player.getLocation().getDirection().multiply(lHub.getInstance().getSettingsHandler().getDoubleJumpMultiplier()).setY(1));
			if (lHub.getInstance().getSettingsHandler().getDoubleJumpSound() != null) {
				player.playSound(player.getLocation(), lHub.getInstance().getSettingsHandler().getDoubleJumpSound(), 2.0f, 1.0f);
			}
		}
	}*/

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
        event.getPlayer().getInventory().clear();
        event.getPlayer().removeMetadata("pvpmode", lHub.getInstance());
    }

    @EventHandler
    public void onMobspawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onMobTarget(EntityTargetEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeather(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInvInter(InventoryClickEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE && !event.getWhoClicked().hasMetadata("pvpmode")) {
            event.setCancelled(false);
            return;
        }
        event.setCancelled(!event.getWhoClicked().hasMetadata("pvpmode"));
    }

    @EventHandler
    public void onFoodLevel(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInvInter(InventoryDragEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE && !event.getWhoClicked().hasMetadata("pvpmode")) {
            event.setCancelled(false);
            return;
        }
        event.setCancelled(!event.getWhoClicked().hasMetadata("pvpmode"));
    }

    @EventHandler
    public void onInvInter(InventoryInteractEvent event) {
        if (event.getWhoClicked().getGameMode() == GameMode.CREATIVE && !event.getWhoClicked().hasMetadata("pvpmode")) {
            event.setCancelled(false);
            return;
        }
        event.setCancelled(!event.getWhoClicked().hasMetadata("pvpmode"));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!event.getEntity().hasMetadata("pvpmode")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player sender = event.getPlayer();
        if (event.getPlayer().hasMetadata("pvpmode")) {
            sender.removeMetadata("pvpmode", lHub.getInstance());
            sender.sendMessage(CC.translate("&cPvP Mode is now disabled."));
            lHub.getInstance().getItemHandler().setItems(sender);
            event.setRespawnLocation(lHub.getInstance().getSettingsHandler().getSpawnLocation());
            for (Player online : Bukkit.getOnlinePlayers()) {
                if (!online.hasMetadata("pvpmode")) continue;

                online.hidePlayer(sender);
                sender.hidePlayer(online);
            }
        }
    }

    @EventHandler
    public void onMoveVoid(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location loc = player.getLocation();

        if (lHub.getInstance().getSettingsHandler().isAntiVoid()) {
            if (loc.getBlockY() <= lHub.getInstance().getSettingsHandler().getAntiVoidHeight()) {
                player.setVelocity(player.getVelocity().add(new Vector(0, lHub.getInstance().getSettingsHandler().getSpawnLocation().getY(), 0)));
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getDamager() instanceof Player)) return;

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (attacker.hasMetadata("pvpmode") && victim.hasMetadata("pvpmode")) return;

        event.setCancelled(true);
    }

}
