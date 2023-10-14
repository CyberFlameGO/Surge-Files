package dev.lbuddyboy.samurai.map.stats.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.stats.StatsEntry;
import org.bukkit.ChatColor;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("statsmanager|statmanager|sm")
@CommandPermission("op")
public class StatManagerCommand extends BaseCommand {

	@Subcommand("setkills")
	@CommandCompletion("@players")
	public static void setKills(Player player, @Name("player") @Optional UUID target, @Name("kills") int kills) {

		StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(target);
		stats.setKills(kills);

		player.sendMessage(ChatColor.GREEN + "You've set " + FrozenUUIDCache.name(target) + "'s own kills to: " + kills);
	}

	@Subcommand("setdeaths")
	@CommandCompletion("@players")
	public static void setDeaths(Player player, @Name("player") @Optional UUID target, @Name("deaths") int deaths) {

		StatsEntry stats = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(target);
		stats.setDeaths(deaths);

		player.sendMessage(ChatColor.GREEN + "You've set " + FrozenUUIDCache.name(target) + "'s deaths to: " + deaths);
	}

	@Subcommand("setdeaths")
	@CommandCompletion("@players")
	public static void setkillstreak(Player player, @Name("player") @Optional UUID target, @Name("deaths") int killstreak) {
		StatsEntry statsEntry = Samurai.getInstance().getMapHandler().getStatsHandler().getStats(player);
		statsEntry.setKillstreak(killstreak);

		player.sendMessage(ChatColor.GREEN + "You set your killstreak to: " + killstreak);
	}

	@Subcommand("clearall")
	public static void clearallstats(Player sender) {
		ConversationFactory factory = new ConversationFactory(Samurai.getInstance()).withModality(true).withPrefix(new NullConversationPrefix()).withFirstPrompt(new StringPrompt() {

			@Override
			public String getPromptText(ConversationContext context) {
				return "&aAre you sure you want to clear all stats? Type &byes&a to confirm or &bno&a to quit.";
			}

			@Override
			public Prompt acceptInput(ConversationContext cc, String s) {
				if (s.equalsIgnoreCase("yes")) {
					Samurai.getInstance().getMapHandler().getStatsHandler().clearAll();
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "All stats cleared!");
					return Prompt.END_OF_CONVERSATION;
				}

				if (s.equalsIgnoreCase("no")) {
					cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Cancelled.");
					return Prompt.END_OF_CONVERSATION;
				}

				cc.getForWhom().sendRawMessage(ChatColor.GREEN + "Unrecognized response. Type &b/yes&a to confirm or &b/no&a to quit.");
				return Prompt.END_OF_CONVERSATION;
			}

		}).withLocalEcho(false).withEscapeSequence("/no").withTimeout(10).thatExcludesNonPlayersWithMessage("Go away evil console!");

		Conversation con = factory.buildConversation(sender);
		sender.beginConversation(con);
	}

}
