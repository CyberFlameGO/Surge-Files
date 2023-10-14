package dev.lbuddyboy.samurai.custom.ability.items.exotic;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.profile.AbilityProfile;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class NinjaStarTwo extends AbilityItem implements Listener {

	public NinjaStarTwo() {
		super("NinjaStarTwo");

		this.name = "ninja-two";
	}

	private List<UUID> ninjaStarring = new ArrayList<>();

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		return false;
	}

	@EventHandler
	public void onEat(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();

		AbilityItem heldPackage = null;
		for (AbilityItem partnerPackage : Samurai.getInstance().getAbilityItemHandler().getAbilityItems()) {
			if (partnerPackage.isPartnerItem(player.getItemInUse())) {
				heldPackage = partnerPackage;
			}
		}

		if (heldPackage == null) {
			return;
		}

		if (!canUse(player, event)) return;

		AbilityProfile abilityProfile = AbilityProfile.byUUID(player.getUniqueId());
		Player attacker = Bukkit.getPlayer(abilityProfile.getLastDamagerName());

		if (attacker == null || (abilityProfile.getLastDamagedMillis() + 15_000L) < System.currentTimeMillis()) {
			player.sendMessage(ChatColor.RED + "The last person who attacked you could not be found!");
			return;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(abilityProfile.getLastDamagerName());

		if (!target.isOnline()) {
			player.sendMessage(CC.translate("&cYou have not been hit by a player in the past 15 seconds."));
			return;
		}

		setGlobalCooldown(player);
		setCooldown(player);
		consume(attacker, event.getItem());

		MessageConfiguration.NINJA_STAR_ACTIVATED_CLICKER.sendListMessage(player
				, "%ability-name%", this.getName()
				, "%target%", target.getName()
		);

		if (target.getPlayer() != null) {
			MessageConfiguration.NINJA_STAR_ACTIVATED_TARGET.sendListMessage(target.getPlayer()
					, "%ability-name%", getName()
					, "%clicker%", player.getName()
			);
		}

		ninjaStarring.add(player.getUniqueId());

		new BukkitRunnable() {

			@Override
			public void run() {
				if (!ninjaStarring.contains(player.getUniqueId())) return;

				Player onlineTarget = (Player) target;

				Location storedLoc = onlineTarget.getLocation();

				if (!target.isOnline()) {
					player.teleport(storedLoc);
					return;
				}

				player.teleport(onlineTarget);
			}

		}.runTaskLater(Samurai.getInstance(), 20 * 3);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		this.ninjaStarring.remove(event.getEntity().getUniqueId());
	}

}
