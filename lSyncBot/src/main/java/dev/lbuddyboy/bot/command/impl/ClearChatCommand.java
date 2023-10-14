package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class ClearChatCommand extends Command {

	public ClearChatCommand() {
		super("clearchat", Collections.emptyList(), Collections.singletonList(new OptionData(OptionType.INTEGER, "messages", "How many messages to clear.", true, false)), DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR), "Displays this message", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (event.getName().equals("clearchat")) {

			event.getGuildChannel().getHistoryBefore(event.getIdLong(), event.getOption("messages").getAsInt()).queue(messageHistory -> {
				messageHistory.getRetrievedHistory().forEach(message -> message.delete().queue());
			});

			EmbedBuilder embedBuilder = new EmbedBuilder();

			embedBuilder.setTitle("Clear Chat");
			embedBuilder.setDescription("The chat has been cleared by " + event.getMember().getAsMention());
			embedBuilder.setColor(Color.GREEN);

			event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
		}
	}
}
