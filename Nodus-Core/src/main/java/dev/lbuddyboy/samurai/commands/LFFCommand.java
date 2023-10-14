package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.commands.menu.menu.LFFMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("lff|lookingforafac|lookingforafaction")
public class LFFCommand extends BaseCommand {

	public static Cooldown cooldown = new Cooldown();

	@Default
	public static void lff(Player sender) {
		Team faction = Samurai.getInstance().getTeamHandler().getTeam(sender);
		if (faction == null) {
			if (cooldown.onCooldown(sender)) {
				sender.sendMessage(org.bukkit.ChatColor.RED + "You are currently on cooldown for another " + cooldown.getRemaining(sender) + ".");
				return;
			}
			new LFFMenu().openMenu(sender);
		} else {
			sender.sendMessage(ChatColor.RED + "You're already on a team.");
		}
	}

}
