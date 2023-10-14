package dev.lbuddyboy.gkits.enchants.types;

import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.enchants.object.Rarity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/11/2021 / 1:48 PM
 * LBuddyBoy Development / me.lbuddyboy.gkits.enchants.types
 */
public class DepthStrider extends CustomEnchant {
	@Override
	public String name() {
		return "depthstrider";
	}

	@Override
	public String displayName() {
		return "DepthStrider";
	}

	@Override
	public int maxLevel() {
		return 4;
	}

	@Override
	public int minLevel() {
		return 3;
	}

	@Override
	public List<String> allowedTypes() {
		return Collections.singletonList("BOOTS");
	}

	@Override
	public String description() {
		return "Move fast in water";
	}
	@Override
	public List<Rarity> rarities() {
		return Collections.singletonList(Rarity.LEGENDARY);
	}

	@Override
	public void activate(Player player, ItemStack piece, int level) {
	}

	@Override
	public void deactivate(Player player, ItemStack piece, int level) {
	}
}
