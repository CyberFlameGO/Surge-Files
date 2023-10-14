package dev.lbuddyboy.bunkers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Name;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 6:59 PM
 * SteelBunkers / com.steelpvp.bunkers.command
 */

@CommandPermission("pay")
public class PayCommand extends BaseCommand {

	@Default
	public static void pay(Player sender, @Name("target") UUID target, @Name("amount") double amount) {

		if (!Bunkers.getInstance().getGameHandler().getGameUser(sender.getUniqueId()).hasBalance(amount)) {
			sender.sendMessage(CC.translate("&cYou do not have funds for this."));
			return;
		}

		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(target);
		user.addBalance(amount);
		Bunkers.getInstance().getGameHandler().getGameUser(sender.getUniqueId()).takeBalance(amount);
	}

}
