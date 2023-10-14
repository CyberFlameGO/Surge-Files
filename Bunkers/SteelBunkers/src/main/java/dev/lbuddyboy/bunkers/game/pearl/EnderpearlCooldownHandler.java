package dev.lbuddyboy.bunkers.game.pearl;

import dev.lbuddyboy.bunkers.Bunkers;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnderpearlCooldownHandler implements Listener {

	@Getter private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (event.getEntity() instanceof EnderPearl) {
			// Store the player's enderpearl in-case we need to remove it prematurely
			shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(Bunkers.getInstance(), event.getEntity()));

			// Get the default time to apply (in MS)
			long timeToApply = 16_000L;

			// Call our custom event (time to apply needs to be modifiable)
			EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
			Bunkers.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

			// Get the final time
			long finalTime = appliedEvent.getTimeToApply();

			// Put the player into the cooldown map
			enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + finalTime);
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) {
			return;
		}

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player thrower = (Player) event.getEntity().getShooter();

		if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
			long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

			double value = (millisLeft / 1000D);
			double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

			event.setCancelled(true);
			thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
			thrower.updateInventory();

			thrower.setCooldown(Material.ENDER_PEARL, 1);
		}
	}

	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
		        (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static void clearEnderpearlTimer(Player player) {
		enderpearlCooldown.remove(player.getName());
	}

	public static void resetEnderpearlTimer(Player player) {
		long duration = 16_000L;

		enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + duration);
	}

}