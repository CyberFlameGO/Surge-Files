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

public final class NinjaStar extends AbilityItem {

	public NinjaStar() {
		super("NinjaStar");
	}

	@Override
	public long getCooldownTime() {
		return SOTWCommand.isPartnerPackageHour() ? 60L : TimeUnit.MINUTES.toSeconds(2);
	}

	@Override
	public ItemStack partnerItem() {
		return ItemBuilder.of(Material.PRISMARINE_SHARD)
				.name(getName())
				.addToLore(
						" ",
						CC.translate("&g&lDescription"),
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fRight click to teleport to the player",
						" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &fthat has hit you within the last 15 seconds.",
						" "
				)
				.enchant(Enchantment.DURABILITY, 1)
				.modelData(2)
				.build();
	}

	@Override
	public String getName() {
		return CC.translate("&g&lNinja Star");
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

		if (attacker == null || (abilityProfile.getLastDamagedMillis() + 15_000L) < System.currentTimeMillis()) {
			player.sendMessage(ChatColor.RED + "The last person who attacked you could not be found!");
			return false;
		}

		OfflinePlayer target = Bukkit.getOfflinePlayer(abilityProfile.getLastDamagerName());

		if (!target.isOnline()) {
			player.sendMessage(CC.translate("&cYou have not been hit by a player in the past 15 seconds."));
			return false;
		}

		setGlobalCooldown(player);
		setCooldown(player);

		if (!target.isOnline()) {
			player.sendMessage(CC.translate("&cYou have not been hit by a player in the past 15 seconds."));
			return false;
		}

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
