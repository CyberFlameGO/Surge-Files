package dev.lbuddyboy.samurai.commands.donator;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

@CommandAlias("stream|livestream")
@CommandPermission("foxtrot.stream")
public final class LiveStreamCommand extends BaseCommand {

	@Default
	public void live(Player sender, @Name("link") @Single String link) {
		String lowerCase = link.toLowerCase();
		if (lowerCase.contains("youtu.be") || lowerCase.contains("youtube.com") || lowerCase.contains("twitch.tv")) {
			sender.setMetadata("stream_link", new FixedMetadataValue(Samurai.getInstance(), link));
			sender.sendMessage(ChatColor.GREEN + "Set your stream link!");
		} else {
			sender.sendMessage(ChatColor.RED + "You must enter a valid link!");
		}
	}

}
