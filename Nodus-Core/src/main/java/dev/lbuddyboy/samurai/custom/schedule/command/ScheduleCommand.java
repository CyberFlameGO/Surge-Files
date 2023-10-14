package dev.lbuddyboy.samurai.custom.schedule.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.mongodb.client.model.Filters;
import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.custom.schedule.ScheduledTime;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.schedule.ScheduleMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/01/2022 / 8:31 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.schedule
 */

@CommandAlias("schedule")
public class ScheduleCommand extends BaseCommand {

	@Default
	public static void schedules(Player sender) {
		new ScheduleMenu().openMenu(sender);
	}

	@Subcommand("add")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void scheduleAdd(CommandSender sender,
								   @Name("name") String name, @Name("delay") String delayStr, @Name("command") String command) {
		if (Samurai.getInstance().getScheduleHandler().getScheduledTimes().containsKey(name)) {
			sender.sendMessage(CC.translate("&cThat scheduled time already exists."));
			return;
		}

		long time = JavaUtils.parse(delayStr);

		Samurai.getInstance().getScheduleHandler().getScheduledTimes().put(name, new ScheduledTime(
				name,
				command,
				time
		));

		sender.sendMessage(CC.translate("&aSuccessfully added the " + command + " to the schedule! It will execute in " + TimeUtils.formatLongIntoDetailedString(time / 1000)));
		Samurai.getInstance().getScheduleHandler().save();
	}

	@Subcommand("setcommand")
	@CommandPermission("foxtrot.scheduleadmin")
	@CommandCompletion("@scheduledTimes")
	public static void setcommand(CommandSender sender, @Name("time") ScheduledTime time, @Name("command") String command) {
		if (!Samurai.getInstance().getScheduleHandler().getScheduledTimes().containsKey(time.getName())) {
			sender.sendMessage(CC.translate("&cThat scheduled time doesn't exist."));
			return;
		}

		time.setCommand(command);
		sender.sendMessage(CC.translate("&aEdited the " + time.getName() + "'s schedule time command to " + command + "."));
		Samurai.getInstance().getScheduleHandler().save();
	}

	@Subcommand("extendtime")
	@CommandPermission("foxtrot.scheduleadmin")
	@CommandCompletion("@scheduledTimes")
	public static void extendtime(CommandSender sender, @Name("time") ScheduledTime time, @Name("command") String duraStr) {
		if (!Samurai.getInstance().getScheduleHandler().getScheduledTimes().containsKey(time.getName())) {
			sender.sendMessage(CC.translate("&cThat scheduled time doesn't exist."));
			return;
		}

		time.setDuration(time.getDuration() + JavaUtils.parse(duraStr));
		sender.sendMessage(CC.translate("&aEdited the " + time.getName() + "'s schedule time duration to " + TimeUtils.formatLongIntoDetailedString((time.getTimeLeft()) / 1000) + "."));
		Samurai.getInstance().getScheduleHandler().save();
	}

	@Subcommand("remove")
	@CommandCompletion("@scheduledTimes")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void scheduleRemove(CommandSender sender, @Name("time") ScheduledTime time) {
		if (!Samurai.getInstance().getScheduleHandler().getScheduledTimes().containsKey(time.getName())) {
			sender.sendMessage(CC.translate("&cThat scheduled time doesn't exist."));
			return;
		}

		sender.sendMessage(CC.translate("&cDeleted the " + time.getName() + " scheduled time."));
		Samurai.getInstance().getScheduleHandler().getScheduledTimes().remove(time.getName());
		Samurai.getInstance().getScheduleHandler().getCollection().deleteOne(Filters.eq("name", time.getName()));
		Samurai.getInstance().getScheduleHandler().save();
	}

	@Subcommand("clear")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void scheduleClear(CommandSender sender) {

		for (Map.Entry<String, ScheduledTime> entry : Samurai.getInstance().getScheduleHandler().getScheduledTimes().entrySet()) {
			Samurai.getInstance().getScheduleHandler().getCollection().deleteOne(Filters.eq("name", entry.getKey()));
		}
		Samurai.getInstance().getScheduleHandler().getScheduledTimes().clear();
	}

	@Subcommand("startkitsdefaults")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void startkitsdefaults(CommandSender sender) {
		scheduleAdd(sender, "Triple-Keys (2 hours)", "1s", "triplekeys start 2h");
		scheduleAdd(sender, "x1-Partner-Key-All", "5m", "goldencrates givekey * Partner 1");
		scheduleAdd(sender, "x1-Class-Key-All", "20m", "goldencrates givekey * Class 1");
		scheduleAdd(sender, "x2-Ability-Package-All", "30m", "pp all 2");
		scheduleAdd(sender, "Ability-Event", "45m", "pp hour 1h");
		scheduleAdd(sender, "Double-Shards-Event", "1h45m", "shards hour 1h");
	}


	@Subcommand("starthcfdefaults")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void startdefaults(CommandSender sender) {
		scheduleAdd(sender, "x2-Surge-Key-All", "30m", "crate giveall Surge 2");
		scheduleAdd(sender, "x3-Aura-Key-All", "60m", "crate giveall Aura 3");
		scheduleAdd(sender, "x1-Airdrop-All", "75m", "airdrops giveall 1");
		scheduleAdd(sender, "x2-Ability-Package-All", "1h45m", "ability givepp all 2");
		scheduleAdd(sender, "Reduced-Ability-CD-Event", "2h", "ability startevent 2h");
		scheduleAdd(sender, "Deep Dark Event", "2h10m", "deepdark spawnevent");
		scheduleAdd(sender, "Random KoTH", "2h30m", "koth activaterandom");
		scheduleAdd(sender, "Citadel", "24h", "koth activate Citadel");
	}

	@Subcommand("list")
	@CommandPermission("foxtrot.scheduleadmin")
	public static void scheduleListt(CommandSender sender) {
		sender.sendMessage(CC.translate("&7"));
		sender.sendMessage(CC.translate("&g&lSchedule List"));
		sender.sendMessage(CC.translate("&7"));
		if (Samurai.getInstance().getScheduleHandler().getScheduledTimes().isEmpty()) {
			sender.sendMessage(CC.translate("&cThere is nothing scheduled at the moment."));
			sender.sendMessage(CC.translate("&7"));
		} else {
			for (Map.Entry<String, ScheduledTime> entry : Samurai.getInstance().getScheduleHandler().getScheduledTimes().entrySet()) {
				sender.sendMessage(CC.translate("&g" + sender.getName()));
				if (sender.isOp()) {
					sender.sendMessage(CC.translate(" &7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCommand&f: &c" + entry.getValue().getCommand()));
				}
				sender.sendMessage(CC.translate(" &7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gTime Left&f: &c" + TimeUtils.formatLongIntoDetailedString((entry.getValue().getTimeLeft()) / 1000)));
				sender.sendMessage(CC.translate("&f"));
			}
		}
	}

}
