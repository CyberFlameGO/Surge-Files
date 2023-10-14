package dev.lbuddyboy.samurai.server.menu;

import dev.lbuddyboy.samurai.custom.airdrop.menu.AirdropLootMenu;
import dev.lbuddyboy.samurai.custom.supplydrops.menu.SupplyCrateMenu;
import dev.lbuddyboy.samurai.custom.vaults.menu.VaultMenu;
import dev.lbuddyboy.samurai.server.menu.buttons.LootTableButton;
import dev.lbuddyboy.samurai.server.menu.buttons.LootTablesButton;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.loottable.menu.LootTablePreviewMenu;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 26/02/2022 / 2:37 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.server.menu
 */

public class LootTableMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&7All Loot Tables");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(11, new LootTableButton(new Menu() {

			@Override
			public Map<Integer, Button> getButtons(Player player) {
				Map<Integer, Button> buttons = new HashMap<>();

				buttons.put(4, new BackButton(new LootTableMenu()));

				int i = 8;
				for (ItemStack stack : Samurai.getInstance().getCitadelHandler().getCitadelLoot()) {
					buttons.put(++i, new Button() {
						@Override
						public String getName(Player var1) {
							return null;
						}

						@Override
						public List<String> getDescription(Player var1) {
							return null;
						}

						@Override
						public Material getMaterial(Player var1) {
							return null;
						}

						@Override
						public ItemStack getButtonItem(Player player) {
							return ItemBuilder.copyOf(stack).addToLore(" ").build();
						}
					});
				}

				return buttons;
			}

			@Override
			public boolean isPlaceholder() {
				return true;
			}

			@Override
			public String getTitle(Player player) {
				return "Citadel Loot";
			}
		}, CC.getCustomHead("&5&lCitadel Loot &7(Click)", 1, "9dbdaa755099edd7efa1f12882c7a51b5815db52e0b164aef6df9a1f53eca23")));

		buttons.put(13, new LootTableButton(new SupplyCrateMenu(), CC.getCustomHead("&dSupply Crate Loot &7(Click)", 1, "d08df60c51074eef2544ff38cead9e16675ae4251916105180e1f8ce197ab3bc")));
		buttons.put(15, new LootTableButton(new AirdropLootMenu(), CC.getCustomHead("&bAirDrop Loot &7(Click)", 1, "3c11a0d90c37eb695c8a523d8601aa1c85fad09a4d2232d04ed23ac90e4325c2")));
		buttons.put(20, new LootTableButton(new VaultMenu(), CC.getCustomHead("&6Vault Event Loot &7(Click)", 1, "85ffb52332cbfcb5be53553d67c72643ba2bb517f7e89ded53d4a92b00cea73e")));
		buttons.put(22, new LootTablesButton(new LootTablePreviewMenu(Samurai.getInstance().getSlotHandler().getLootTable(), this), CC.getCustomHead("&eRoll Ticket Loot &7(Click)", 1, "cf760711fee47e4220c410881d5cde4aeb5ed0a1359018119effa5eb4dc937a9")));
		buttons.put(24, new LootTablesButton(new LootTablePreviewMenu(Samurai.getInstance().getDeepDarkHandler().getLootTable(), this), CC.getCustomHead("&3Deep Dark Loot &7(Click)", 1, "5f0e416fbc046c340758d1963f39bb840cbbe6397b0ccf906784e013edcd323f")));
		buttons.put(30, new LootTablesButton(new LootTablePreviewMenu(Samurai.getInstance().getLootHillHandler().getLootTable(), this), CC.getCustomHead("&bLoot Hill &7(Click)", 1, "59c81b619a7b55654d35ffe0db6c48d7636b0333f60e04035638a418abd4b24a")));
		buttons.put(32, new LootTablesButton(new LootTablePreviewMenu(Samurai.getInstance().getServerHandler().getFishingHandler().getLootTable(), this), CC.getCustomHead("&6Fishing &7(Click)", 1, "3ac2b1193c12459da32777a66e77c33eba8388d9cd516d83f3651b9f7baf0")));

		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return 45;
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
}
