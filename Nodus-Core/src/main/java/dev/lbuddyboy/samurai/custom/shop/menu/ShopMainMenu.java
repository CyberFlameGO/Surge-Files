package dev.lbuddyboy.samurai.custom.shop.menu;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.shards.menu.ShardMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.shop.ShopCategory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/12/2021 / 9:56 AM
 * SteelHCF-main / com.steelpvp.hcf.shop.menu
 */
public class ShopMainMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&x&1&d&a&4&f&bShop");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (ShopCategory category : ShopCategory.values()) {
			buttons.put(category.getMenuSlot(), new ShopCategoryButton(category));
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class ShopCategoryButton extends Button {

		public static final Feature[] VALUES = Feature.values();
		private ShopCategory category;

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
			return category.getButtonItem();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType) {
			if (category == ShopCategory.GEM) {
				if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.SHARD_SHOP)) {
					player.sendMessage(CC.translate("&cThis feature is currently disabled."));
					return;
				}
				new ShardMenu().openMenu(player);
				return;
			}
			if (Samurai.getInstance().getFeatureHandler().isDisabled(transform())) {
				player.sendMessage(CC.translate("&cThis feature is currently disabled."));
				return;
			}
			new ShopCategoryMenu(category).openMenu(player);
		}

		private Feature transform() {
			for (Feature feature : VALUES) {
				if (feature == Feature.SELL_SHOP && category == ShopCategory.SELL) {
					return feature;
				} else if (feature == Feature.BUY_SHOP && category == ShopCategory.BUY) {
					return feature;
				} else if (feature == Feature.DECO_SHOP && category == ShopCategory.DECORATION_SHOP) {
					return feature;
				} else if (feature == Feature.CLAY_SHOP && category == ShopCategory.CLAY_SHOP) {
					return feature;
				} else if (feature == Feature.CONCRETE_SHOP && category == ShopCategory.CONCRETE_SHOP) {
					return feature;
				} else if (feature == Feature.WOOL_SHOP && category == ShopCategory.WOOL_SHOP) {
					return feature;
				} else if (feature == Feature.GLASS_SHOP && category == ShopCategory.GLASS_SHOP) {
					return feature;
				}
			}
			return null;
		}

	}

	@Override
	public boolean isPlaceholder() {
		return true;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		return 54;
	}
}
