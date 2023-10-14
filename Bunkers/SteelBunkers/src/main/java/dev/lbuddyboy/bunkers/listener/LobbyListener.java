package dev.lbuddyboy.bunkers.listener;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.Callback;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import dev.lbuddyboy.bunkers.util.cooldown.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 10:38 PM
 * SteelBunkers / com.steelpvp.bunkers.listener
 */
public class LobbyListener implements Listener {

	private static final Map<ItemStack, Callback<Player>> LOBBY_ITEMS = new HashMap<>();
	private static final Cooldown CLICK_COOLDOWN = new Cooldown();

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(null);

		Player player = event.getPlayer();
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(player.getUniqueId());

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.WAITING || Bunkers.getInstance().getGameHandler().getState() == GameState.COUNTING) {
			player.getInventory().clear();
			for (ItemStack stack : LOBBY_ITEMS.keySet()) {
				player.getInventory().addItem(stack);
			}
			player.teleport(event.getPlayer().getWorld().getSpawnLocation());
			player.setLevel(0);
			player.setFoodLevel(20);
			player.setHealth(20);
			player.setExp(0);
			for (PotionEffect pe : player.getActivePotionEffects()) {
				player.removePotionEffect(pe.getType());
			}
			player.setTotalExperience(0);
		} else {
			if (user.isEligibleRejoin()) {
				if (user.isLoggerDied()) {
					Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);

					if (team.isRaidable()) {
						player.teleport(event.getPlayer().getWorld().getSpawnLocation());
						Bunkers.getInstance().getSpectatorHandler().enable(event.getPlayer());
						player.sendMessage(CC.translate("&cIn your absence your team went raidable, so you've been put to spectator mode"));
						return;
					}

					player.getInventory().clear();
					player.teleport(team.getHome());
				}
				Bukkit.broadcastMessage(CC.translate("&6" + player.getName() + "&a has just rejoined the game!"));
				return;
			}
			event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
			Bunkers.getInstance().getSpectatorHandler().enable(event.getPlayer());
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {

		if (Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
			return;
		}

		if (Bunkers.getInstance().getGameHandler().getState() == GameState.ACTIVE) return;
		event.setCancelled(true);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage(null);

		if (!event.getPlayer().hasMetadata("spectator")) {
			GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(event.getPlayer().getUniqueId());
			user.setLeft(System.currentTimeMillis());
		}

	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();

		if (!event.getAction().name().contains("RIGHT_")) return;

		ItemStack stack = event.getItem();
		if (stack == null) return;

		for (Map.Entry<ItemStack, Callback<Player>> entry : LOBBY_ITEMS.entrySet()) {
			if (entry.getKey().isSimilar(stack)) {
				if (CLICK_COOLDOWN.onCooldown(player)) return;

				entry.getValue().callback(player);
				event.setCancelled(true);

				CLICK_COOLDOWN.applyCooldown(player, 1);
			}
		}

	}

	static {
		LOBBY_ITEMS.put(ItemBuilder.of(Material.RED_WOOL).name("&cRed Team &7(Click to Join)").build(), (player) -> {
			Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);
			Team red = Bunkers.getInstance().getTeamHandler().getTeam("Red");
			if (red.getOnlineMembers().size() >= 5) {
				player.sendMessage(CC.translate("&cThat team is full!"));
				return;
			}
			if (team == null) {
				red.addPlayer(player);
				return;
			}
			team.removePlayer(player);
			red.addPlayer(player);
		});
		LOBBY_ITEMS.put(ItemBuilder.of(Material.BLUE_WOOL).name("&9Blue Team &7(Click to Join)").build(), (player) -> {
			Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);
			Team blue = Bunkers.getInstance().getTeamHandler().getTeam("Blue");
			if (blue.getOnlineMembers().size() >= 5) {
				player.sendMessage(CC.translate("&cThat team is full!"));
				return;
			}
			if (team == null) {
				blue.addPlayer(player);
				return;
			}
			team.removePlayer(player);
			blue.addPlayer(player);
		});
		LOBBY_ITEMS.put(ItemBuilder.of(Material.YELLOW_WOOL).name("&eYellow Team &7(Click to Join)").build(), (player) -> {
			Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);
			Team yellow = Bunkers.getInstance().getTeamHandler().getTeam("Yellow");
			if (yellow.getOnlineMembers().size() >= 5) {
				player.sendMessage(CC.translate("&cThat team is full!"));
				return;
			}
			if (team == null) {
				yellow.addPlayer(player);
				return;
			}
			team.removePlayer(player);
			yellow.addPlayer(player);
		});
		LOBBY_ITEMS.put(ItemBuilder.of(Material.GREEN_WOOL).name("&aGreen Team &7(Click to Join)").build(), (player) -> {
			Team team = Bunkers.getInstance().getTeamHandler().getTeam(player);
			Team green = Bunkers.getInstance().getTeamHandler().getTeam("Green");
			if (green.getOnlineMembers().size() >= 5) {
				player.sendMessage(CC.translate("&cThat team is full!"));
				return;
			}
			if (team == null) {
				green.addPlayer(player);
				return;
			}
			team.removePlayer(player);
			green.addPlayer(player);
		});
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.getPlayer().hasMetadata("spectator")) {
			event.setCancelled(true);
			for (Player player : Bukkit.getOnlinePlayers()) {
				player.sendMessage(CC.translate("&7[S] &g" + event.getPlayer().getName() + "&7: &f" + event.getMessage()));
			}
			return;
		}
		if (event.getMessage().startsWith("@") || event.getPlayer().hasMetadata("teamchat")) {
			event.setCancelled(true);
			Team team = Bunkers.getInstance().getTeamHandler().getTeam(event.getPlayer());
			for (Player player : team.getOnlineMembers()) {
				player.sendMessage(CC.translate("&g&l[TEAM CHAT] &g" + event.getPlayer().getName() + "&7: &f" + event.getMessage().replaceAll("@", "")));
			}
			return;
		}
		event.setCancelled(true);
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(event.getPlayer());
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (team != null) {
				player.sendMessage(CC.translate("&7[" + team.getDisplay() + "&7] " + team.getColor() + event.getPlayer().getName() + "&7: &f" + event.getMessage()));
			} else {
				player.sendMessage(CC.translate("&7[*] &g" + event.getPlayer().getName() + "&7: &f" + event.getMessage()));
			}
		}
	}

}
