package dev.lbuddyboy.bunkers.game.listener;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.event.GameEndEvent;
import dev.lbuddyboy.bunkers.event.GameStartEvent;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.game.shop.Shop;
import dev.lbuddyboy.bunkers.game.shop.menu.RepairMenu;
import dev.lbuddyboy.bunkers.game.task.BalanceTask;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.InventoryUtils;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.WorldUtils;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import net.shadowxcraft.rollbackcore.Copy;
import net.shadowxcraft.rollbackcore.Paste;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 10:12 PM
 * SteelBunkers / com.steelpvp.bunkers.game.listener
 */
public class GameListener implements Listener {

	private static final List<ItemStack> DEFAULT_GAME_INVENTORY = Arrays.asList(
			ItemBuilder.of(Material.STONE_PICKAXE).build(),
			ItemBuilder.of(Material.STONE_AXE).build()
	);
	private static final int DELAY_BALANCE_INCREMENT = 5;
	private static final double BALANCE_INCREMENT_AMOUNT = 8.5;

	@EventHandler
	public void onEnd(GameEndEvent event) {
		if (Bunkers.getInstance().getGameHandler().getGameSettings().getBounds() != null) {
			new Paste(Bunkers.getInstance().getGameHandler().getGameSettings().getBounds().getLowerNE(),
					Bunkers.getInstance().getDataFolder() + "/arenas/Bunker-Map",
					Bukkit.getConsoleSender(), true /*Clear entities*/, false /*Don't ignore air*/, "[Arena Restorer] ")
					.run();
		}
	}

	@EventHandler
	public void onGameStart(GameStartEvent event) {
		Bunkers.getInstance().getGameHandler().setState(GameState.ACTIVE);
		if (Bunkers.getInstance().getGameHandler().getGameSettings().getBounds() != null) {
			new Copy(Bunkers.getInstance().getGameHandler().getGameSettings().getBounds().getLowerNE(),
					Bunkers.getInstance().getGameHandler().getGameSettings().getBounds().getUpperSW(),
					Bunkers.getInstance().getDataFolder() + "/arenas/Bunker-Map",
					Bukkit.getConsoleSender(), "[Arena Save] ").run();
		}

		for (Player player : Bukkit.getOnlinePlayers()) {
			player.getInventory().clear();

			Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);
			if (team == null) {
				player.sendMessage(CC.translate("&cYou didn't choose a team, so we have put you in to spectator mode."));
				Bunkers.getInstance().getSpectatorHandler().enable(player);
				continue;
			}

			for (ItemStack stack : DEFAULT_GAME_INVENTORY) {
				player.getInventory().addItem(stack);
			}

			player.teleport(team.getHome());
			player.setNoDamageTicks(20 * 15);
			player.setHealth(20);
			player.setFoodLevel(20);

			WorldUtils.butcher();

			player.sendTitle(CC.translate("&g&lGAME STARTED"), CC.translate("&fDon't forget to place your fence gates."));
			Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), () -> {
				player.sendTitle(CC.translate("&g&lTIP"), CC.translate("&fGo down to your mines located below your teams home."));
				Bukkit.getScheduler().runTaskLater(Bunkers.getInstance(), () -> {
					player.sendTitle(CC.translate("&g&lTIP"), CC.translate("&fSell those ores at the sell shop villager."));
				}, 35);
			}, 35);

			if (team.getHome() != null) {
				LCWaypoint waypoint = new LCWaypoint("HQ", team.getHome(), Color.blue.hashCode(), true);
				LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
			}

			for (Team other : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
				if (other == team) continue;
				if (other.getHome() == null) continue;
				Color color = (other.getColor() == ChatColor.YELLOW ? Color.yellow : other.getColor() == ChatColor.GREEN ? Color.green : other.getColor() == ChatColor.RED ? Color.red : Color.blue);
				LCWaypoint waypoint = new LCWaypoint(other.getName(), other.getHome(), color.hashCode(), true);
				LunarClientAPI.getInstance().sendWaypoint(player, waypoint);
			}

			LCWaypoint waypoint = new LCWaypoint(CC.translate(Bunkers.getInstance().getGameHandler().getGameSettings().getKothName()), player.getWorld().getSpawnLocation(), Color.orange.hashCode(), true);
			LunarClientAPI.getInstance().sendWaypoint(player, waypoint);

		}

		Bunkers.getInstance().getGameHandler().setStartedAt(System.currentTimeMillis());

		for (Team team : Bunkers.getInstance().getTeamHandler().getTeams().values()) {
			team.resetClaim();

			if (team.getColor() == ChatColor.LIGHT_PURPLE) continue;

			for (Shop shop : team.getShops()) {
				shop.spawn();
			}
		}

		new BalanceTask(BALANCE_INCREMENT_AMOUNT).runTaskTimerAsynchronously(Bunkers.getInstance(), 20 * DELAY_BALANCE_INCREMENT, 20 * DELAY_BALANCE_INCREMENT);
	}

	@EventHandler
	public void onInteract(PlayerInteractEntityEvent event) {
		Entity entity = event.getRightClicked();
		if (entity.getCustomName() == null) return;
		if (!(entity instanceof Villager)) return;
		if (entity.hasMetadata("dead")) return;

		Villager villager = (Villager) entity;

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(villager.getLocation());

		if (Bunkers.getInstance().getSpectatorHandler().isSpectator(event.getPlayer())) {
			event.getPlayer().sendMessage(CC.translate("&cYou cannot do this whilst spectating."));
			return;
		}

		Shop shop = team.getShop(villager.getVillagerType());
		shop.openMenu(event.getPlayer());
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (Bunkers.getInstance().getGameHandler().isGracePeriod()) {
			event.setCancelled(true);
		}
		if (event.getEntity().hasMetadata("dead")) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

		if (event.getEntity().hasMetadata("dead")) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof Villager)) return;
		if (!(event.getDamager() instanceof Player)) return;

		Player attacker = (Player) event.getDamager();
		Villager damaged = (Villager) event.getEntity();

		Team teamAt = Bunkers.getInstance().getTeamHandler().getTeam(damaged.getLocation());

		event.setCancelled(true);

		if (teamAt == null) return;
		if (teamAt.getMembers().contains(attacker.getUniqueId())) return;

		event.setCancelled(false);

	}

	@EventHandler
	public void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {

		if (event.getEntity().hasMetadata("dead")) {
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof Villager)) return;
		if (!(event.getDamager() instanceof Projectile)) return;
		if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) return;

		Player attacker = (Player) ((Projectile) event.getDamager()).getShooter();
		if (attacker == null) return;

		Villager damaged = (Villager) event.getEntity();

		Team teamAt = Bunkers.getInstance().getTeamHandler().getTeam(damaged.getLocation());

		event.setCancelled(true);

		if (teamAt == null) return;
		if (teamAt.getMembers().contains(attacker.getUniqueId())) return;

		event.setCancelled(false);

	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {

		Player player = event.getEntity();
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);

		if (team == null) {
			event.getDrops().clear();
			event.setDeathMessage(null);
			return;
		}
		if (event.getDeathMessage() == null) return;

		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		user.setDeaths(user.getDeaths() + 1);
		team.setDtr(team.getDtr() - 1);

		String raw = event.getDeathMessage().replaceAll(player.getName(), "");
		if (player.getKiller() != null) {
			raw = event.getDeathMessage().replaceAll(player.getName(), "").replaceAll(player.getKiller().getName(), "%killer%");
		}
		String finalMsg = CC.translate("&g" + player.getName() + "&7[" + user.getKills() + "]&e" + raw);
		if (player.getKiller() != null) {
			GameUser killerUser = Bunkers.getInstance().getGameHandler().getGameUser(player.getKiller().getUniqueId());
			killerUser.setKills(killerUser.getKills() + 1);
			finalMsg = CC.translate("&g" + player.getName() + "&7[" + user.getKills() + "]&e" + raw.replaceAll("%killer%", "&g" + player.getKiller().getName() + "&7[" + killerUser.getKills() + "]&e"));
		}

		event.setDeathMessage(finalMsg);

	}

	@EventHandler
	public void onDeath(EntityDeathEvent event) {
		if ((event.getEntity().getType() != EntityType.VILLAGER)) return;
		Villager villager = (Villager) event.getEntity();

		Team team = Bunkers.getInstance().getTeamHandler().getTeam(villager.getLocation());

		Shop shop = team.getShop(villager.getVillagerType());
		shop.kill();
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(event.getPlayer());
		if (team == null) return;
		event.setRespawnLocation(team.getHome());

		if (team.getDtr() <= 0) {
			Bukkit.getScheduler().runTask(Bunkers.getInstance(), () -> {
				Bunkers.getInstance().getSpectatorHandler().enable(event.getPlayer());
			});
		} else {
			event.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_PICKAXE));
			event.getPlayer().getInventory().addItem(new ItemStack(Material.STONE_AXE));
		}
	}

	@EventHandler
	public void onDrink(PlayerItemConsumeEvent event) {
		if (event.getItem().isSimilar(InventoryUtils.ANTIDOTE)) {
			InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), event.getItem(), 1);
			for (PotionEffect pot : event.getPlayer().getActivePotionEffects()) {
				if (pot.getType() != PotionEffectType.POISON || pot.getType() != PotionEffectType.BLINDNESS || pot.getType() != PotionEffectType.CONFUSION || pot.getType() != PotionEffectType.SLOW) {
					event.getPlayer().removePotionEffect(pot.getType());
				}
			}
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {

		Player player = event.getPlayer();

		Location from = event.getFrom();
		Location to = event.getTo();
		if (from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) {
			Team toTeam = Bunkers.getInstance().getTeamHandler().getTeam(to);
			Team fromTeam = Bunkers.getInstance().getTeamHandler().getTeam(from);

			if (fromTeam == null && toTeam != null) {
				player.sendMessage(CC.translate("&fEntering: " + toTeam.getDisplay() + ", &fLeaving: &cWarzone"));
			}

			if (toTeam == null && fromTeam != null) {
				player.sendMessage(CC.translate("&fEntering: &cWarzone" + ", &fLeaving: " + fromTeam.getDisplay()));
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Block clicked = event.getClickedBlock();
		if (clicked == null) return;
		if (clicked.getType() != Material.ANVIL) return;

		new RepairMenu().openMenu(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if (Bunkers.getInstance().getGameHandler().getState() == GameState.STARTING) return;
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(event.getPlayer());
		if (team == null) return;
		team.removePlayer(event.getPlayer());
	}

}
