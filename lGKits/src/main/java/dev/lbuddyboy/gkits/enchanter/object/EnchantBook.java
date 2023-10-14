package dev.lbuddyboy.gkits.enchanter.object;

import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import dev.lbuddyboy.gkits.util.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 11:19 PM
 * GKits / me.lbuddyboy.gkits.enchanter.object
 */

@AllArgsConstructor
@Getter
public class EnchantBook {

	private CustomEnchant enchant;
	private int level;

	public ItemStack build() {
		return new ItemBuilder(Material.BOOK)
				.setName(CC.translate(enchant.topRarityColor() + enchant.displayName() + " " + (level == Integer.MAX_VALUE ? enchant.minLevel() + "-" + (enchant.maxLevel() - 1) : (level == 0 ? 1 : level)) + ""))
				.setLore(CC.translate(Arrays.asList(
						"",
						"&fDrag this on to an item to add " + enchant.name() + " &fon to an item",
						"&7\u25b6 &fAllowed Gear: " + enchant.topRarityColor() + StringUtils.join(enchant.allowedTypes()),
						"&7\u25b6 &fDescription: " + enchant.topRarityColor() + enchant.description(),
						""
				)))
				.create();
	}

}
