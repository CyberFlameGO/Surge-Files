package dev.lbuddyboy.bunkers.spectator;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.GameState;
import dev.lbuddyboy.bunkers.spectator.menu.PlayerListMenu;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 20/03/2022 / 8:20 PM
 * SteelBunkers / com.steelpvp.bunkers.spectator
 */
public class SpectatorListener implements Listener {

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {

		Player player = event.getPlayer();
		ItemStack stack = event.getItem();

		if (!player.hasMetadata("spectator")) return;
		if (stack == null) return;

		ItemStack playerList = Bunkers.getInstance().getSpectatorHandler().getItems()[0];
		ItemStack randomTP = Bunkers.getInstance().getSpectatorHandler().getItems()[1];

		if (playerList.isSimilar(stack)) {
			new PlayerListMenu().openMenu(player);
			return;
		}

		if (randomTP.isSimilar(stack)) {
			List<Player> players = Bukkit.getOnlinePlayers().stream().filter(p -> !p.hasMetadata("spectator")).collect(Collectors.toList());
			player.teleport(players.get(new Random().nextInt(players.size())));
			return;
		}

		event.setCancelled(true);

	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (event.getPlayer().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.getPlayer().setFoodLevel(20);
			event.getPlayer().setHealth(20);
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.getPlayer().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDrop(InventoryClickEvent event) {
		if (event.getWhoClicked().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onLose(FoodLevelChangeEvent event) {
		if (event.getEntity().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onPIkcup(PlayerPickupItemEvent event) {
		if (event.getPlayer().hasMetadata("spectator") || Bunkers.getInstance().getGameHandler().getState() != GameState.ACTIVE) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().hasMetadata("spectator")) {
			event.setCancelled(true);
		}
	}

}
