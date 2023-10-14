package dev.lbuddyboy.samurai.custom.shop;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/02/2022 / 5:29 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.shop
 */

@Getter
public class ShopHandler {

	public static final ShopItem[] VALUES = ShopItem.values();
	private final Map<ShopCategory, List<ShopItem>> blocks;

	public ShopHandler() {
		this.blocks = new ConcurrentHashMap<>();

		for (ShopItem item : VALUES) {
			List<ShopItem> items = blocks.getOrDefault(item.getCategory(), new ArrayList<>());
			items.add(item);
			blocks.put(item.getCategory(), items);
		}

	}

}
