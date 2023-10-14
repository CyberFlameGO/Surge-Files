package dev.lbuddyboy.bunkers.spectator;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 11:14 PM
 * SteelBunkers / com.steelpvp.bunkers.spectator
 */

@Getter
public class SpectatorHandler {

	private static final String METADATA = "spectator";

	private final ItemStack[] items;

	public SpectatorHandler() {
		this.items = new ItemStack[]{
				ItemBuilder.of(Material.PLAYER_HEAD).name("&ePlayer List").build(),
				ItemBuilder.of(Material.CLOCK).name("&eRandom Teleport").build()
		};
		Bukkit.getPluginManager().registerEvents(new SpectatorListener(), Bunkers.getInstance());
	}

	public void enable(Player player) {
		player.setMetadata(METADATA, new FixedMetadataValue(Bunkers.getInstance(), true));
		player.getInventory().setContents(this.items);
		player.setAllowFlight(true);

		for (Player target : Bukkit.getOnlinePlayers()) {
			target.hidePlayer(Bunkers.getInstance(), player);
		}

	}

	public void disable(Player player) {
		player.removeMetadata(METADATA, Bunkers.getInstance());
		player.getInventory().clear();
		player.setAllowFlight(false);
	}

	public boolean isSpectator(Player player) {
		return player.hasPermission(METADATA);
	}

}
