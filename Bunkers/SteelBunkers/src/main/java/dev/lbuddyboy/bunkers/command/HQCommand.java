package dev.lbuddyboy.bunkers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.team.Team;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import dev.lbuddyboy.bunkers.util.cooldown.Cooldown;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 6:59 PM
 * SteelBunkers / com.steelpvp.bunkers.command
 */

@CommandPermission("hq|home")
public class HQCommand extends BaseCommand {

	public static Cooldown time = new Cooldown();

	@Default
	public static void def(Player sender) {
		Team team = Bunkers.getInstance().getTeamHandler().getTeam(sender);
		if (team == null) return;

		if (time.onCooldown(sender)) {
			sender.sendMessage(CC.translate("&cYou are already warping to your HQ."));
			return;
		}

		sender.sendMessage(CC.translate("&cYou will be teleported to your spawn point in 15 seconds, don't get hit or move a block!"));
		time.applyCooldown(sender, 15);
		BukkitTask task = new BukkitRunnable() {
			@Override
			public void run() {
				sender.teleport(team.getHome());
			}
		}.runTaskLater(Bunkers.getInstance(), 20 * 15);
		BukkitRunnable runnable = new StuckTask(sender, sender.getLocation(), task, sender.getHealth());
		runnable.runTaskTimer(Bunkers.getInstance(), 2, 2);
	}

}
