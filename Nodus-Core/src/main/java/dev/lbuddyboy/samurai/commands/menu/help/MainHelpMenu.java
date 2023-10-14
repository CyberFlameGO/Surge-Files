package dev.lbuddyboy.samurai.commands.menu.help;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/12/2021 / 3:35 AM
 * SteelHCF-main / com.steelpvp.hcf.command.menu.help
 */
public class MainHelpMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aServer Help");
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(12, new HelpButton("&a&lGeneral Information &7(Click to View)", Material.ITEM_FRAME, new GeneralHelpMenu()));
		buttons.put(14, new HelpButton("&a&lMap Information &7(Click to View)", Material.MAP, new MapHelpMenu()));
		buttons.put(20, new HelpButton("&a&lFactions Information &7(Click to View)", Material.SHIELD, new FactionsHelpMenu()));
		buttons.put(24, new HelpButton("&a&lUseful Commands Information &7(Click to View)", Material.COMMAND_BLOCK, new UsefulCommandsHelpMenu()));
		buttons.put(39, new HelpInfoButton(CC.getCustomHead("&a&lStore &7(Click to View)", 1, "99e77fae5313bac19bf14577d50093e4738ebd70fd54a4de1a27475d0ec9538f"), "&a&lStore&7: https://store.minesurge.org"));
		buttons.put(40, new HelpInfoButton(CC.getCustomHead("&9&lDiscord &7(Click to View)", 1, "3664e54e76287e3fe2bd397c098ee7a4bd0f9c88f939fde8bab78c3271a4618f"), "&9&lDiscord&7: https://minesurge.org/discord"));
		buttons.put(41, new HelpInfoButton(CC.getCustomHead("&b&lTwitter &7(Click to View)", 1, "cc745a06f537aea80505559149ea16bd4a84d4491f12226818c3881c08e860fc"), "&b&lTwitter&7: https://twitter.com/MineSurgeORG"));

		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return super.size(buttons) + 9;
	}

	@AllArgsConstructor
	public static class HelpButton extends Button {

		private String name;
		private Material material;
		private Menu toMenu;

		@Override
		public String getName(Player var1) {
			return CC.translate(this.name);
		}

		@Override
		public List<String> getDescription(Player var1) {
			return null;
		}

		@Override
		public Material getMaterial(Player var1) {
			return this.material;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			toMenu.openMenu(player);
		}
	}

	@AllArgsConstructor
	public static class HelpInfoButton extends Button {

		private ItemStack stack;
		private String message;

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
			return this.stack;
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			player.closeInventory();
			player.sendMessage(CC.translate(this.message));
		}
	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}
}
