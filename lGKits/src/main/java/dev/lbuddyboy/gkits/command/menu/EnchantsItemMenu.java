package dev.lbuddyboy.gkits.command.menu;

import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.enchanter.object.EnchantBook;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import dev.lbuddyboy.gkits.util.menu.button.BackButton;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/07/2021 / 10:49 PM
 * GKits / me.lbuddyboy.gkits.command.menu
 */

public class EnchantsItemMenu extends Menu {

	private String type;

	public EnchantsItemMenu(String type) {
		this.type = type;
	}

	@Override
	public String getTitle(Player player) {
		return "Enchants";
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 1;
		for (CustomEnchant customEnchant : lGKits.getInstance().getCustomEnchantManager().listByArmorType(type)) {
			buttons.add(Button.fromItem(new EnchantBook(customEnchant, Integer.MAX_VALUE).build(), i++));
		}

		int size = calculatedSize(buttons);

		buttons.add(new BackButton(size + 9, new EnchantsMenu()));

		return buttons;
	}

	public int calculatedSize(List<Button> buttons) {
		int highest = 0;
		for (Button button : buttons) {
			if (button.getSlot() <= 0) continue;

			if (button.getSlot() - 1 <= highest) continue;
			highest = button.getSlot() - 1;
		}
		return (int) (Math.ceil((double) (highest + 1) / 9.0) * 9.0);
	}

	@Override
	public int getSize(Player player) {
		return calculatedSize(getButtons(player));
	}
}
