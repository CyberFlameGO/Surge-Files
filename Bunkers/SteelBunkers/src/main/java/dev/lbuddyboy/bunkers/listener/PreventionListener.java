package dev.lbuddyboy.bunkers.listener;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.game.ore.OreBreak;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.team.Team;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 20/03/2022 / 10:04 AM
 * SteelBunkers / com.steelpvp.bunkers.listener
 */
public class PreventionListener implements Listener {

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.WAITING) {
			event.setCancelled(!event.getPlayer().isOp());
			return;
		}

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		Block clicked = event.getClickedBlock();

		if (clicked == null) return;
		if (!clicked.getType().isInteractable()) return;

		Team teamAt = Bunkers.getInstance().getTeamHandler().getTeam(clicked.getLocation());

		if (teamAt == null) {
			event.setUseInteractedBlock(Event.Result.DENY);
			return;
		}

		if (teamAt.getLocations().containsKey(clicked.getType())) {
			if (teamAt.getLocations().get(clicked.getType()).contains(clicked.getLocation())) {
				return;
			}
		}

		if (!Bunkers.getInstance().getGameHandler().getPlacedBlocks().contains(clicked.getLocation())) {
			event.setUseInteractedBlock(Event.Result.DENY);
			return;
		}

		if (teamAt.isRaidable()) return;
		if (teamAt.getMembers().contains(player.getUniqueId())) return;

		event.setUseInteractedBlock(Event.Result.DENY);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {

		Player player = event.getPlayer();
		Block clicked = event.getBlock();

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.WAITING) {
			event.setCancelled(!event.getPlayer().isOp());
			return;
		}

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}


		Team teamAt = Bunkers.getInstance().getTeamHandler().getTeam(clicked.getLocation());

		if (teamAt == null) {
			event.setCancelled(true);
			return;
		}

		if (teamAt.getLocations().containsKey(clicked.getType())) {
			if (teamAt.getLocations().get(clicked.getType()).contains(clicked.getLocation())) {
				event.setCancelled(true);

				Bunkers.getInstance().getGameHandler().getOreBreaks().add(new OreBreak(clicked.getType(), clicked.getLocation(), System.currentTimeMillis() + 8_000L));
				if (clicked.getType() == Material.IRON_ORE || clicked.getType() == Material.GOLD_ORE) {
					event.setDropItems(false);
				}
				clicked.getWorld().dropItemNaturally(clicked.getLocation(), getEqual(clicked.getType()));
				GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());
				user.setOresMined(user.getOresMined() + 1);

				clicked.setType(Material.COBBLESTONE);
				return;
			}
		}

		if (!Bunkers.getInstance().getGameHandler().getPlacedBlocks().contains(clicked.getLocation())) {
			event.setCancelled(true);
			return;
		}

		if (teamAt.isRaidable()) return;
		if (teamAt.getMembers().contains(player.getUniqueId())) return;

		event.setCancelled(true);

	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.WAITING) {
			event.setCancelled(!event.getPlayer().isOp());
			return;
		}

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		Player player = event.getPlayer();
		Block clicked = event.getBlockPlaced();

		Team teamAt = Bunkers.getInstance().getTeamHandler().getTeam(clicked.getLocation());

		if (teamAt == null) {
			event.setCancelled(true);
			return;
		}

		if (teamAt.isRaidable()) {
			Bunkers.getInstance().getGameHandler().getPlacedBlocks().add(clicked.getLocation());
			return;
		}

		if (teamAt.getMembers().contains(player.getUniqueId())) {
			Bunkers.getInstance().getGameHandler().getPlacedBlocks().add(clicked.getLocation());
			return;
		}

		event.setCancelled(true);

	}

	@EventHandler
	public void onSpawn(CreatureSpawnEvent event) {
		if (event.getEntityType() == EntityType.VILLAGER) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDamager() instanceof Player)) return;

		Player attacker = (Player) event.getDamager();
		Player damaged = (Player) event.getEntity();

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(attacker);

		event.setCancelled(true);

		if (attacker.hasMetadata("spectator")) return;
		if (team == null) return;
		if (team.getMembers().contains(damaged.getUniqueId())) return;

		event.setCancelled(false);

	}

	@EventHandler
	public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof Player)) return;
		if (!(event.getDamager() instanceof Projectile)) return;
		if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

		Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
		if (attacker == null) return;
		Player damaged = (Player) event.getEntity();

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(attacker);

		event.setCancelled(true);

		if (attacker.hasMetadata("spectator")) return;
		if (team == null) return;
		if (team.getMembers().contains(damaged.getUniqueId())) return;

		event.setCancelled(false);

	}

	public ItemStack getEqual(Material ore) {
		if (ore == Material.GOLD_ORE) {
			return new ItemStack(Material.GOLD_INGOT, 3);
		} else if (ore == Material.IRON_ORE) {
			return new ItemStack(Material.IRON_INGOT, 3);
		} else if (ore == Material.DIAMOND_ORE) {
			return new ItemStack(Material.DIAMOND, 3);
		} else if (ore == Material.COAL_ORE) {
			return new ItemStack(Material.COAL, 3);
		}
		return null;
	}

}
