package dev.lbuddyboy.samurai.listener.patch;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionLimitListener implements Listener {

	public PotionLimitListener() {
		maxLevel.put(PotionType.INSTANT_DAMAGE, Arrays.asList(-1));
		maxLevel.put(PotionType.REGEN, Arrays.asList(-1));
		maxLevel.put(PotionType.STRENGTH, Arrays.asList(-1));
		maxLevel.put(PotionType.WEAKNESS, Arrays.asList(-1));
		maxLevel.put(PotionType.SLOWNESS, Arrays.asList(1));
		maxLevel.put(PotionType.INVISIBILITY, Arrays.asList(1));
		maxLevel.put(PotionType.POISON, Arrays.asList(1));
		maxLevel.put(PotionType.FIRE_RESISTANCE, Arrays.asList(1));
		maxLevel.put(PotionType.SPEED, Arrays.asList(1, 2));
		maxLevel.put(PotionType.INSTANT_HEAL, Arrays.asList(1, 2));
		maxLevel.put(PotionType.NIGHT_VISION, Arrays.asList(-1));
		maxLevel.put(PotionType.WATER_BREATHING, Arrays.asList(-1));
		maxLevel.put(PotionType.WATER, Arrays.asList(-1));
		maxLevel.put(PotionType.SLOW_FALLING, Arrays.asList(-1));
		maxLevel.put(PotionType.JUMP, Arrays.asList(-1));
	}


	private static Map<PotionType, List<Integer>> maxLevel = new HashMap();

	@EventHandler
	public void onLaunch(PotionSplashEvent event) {
		if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) return;

			for (PotionEffect effect : event.getPotion().getEffects()) {
			if (effect.getType() == PotionEffectType.SPEED) break;
			if (effect.getType() == PotionEffectType.SLOW && effect.getDuration() > 20 * 60) {
				event.setCancelled(true);
				return;
			}
			PotionType potType = PotionType.getByEffect(effect.getType());
			if (maxLevel.containsKey(potType) && maxLevel.get(potType).contains(-1)) {
				event.setCancelled(true);
				return;
			}
			if (maxLevel.containsKey(potType) && effect.getAmplifier() > 1 && !maxLevel.get(potType).contains(2)) {
				event.setCancelled(true);
			} else if (maxLevel.containsKey(potType) && effect.getAmplifier() == 0 && maxLevel.get(potType).contains(2) && !maxLevel.get(potType).contains(1)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onLaunch(LingeringPotionSplashEvent event) {
		if (event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) return;

		for (PotionEffect effect : event.getEntity().getEffects()) {
			if (effect.getType() == PotionEffectType.SPEED) break;
			PotionType potType = PotionType.getByEffect(effect.getType());
			if (maxLevel.containsKey(potType) && maxLevel.get(potType).contains(-1)) {
				event.setCancelled(true);
				return;
			}
			if (maxLevel.containsKey(potType) && effect.getAmplifier() > 1 && !maxLevel.get(potType).contains(2)) {
				event.setCancelled(true);
			} else if (maxLevel.containsKey(potType) && effect.getAmplifier() == 0 && maxLevel.get(potType).contains(2) && !maxLevel.get(potType).contains(1)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event) {
		if (event.getPlayer().getWorld().getEnvironment() == World.Environment.THE_END && Samurai.getInstance().getMapHandler().isKitMap()) return;

		ItemStack stack = event.getItem();
		if (stack.getType().equals(Material.POTION)) {
			ItemMeta meta = event.getItem().getItemMeta();
			if (meta instanceof PotionMeta) {
				PotionMeta potMeta = (PotionMeta) meta;
				PotionData potionData = potMeta.getBasePotionData();
				PotionType potType = potionData.getType();
				if (potType == PotionType.SPEED) return;
				if (maxLevel.containsKey(potType) && maxLevel.get(potType).contains(-1)) {
					event.setCancelled(true);
					return;
				}
				if (maxLevel.containsKey(potType) && potionData.isUpgraded() && !maxLevel.get(potType).contains(2)) {
					event.setCancelled(true);
				} else if (maxLevel.containsKey(potType) && !potionData.isUpgraded() && maxLevel.get(potType).contains(2) && !maxLevel.get(potType).contains(1)) {
					event.setCancelled(true);
				}
			}
		}
	}

}
