package dev.lbuddyboy.samurai.util.modsuite;

import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.potion.PotionEffect;

public final class PlayerUtils {

	public static void resetInventory(Player player) {
		PlayerUtils.resetInventory(player, null);
	}

	public static void resetInventory(Player player, GameMode gameMode) {
		player.setHealth(player.getMaxHealth());
		player.setFallDistance(0.0f);
		player.setFoodLevel(20);
		player.setSaturation(10.0f);
		player.setLevel(0);
		player.setExp(0.0f);
		if (!player.hasMetadata("modmode")) {
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
		}
		player.setFireTicks(0);
		for (PotionEffect potionEffect : player.getActivePotionEffects()) {
			player.removePotionEffect(potionEffect.getType());
		}
		if (gameMode != null && player.getGameMode() != gameMode) {
			player.setGameMode(gameMode);
		}
	}

	public static Player getDamageSource(Entity damager) {
		Projectile projectile;
		Player playerDamager = null;
		if (damager instanceof Player) {
			playerDamager = (Player) damager;
		} else if (damager instanceof Projectile && (projectile = (Projectile) damager).getShooter() instanceof Player) {
			playerDamager = (Player) projectile.getShooter();
		}
		return playerDamager;
	}

}

