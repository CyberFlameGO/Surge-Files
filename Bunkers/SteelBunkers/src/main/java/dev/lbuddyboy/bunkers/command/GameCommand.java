package dev.lbuddyboy.bunkers.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.command.claim.ClaimCommand;
import dev.lbuddyboy.bunkers.game.user.GameUser;
import dev.lbuddyboy.bunkers.scoreboard.BunkersScoreboard;
import dev.lbuddyboy.bunkers.util.Cuboid;
import dev.lbuddyboy.bunkers.util.bukkit.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 6:59 PM
 * SteelBunkers / com.steelpvp.bunkers.command
 */

@CommandAlias("game")
public class GameCommand extends BaseCommand {

	@Subcommand("start")
	@CommandPermission("op")
	public static void gameStart(CommandSender sender, @Name("countdown") int countdown) {
		Bunkers.getInstance().getGameHandler().start(countdown);
		sender.sendMessage(CC.translate("&aStarted the game start countdown..."));
	}

	@Subcommand("end")
	@CommandPermission("op")
	public static void gameEnd(CommandSender sender) {
		Bunkers.getInstance().getGameHandler().end(false);
		sender.sendMessage(CC.translate("&aEnded the game..."));
	}

	@Subcommand("setgame box")
	@CommandPermission("op")
	public static void setgame(Player sender) {
		Bunkers.getInstance().getGameHandler().getGameSettings().setBounds(new Cuboid(ClaimCommand.pos1.get(sender.getName()), ClaimCommand.pos2.get(sender.getName())));
		Bunkers.getInstance().getGameHandler().getGameSettings().save();

		sender.sendMessage(CC.translate("&aSet the game box."));
	}

	@Subcommand("setkoth box")
	@CommandPermission("op")
	public static void setkothloc(Player sender) {
		Bunkers.getInstance().getGameHandler().getGameSettings().setKothCuboid(new Cuboid(ClaimCommand.pos1.get(sender.getName()), ClaimCommand.pos2.get(sender.getName())));
		Bunkers.getInstance().getGameHandler().getGameSettings().save();

		sender.sendMessage(CC.translate("&aSet the koth box."));
	}

	@Subcommand("setkoth name")
	@CommandPermission("op")
	public static void setkothname(CommandSender sender, @Name("name") String name) {
		Bunkers.getInstance().getGameHandler().getGameSettings().setKothName(name);
		Bunkers.getInstance().getGameHandler().getGameSettings().save();

		sender.sendMessage(CC.translate("&aSet the koth name."));
	}

	@Subcommand("setkoth time")
	@CommandPermission("op")
	public static void setkothtime(CommandSender sender, @Name("name") int seconds) {
		Bunkers.getInstance().getGameHandler().getKoTHHandler().setDefSecs(seconds);
		Bunkers.getInstance().getGameHandler().getKoTHHandler().setRemaining(seconds);

		sender.sendMessage(CC.translate("&aSet the koth seconds."));
	}

	@Subcommand("addbal")
	@CommandPermission("op")
	public static void setBal(CommandSender sender, @Name("target") UUID target, @Name("amount") double amount) {
		GameUser user = Bunkers.getInstance().getGameHandler().getGameUser(target);
		user.addBalance(amount);
	}

	@Subcommand("startkoth")
	@CommandPermission("op")
	public static void startkoth(CommandSender sender, @Name("millis") long millis) {
		BunkersScoreboard.setKOTH_START_MILLIS(millis);
	}

	@Subcommand("zerodura")
	@CommandPermission("op")
	public static void setzerodura(Player sender) {
		sender.getInventory().getItemInMainHand().setDurability((short) (sender.getInventory().getItemInMainHand().getType().getMaxDurability() - 1));
	}

}
