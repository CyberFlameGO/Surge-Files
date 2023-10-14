package dev.lbuddyboy.bunkers.game.shop;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.shop.menu.BuildMenu;
import dev.lbuddyboy.bunkers.game.shop.menu.CombatMenu;
import dev.lbuddyboy.bunkers.game.shop.menu.EnchantMenu;
import dev.lbuddyboy.bunkers.game.shop.menu.SellMenu;
import dev.lbuddyboy.bunkers.util.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;

@Getter
public class Shop {

	@Setter public static int[] COMBAT_SLOTS, SELL_SLOTS, BUY_SLOTS, ENCHANTMENT_SLOTS;
	@Getter private static final Map<String, Shop> shops = new HashMap<>();

	private final Villager.Type villagerType;
	private Villager villager;
	private final int[] slots;
	private final Location spawnLoc;

	public Shop(Villager.Type villagerType, int[] slots, Location location) {
		this.villagerType = villagerType;
		this.slots = slots;
		this.spawnLoc = location;
	}

	public void spawn() {
		if (this.spawnLoc == null) return;
		this.villager = this.spawnLoc.getWorld().spawn(this.spawnLoc, Villager.class);
		villager.setVillagerType(this.villagerType);
		villager.setCustomName(CC.translate(getName() + " Shop"));
		villager.setCustomNameVisible(true);
		villager.setNoDamageTicks(20 * 60);
		villager.setMetadata(CC.translate(getName()), new FixedMetadataValue(Bunkers.getInstance(), true));
	}

	public void openMenu(Player player) {
		if (getName().equals("&4Combat")) {
			new CombatMenu(this).openMenu(player);
			return;
		}
		if (getName().equals("&cSell")) {
			new SellMenu(this).openMenu(player);
			return;
		}
		if (getName().equals("&dEnchantment")) {
			new EnchantMenu(this).openMenu(player);
			return;
		}
		new BuildMenu(this).openMenu(player);
	}

	public String getName() {
		if (this.villagerType == Villager.Type.JUNGLE) {
			return "&cSell";
		}
		if (this.villagerType == Villager.Type.TAIGA) {
			return "&4Combat";
		}
		if (this.villagerType == Villager.Type.SAVANNA) {
			return "&dEnchantment";
		}
		return "&aBuild";
	}

	public void kill() {
		long start = (System.currentTimeMillis() + 60_000L);

		this.villager = null;
		Bukkit.getScheduler().runTaskTimer(Bunkers.getInstance(), (task) -> {
			long diff = start - System.currentTimeMillis();
			if (diff <= 0) {
				task.cancel();
				this.villager.remove();
				this.villager = null;
				spawn();
				return;
			}
			if (this.villager != null) {
				this.villager.setCustomName(CC.translate("&cDead: &7" + TimeUtils.formatLongIntoMMSS((diff / 1000)) + "..."));
				return;
			}
			this.villager = this.spawnLoc.getWorld().spawn(this.spawnLoc, Villager.class);
			villager.setVillagerType(this.villagerType);
			villager.setCustomNameVisible(true);
			villager.setMetadata("dead", new FixedMetadataValue(Bunkers.getInstance(), true));
			villager.setCustomName(CC.translate("&cDead: &7" + TimeUtils.formatLongIntoMMSS((diff / 1000)) + "..."));
		}, 0, 20);
	}

}
