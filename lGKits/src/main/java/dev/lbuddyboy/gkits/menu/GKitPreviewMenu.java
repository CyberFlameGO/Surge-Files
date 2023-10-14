package dev.lbuddyboy.gkits.menu;

import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import dev.lbuddyboy.gkits.util.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GKitPreviewMenu extends Menu {

	private final GKit gKit;

	public GKitPreviewMenu(GKit gKit) {
		this.gKit = gKit;
	}

	@Override
	public String getTitle(Player player) {
		return "Preview";
	}

	@Override
	public int getSize(Player player) {
		return Math.min(super.getSize(player), 54);
	}

	@Override
	public List<Button> getButtons(Player player) {
		List<Button> buttons = new ArrayList<>();

		int i = 19;
		int in = 2;

		if (gKit.getArmor() != null) {
			List<ItemStack> armor = new ArrayList<>(Arrays.asList(gKit.getArmor()));
			Collections.reverse(armor);
			for (ItemStack item : armor) {
				if (item == null || item.getType() == Material.AIR) {
					continue;
				}

				buttons.add(Button.fromItem(item, in++));
			}
		}
		for (ItemStack item : gKit.getItems()) {
			buttons.add(Button.fromItem(item, i++));
		}

		for (ItemStack item : gKit.getFakeItems()) {
			if (item == null || item.getType() == Material.AIR) continue;

			buttons.add(Button.fromItem(item, i++));
		}

		buttons.add(new BackButton(9, new GKitMenu()));

		return buttons;
	}

	@Override
	public boolean autoFill() {
		return true;
	}
}