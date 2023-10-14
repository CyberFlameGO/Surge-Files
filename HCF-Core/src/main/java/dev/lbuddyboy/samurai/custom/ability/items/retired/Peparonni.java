package dev.lbuddyboy.samurai.custom.ability.items.retired;

import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.profile.AbilityProfile;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

public final class Peparonni extends AbilityItem {

	public Peparonni() {
		super("Peperoni");
	}

	@Override
	public long getCooldownTime() {
		return SOTWCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(2);
	}

	@Override
	public ItemStack partnerItem() {
		return ItemBuilder.of(Material.ROSE_BUSH)
				.name(getName())
				.addToLore(
						" ",
						CC.translate("&g&lDescription"),
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click to teleport to the player",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fthat has hit you within the last 20 seconds.",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fYOU HAVE TO BE IN WATER/LAVA",
						" "
				)
				.enchant(Enchantment.DURABILITY, 1)
				.modelData(2)
				.build();
	}

	@Override
	public String getName() {
		return CC.translate("&g&lPizzas Peperoni");
	}

	@Override
	public int getAmount() {
		return 1;
	}

	@Override
	protected boolean onUse(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		AbilityProfile abilityProfile = AbilityProfile.byUUID(player.getUniqueId());
		Player attacker = Bukkit.getPlayer(abilityProfile.getLastDamagerName());

		if (attacker == null || (abilityProfile.getLastDamagedMillis() + 20_000L) < System.currentTimeMillis()) {
			player.sendMessage(ChatColor.RED + "The last person who attacked you could not be found!");
			return false;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(abilityProfile.getLastDamagerName());

		if (!target.isOnline()) {
			player.sendMessage(CC.translate("&cYou have not been hit by a player in the past 20 seconds."));
			return false;
		}
		if (!target.isOnline()) {
			player.sendMessage(CC.translate("&cYou have not been hit by a player in the past 20 seconds."));
			return false;
		}

		if (player.getLocation().getBlock().getType() != Material.LAVA && player.getLocation().getBlock().getType() != Material.WATER) {
			player.sendMessage(CC.translate("&cYou are not in lava/water."));
			return false;
		}

		setGlobalCooldown(player);
		setCooldown(player);

		sendActivationMessages(player,
				new String[]{
						"You have activated " + getName() + CC.WHITE + "!",
						"You will be teleported in " + CC.MAIN + "3 seconds" + CC.WHITE + "."
				}, null, null);

		Player onlineTarget = (Player) target;

		onlineTarget.sendMessage("");
		onlineTarget.sendMessage(CC.translate(getName() + " &7- &a" + player.getName() + " &7will be teleported to you in &g3 seconds"));
		onlineTarget.sendMessage("");

		Location storedLoc = onlineTarget.getLocation();

		new BukkitRunnable() {
			@Override
			public void run() {
				if (!target.isOnline()) {
					player.teleport(storedLoc);
					return;
				}

				player.teleport(onlineTarget);
			}
		}.runTaskLater(Samurai.getInstance(), 70);
		return true;
	}

}
