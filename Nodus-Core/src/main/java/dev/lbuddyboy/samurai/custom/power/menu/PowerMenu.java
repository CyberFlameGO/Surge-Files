package dev.lbuddyboy.samurai.custom.power.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PowerMenu extends Menu {
	@Override
	public String getTitle(Player player) {
		return "Power Selection";
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return 27;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		// 11 13 15
		Map<Integer, Button> buttons = new HashMap<>();
		buttons.put(11, getStrength());
		buttons.put(13, getTrapper());
		buttons.put(15, getMixture());
		return buttons;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	private Button getMixture() {
		return new Button() {
			@Override
			public String getName(Player player) {
				return null;
			}

			@Override
			public List<String> getDescription(Player player) {
				return null;
			}

			@Override
			public Material getMaterial(Player player) {
				return null;
			}

			@Override
			public ItemStack getButtonItem(Player player) {
				return ItemBuilder.of(Material.BOOK).name(CC.translate("&3&lMixture Power"))
						.setLore(CC.translate(Arrays.asList(
								"",
								"&7Click to choose the &3Mixture Power&7.",
								"",
								"&3&lDescription",
								"&7Once shift right clicking you will receive one of the two effects.",
								"&3Absorption 3 or Strength 2&7. The time of these effects can vary",
								"&7from your booster. Your default time is the positive",
								"&7effect is going to give you &38 seconds&7 & the negative is &35 seconds&7."
						)))
						.build();
			}

			@SneakyThrows
			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				Samurai.getInstance().getPowerHandler().getPowerMap().setPower(player.getUniqueId(), "Mixture");
				player.closeInventory();
			}
		};
	}
	private Button getTrapper() {
		return new Button() {
			@Override
			public String getName(Player player) {
				return null;
			}

			@Override
			public List<String> getDescription(Player player) {
				return null;
			}

			@Override
			public Material getMaterial(Player player) {
				return null;
			}

			@Override
			public ItemStack getButtonItem(Player player) {
				return ItemBuilder.of(Material.GRASS)
						.setLore(CC.translate(Arrays.asList(
								"",
								"&7Click to choose the &dTrapper Power&7.",
								"",
								"&d&lDescription",
								"&7Once shift right clicking you will receive one of the two effects.",
								"&7&dResistance 3 or Poison 1&7. The time of these effects can vary",
								"&7from your booster. Your default time is the positive",
								"&7effect is going to give you &d8 seconds&7 & the negative is &d5 seconds&7."
						)))
						.name(CC.translate("&d&lTrapper Power")).build();
			}

			@SneakyThrows
			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				Samurai.getInstance().getPowerHandler().getPowerMap().setPower(player.getUniqueId(), "Trapper");
				player.closeInventory();
			}
		};
	}
	private Button getStrength() {
		return new Button() {
			@Override
			public String getName(Player player) {
				return null;
			}

			@Override
			public List<String> getDescription(Player player) {
				return null;
			}

			@Override
			public Material getMaterial(Player player) {
				return null;
			}

			@Override
			public ItemStack getButtonItem(Player player) {
				return ItemBuilder.of(Material.BLAZE_POWDER).name(CC.translate("&4&lAggressive Power"))
						.setLore(CC.translate(Arrays.asList(
								"",
								"&7Click to choose the &4Aggressive Power&7.",
								"",
								"&4&lDescription",
								"&7Once shift right clicking you will receive one of the two effects.",
								"&4Strength 2 or Speed 3&7. The time of these effects can vary",
								"&7from your booster. Your default time is the positive",
								"&7effect is going to give you &48 seconds&7 & the negative is &45 seconds&7."
						))).build();
			}

			@SneakyThrows
			@Override
			public void clicked(Player player, int slot, ClickType clickType) {
				Samurai.getInstance().getPowerHandler().getPowerMap().setPower(player.getUniqueId(), "Strength");
				player.closeInventory();
			}
		};
	}
}
