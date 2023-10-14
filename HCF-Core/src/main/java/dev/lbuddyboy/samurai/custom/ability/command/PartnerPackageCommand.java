package dev.lbuddyboy.samurai.custom.ability.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.ability.offhand.OffHand;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.custom.ability.AbilityItemHandler;
import dev.lbuddyboy.samurai.custom.ability.menu.AbilityItemsMenu;
import dev.lbuddyboy.samurai.custom.ability.menu.AbilityItemsRecipesMenu;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.server.timer.command.ServerTimerCommand;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

import static dev.lbuddyboy.samurai.commands.staff.donator.DonatorTimerCommand.isTripled;

@CommandAlias("partneritem|pp|ppackage|abilityitems|ability")
public final class PartnerPackageCommand extends BaseCommand {

	@Default
	public static void def(Player sender) {
		new AbilityItemsMenu().openMenu(sender);
	}

	@Subcommand("recipes")
	public static void recipes(Player sender) {
		new AbilityItemsRecipesMenu().openMenu(sender);
	}

	@Subcommand("startevent")
	@CommandPermission("foxtrot.ability.startevent")
	public static void hourStart(CommandSender sender, @Name("time") String time) {

		if (SOTWCommand.isPartnerPackageHour()) {
			sender.sendMessage(CC.translate("&cThis event is already active."));
			return;
		}

		int seconds;
		try {
			seconds = TimeUtils.parseTime(time);
		} catch (IllegalArgumentException e) {
			sender.sendMessage(CC.RED + e.getMessage());
			return;
		}
		if (seconds < 0) {
			sender.sendMessage(ChatColor.RED + "Invalid time!");
			return;
		}

		for (String s : FoxtrotConfiguration.ABILITY_EVENT_START_MESSAGE.getStringList()) {
			Bukkit.broadcastMessage(CC.translate(s));
		}

		ServerTimer abilityEvent = new ServerTimer("AbilityEvent",
				FoxtrotConfiguration.ABILITY_EVENT_DISPLAY.getString(),
				FoxtrotConfiguration.ABILITY_EVENT_CONTEXT.getString(),
				System.currentTimeMillis(),
				seconds * 1000L
		);

		Samurai.getInstance().getTimerHandler().getServerTimers().put("AbilityEvent", abilityEvent);

		try {
			DiscordLogger.logSpecialEvent("Ability Event", TimeUtils.formatIntoDetailedString(seconds), "This event will cut in half any ability item's cooldown as long as the timer is active.");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Subcommand("stopevent")
	@CommandPermission("foxtrot.ability.stopevent")
	public static void hourStop(CommandSender sender) {
		ServerTimer removed = Samurai.getInstance().getTimerHandler().getServerTimers().get("AbilityEvent");

		if (removed != null) {
			sender.sendMessage(ChatColor.GREEN + "Deactivated the ability event timer.");
			ServerTimerCommand.delete(sender, Samurai.getInstance().getTimerHandler().getAbilityEvent());
			return;
		}

		sender.sendMessage(ChatColor.RED + "Not active");
	}

	@Subcommand("give|giveitem")
	@CommandPermission("foxtrot.ability")
	@CommandCompletion("@players @abilityitems")
	public static void give(CommandSender sender, @Name("player") OnlinePlayer target,
							@Name("package") AbilityItem partnerPackage,
							@Name("amount") int amount) {
		ItemStack item = partnerPackage.getPartnerItem();
		item.setAmount(amount);
		target.getPlayer().getInventory().addItem(item);
		sender.sendMessage(CC.WHITE + "Gave " + CC.MAIN + amount + " " + CC.PINK + CC.BOLD +
				partnerPackage.getName() + CC.WHITE + " to " + CC.MAIN + target.getPlayer().getName() + CC.WHITE + "!");
	}

	@Subcommand("giveoffhand")
	@CommandPermission("foxtrot.ability")
	@CommandCompletion("@players @offhanditems")
	public static void give(CommandSender sender, @Name("player") OnlinePlayer target,
							@Name("package") OffHand offHand,
							@Name("amount") int amount) {
		ItemStack item = offHand.getPartnerItem();
		item.setAmount(amount);
		target.getPlayer().getInventory().addItem(item);
		sender.sendMessage(CC.WHITE + "Gave " + CC.MAIN + amount + " " + CC.PINK + CC.BOLD +
				offHand.getName() + CC.WHITE + " to " + CC.MAIN + target.getPlayer().getName() + CC.WHITE + "!");
	}

	@Subcommand("givepackage|givepp")
	@CommandPermission("foxtrot.ability")
	public static void givepp(CommandSender sender, @Name("player") OnlinePlayer target,
							@Name("amount") int amount) {
		ItemStack item = Samurai.getInstance().getPartnerCrateHandler().getCrateItem().clone();
		int actualAmount = (isTripled() ? amount * 3 : amount);
		item.setAmount(actualAmount);
		target.getPlayer().getInventory().addItem(item);
		sender.sendMessage(CC.WHITE + "Gave " + CC.MAIN + actualAmount + " " + CC.PINK + CC.BOLD +
				item.getItemMeta().getDisplayName() + CC.WHITE + " to " + CC.MAIN + target.getPlayer().getName() + CC.WHITE + "!");
	}

	@Subcommand("givepackagenomulti|giveppnomulti")
	@CommandPermission("foxtrot.ability")
	public static void giveppnomulti(CommandSender sender, @Name("player") OnlinePlayer target,
							@Name("amount") int amount) {
		ItemStack item = Samurai.getInstance().getPartnerCrateHandler().getCrateItem().clone();
		item.setAmount(amount);
		ItemUtils.tryFit(target.getPlayer(), item, false);
		sender.sendMessage(CC.WHITE + "Gave " + CC.MAIN + amount + " " + CC.PINK + CC.BOLD +
				item.getItemMeta().getDisplayName() + CC.WHITE + " to " + CC.MAIN + target.getPlayer().getName() + CC.WHITE + "!");
	}

	@Subcommand("reset|resetcd")
	@CommandPermission("foxtrot.ability")
	@CommandCompletion("@players @abilityitems")
	public static void resetcd(CommandSender sender, @Name("player") OnlinePlayer target,
							@Name("package") AbilityItem partnerPackage) {
		partnerPackage.resetCooldown(target.getPlayer());
		sender.sendMessage(CC.GREEN + "Reset package cooldown for player " + CC.DARK_GREEN + target.getPlayer().getName() + CC.GREEN + "!");
	}

	@Subcommand("giveallpackage|giveallpp")
	@CommandPermission("foxtrot.ability")
	public static void giveallppnomulti(CommandSender sender, @Name("amount") int amount) {
		for (Player player : Bukkit.getServer().getOnlinePlayers()) {
			ItemStack crateItem = Samurai.getInstance().getPartnerCrateHandler().getCrateItem().clone();
			crateItem.setAmount(amount);
			ItemUtils.tryFit(player, crateItem, false);
			player.sendMessage(CC.GREEN + "You have received " + amount + "x " + crateItem.getItemMeta().getDisplayName());
		}
		sender.sendMessage(CC.GREEN + "Gave one partner package to all players!");
	}

}
