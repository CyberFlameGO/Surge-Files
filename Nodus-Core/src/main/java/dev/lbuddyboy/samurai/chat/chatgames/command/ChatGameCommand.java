package dev.lbuddyboy.samurai.chat.chatgames.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:35 PM
 * HCTeams / rip.orbit.hcteams.chatgames.command
 */

@CommandAlias("chatgame")
@CommandPermission("op")
public class ChatGameCommand extends BaseCommand {

	@Subcommand("autostart")
	public static void autostartMath(CommandSender sender, @Name("question/math") String type) {
		if (type.equalsIgnoreCase("math")) {
			Samurai.getInstance().getChatHandler().getChatGameHandler().getChatGames().get(1).start();
		} else {
			Samurai.getInstance().getChatHandler().getChatGameHandler().getChatGames().get(0).start();
		}
	}

}
