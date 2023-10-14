package dev.lbuddyboy.samurai.custom;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.kits.KitListener;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * a listener class that represents bootleg fixes to things out of our control
 */
public final class FixListener implements Listener {

	private static final List<String> SPAWN_COMMANDS = Arrays.asList(
			"pv", "vault", "chest"
	);
	private static final List<String> DISALLOWED_COMMANDS = Arrays.asList(
			"pv", "vault", "chest", "tinker", "tinkerer", "rename", "shardshop", "team hq", "t hq", "f hq", "faction hq", "fac hq", "team home", "t home", "f home",
			"pt", "pt rewards", "staff", "h", "staffmode", "staffm", "speed", "sp", "fr", "fres", "invis", "invisibility",
			"faction home", "fac home", "home", "hq", "gkit", "kit", "gkitz", "gkits", "kit", "spawn", "sotw spawn", "invis", "invisibility", "fireres", "fres"
	);

	private final List<DTRBitmask> bitmasks = Arrays.asList(
			DTRBitmask.KOTH,
			DTRBitmask.SAFE_ZONE,
			DTRBitmask.ROAD,
			DTRBitmask.CITADEL,
			DTRBitmask.CONQUEST
	);
	private static final Cooldown cooldown = new Cooldown();

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (!event.getMessage().equalsIgnoreCase("hub") && !event.getMessage().equalsIgnoreCase("lobby")) return;
		if (cooldown.onCooldown(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(CC.translate("&cYou can /hub again in " + cooldown.getRemaining(event.getPlayer())));
			return;
		}

		cooldown.applyCooldown(event.getPlayer().getUniqueId(), 30);
		cooldown.cleanUp();
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getItem().getType() != Material.SHIELD) return;
		if (Feature.DISABLE_SHIELDS.isDisabled()) return;

		event.setCancelled(true);
		event.getPlayer().sendMessage(CC.translate("&cShields are currently disabled."));
	}

	@EventHandler
	public void onFish(PlayerFishEvent event) {
		if (event.getCaught() == null) return;
		if (event.getCaught().getType() == EntityType.PLAYER) return;
		if (!DTRBitmask.SAFE_ZONE.appliesAt(event.getCaught().getLocation())) return;

		event.setCancelled(true);
		event.getPlayer().sendMessage(CC.translate("&cYou cannot fishing rod players in spawn."));
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent event) {
		if (event.getEntity().getWorld().getEnvironment() != World.Environment.NETHER) return;
		if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	private void onEnchant(EnchantItemEvent event) {
		Map<Enchantment, Integer> enchantsToAdd = event.getEnchantsToAdd();
		enchantsToAdd.entrySet().removeIf(entry -> entry.getKey().equals(Enchantment.KNOCKBACK));
	}

	@EventHandler
	private void onLeavesDecay(LeavesDecayEvent event) {
		for (DTRBitmask bitmask : bitmasks) {
			if (bitmask.appliesAt(event.getBlock().getLocation())) {
				event.setCancelled(true);
				break;
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPearl(PlayerInteractEvent event) {
		ItemStack item = event.getItem();
		if (item != null && item.getType() == Material.ENDER_PEARL) {
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				Location location = event.getPlayer().getLocation();
				if (DTRBitmask.CITADEL.appliesAt(location)) {
					if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(CC.RED + "You cannot use this in citadel.");
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPearl(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof EnderPearl && event.getEntity().getShooter() instanceof Player) {
			Player player = (Player) event.getEntity().getShooter();
			player.setMetadata("threwPearl", new FixedMetadataValue(Samurai.getInstance(), true));
			Location location = player.getLocation();
			if (DTRBitmask.CITADEL.appliesAt(location)) {
				if (player.getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
					player.sendMessage(CC.RED + "You cannot use this in citadel.");
				}
			}
		}
	}

	/*
	Pearl At ground does a shit load of fall damage
	 */

	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event) {
		if (event.getEntity() instanceof EnderPearl) {
			if (event.getEntity().getShooter() instanceof Player) {
				event.getEntity().setMetadata("pearled", new FixedMetadataValue(Samurai.getInstance(), true));
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			event.setDamage(event.getDamage() + (event.getDamage() / FoxtrotConfiguration.FALL_DAMAGE_MULTIPLIER.getDouble()));
			if (event.getEntity().hasMetadata("pearled")) {
				event.getEntity().removeMetadata("pearled", Samurai.getInstance());
				event.setDamage(event.getDamage() - 45.0D);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCombatCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		String command = event.getMessage().toLowerCase().replace("/", "");

/*		if (Samurai.getInstance().getMapHandler().isKitMap()) {
			if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
				for (String cmd : SPAWN_COMMANDS) {
					if (command.contains(cmd)) {
						event.setCancelled(true);
						player.sendMessage(ChatColor.RED + "You can only do this at spawn.");
						break;
					}
				}
				return;
			}
		}*/

		if (player.hasMetadata("gaming")) {
			if (command.contains(":")) {
				player.sendMessage(CC.translate("&cThat command is not allowed whilst in a game."));
				event.setCancelled(true);
				return;
			}
			for (String cmd : DISALLOWED_COMMANDS) {
				if (command.contains(cmd)) {
					event.setCancelled(true);
					player.sendMessage(CC.translate("&cThat command is not allowed whilst in a game."));
					break;
				}
			}
		}
	}

	@EventHandler
	private void onCommand(PlayerCommandPreprocessEvent event) {
		String message = event.getMessage().toLowerCase();

		boolean slashKit = message.startsWith("/kit");
		if (slashKit || message.startsWith("/gkit") || message.startsWith("/gkits") || message.startsWith("/gkitz")) {
			Location location = event.getPlayer().getLocation();
			if (DTRBitmask.CITADEL.appliesAt(location) || DTRBitmask.KOTH.appliesAt(location)) {
				if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(CC.RED + "You cannot use kits in citadel/koth.");
				}
			}
		}
	}

}
