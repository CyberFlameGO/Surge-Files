package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/01/2022 / 11:45 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.staff
 */

@CommandAlias("lagg|clearlag|clearlagg|clearitems|cleargrounditems")
@CommandPermission("op")
public class ClearItemsCommand extends BaseCommand {

	@Default
	public static void def(CommandSender sender) {
		int i = 0;

		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntitiesByClasses(Item.class)) {
				entity.remove();
				++i;
			}
		}

		for (World world : Bukkit.getWorlds()) {
			for (Entity entity : world.getEntities()) {
				if (entity instanceof Player) continue;
				if (!(entity instanceof Monster)) continue;
				entity.remove();
				++i;
			}
		}

		sender.sendMessage(CC.translate("&aCleared " + i + " entities from the world."));
	}

	@Subcommand("clear")
	public void clear(CommandSender sender) {
		def(sender);
	}

}
