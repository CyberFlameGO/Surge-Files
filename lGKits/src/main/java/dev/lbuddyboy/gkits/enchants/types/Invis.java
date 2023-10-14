package dev.lbuddyboy.gkits.enchants.types;

import dev.lbuddyboy.gkits.enchants.object.CustomEnchant;
import dev.lbuddyboy.gkits.enchants.object.Rarity;
import dev.lbuddyboy.gkits.lGKits;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/11/2021 / 1:48 PM
 * LBuddyBoy Development / me.lbuddyboy.gkits.enchants.types
 */
public class Invis extends CustomEnchant {
	@Override
	public String name() {
		return "invisibility";
	}

	@Override
	public String displayName() {
		return lGKits.getInstance().getEnchantsYML().gc().getString("enchants." + name() + ".displayName");
	}

	@Override
	public List<Rarity> rarities() {
		List<Rarity> rarityList = new ArrayList<>();
		for (String s : lGKits.getInstance().getEnchantsYML().gc().getStringList("enchants." + name() + ".rarities")) {
			rarityList.add(Rarity.valueOf(s));
		}
		return rarityList;
	}

	@Override
	public int maxLevel() {
		return lGKits.getInstance().getEnchantsYML().gc().getInt("enchants." + name() + ".maxLevel");
	}

	@Override
	public int minLevel() {
		return lGKits.getInstance().getEnchantsYML().gc().getInt("enchants." + name() + ".minLevel");
	}

	@Override
	public List<String> allowedTypes() {
		return lGKits.getInstance().getEnchantsYML().gc().getStringList("enchants." + name() + ".allowedArmor");
	}

	@Override
	public String description() {
		return lGKits.getInstance().getEnchantsYML().gc().getString("enchants." + name() + ".description");
	}

	@Override
	public void activate(Player player, ItemStack piece, int level) {
		super.activate(player, piece, level);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION, level - 1));
	}

	@Override
	public void deactivate(Player player, ItemStack piece, int level) {
		super.deactivate(player, piece, level);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
		player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 30, level));
	}

	@Override
	public void deactivate(Player player) {
		super.deactivate(player);
		player.removePotionEffect(PotionEffectType.INVISIBILITY);
	}

}
