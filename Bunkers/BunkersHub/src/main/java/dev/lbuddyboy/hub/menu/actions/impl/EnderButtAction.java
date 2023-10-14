package dev.lbuddyboy.hub.menu.actions.impl;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.menu.actions.IAction;
import lombok.Getter;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 12:16 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.actions
 */

@Getter
public class EnderButtAction extends IAction<Player, Double> {

	@Override
	public String getName() {
		return "ENDER_BUTT";
	}

	@Override
	public void perform(Player player, String value) {
		if (player.isSneaking()) return;
		if (player.isInsideVehicle()) {
			player.getVehicle().setPassenger(null);
			player.getVehicle().remove();
		}

		try {
			player.playSound(player.getLocation(), lHub.getInstance().getSettingsHandler().getEnderButtSound(), 2.0f, 1.0f);
		} catch (Exception ignored) {}

		player.updateInventory();

		if (lHub.getInstance().getSettingsHandler().isEnderButtRide()) {
			double boost = lHub.getInstance().getSettingsHandler().getEnderButtMultiplier();
			player.setVelocity(player.getLocation().getDirection().normalize().multiply(boost));
			return;
		}

		EnderPearl pearl = player.launchProjectile(EnderPearl.class);
		pearl.setPassenger(player);
		player.spigot().setCollidesWithEntities(false);
	}

}
