package dev.lbuddyboy.samurai.custom.feature.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.feature.FeatureHandler;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.command.GameCommand;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 1:37 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.feature.menu
 */
public class FeatureMenu extends Menu {

	public static final Feature[] VALUES = Feature.values();
	private final FeatureHandler featureHandler;

	public FeatureMenu() {
		this.featureHandler = Samurai.getInstance().getFeatureHandler();

		setAutoUpdate(true);
		setPlaceholder(true);
		setUpdateAfterClick(true);
	}

	@Override
	public String getTitle(Player player) {
		return "Server Features";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = 0;
		for (Feature feature : VALUES) {
			buttons.put(i++, new FeatureButton(feature, this.featureHandler));
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class FeatureButton extends Button {

		private Feature feature;
		private FeatureHandler featureHandler;

		@Override
		public String getName(Player player) {
			return CC.translate(this.feature.getDisplay());
		}

		@Override
		public List<String> getDescription(Player player) {

			List<String> lore = new ArrayList<>(Arrays.asList(
					" ",
					"&f" + this.feature.getDescription(),
					" "
			));

			if (this.featureHandler.isDisabled(this.feature)) {
				lore.add("&gStatus: &cDisabled");
			} else {
				lore.add("&gStatus: &aEnabled");
			}

			lore.add(" ");

			return CC.translate(lore);
		}

		@Override
		public Material getMaterial(Player player) {
			return Material.ANVIL;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			this.featureHandler.getDisabledFeatures().put(this.feature, !this.featureHandler.getDisabledFeatures().getOrDefault(this.feature, false));

			this.featureHandler.save();

			if (this.feature == Feature.END_WORLD) {
				for (Player other : Bukkit.getWorld("world_the_end").getPlayers()) {
					other.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
				}
			}
			if (this.feature == Feature.END_WORLD) {
				for (Player other : Bukkit.getWorld("world_nether").getPlayers()) {
					other.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
				}
			}
			if (this.feature == Feature.MINIGAME) {
				Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
				if (ongoingGame != null) {
					GameCommand.end(player);
				}
			}
		}
	}

}
