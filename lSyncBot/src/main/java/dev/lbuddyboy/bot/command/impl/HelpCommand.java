package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.awt.*;
import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class HelpCommand extends Command {

	public HelpCommand() {
		super("help", Collections.emptyList(), Collections.emptyList(), DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR), "Displays this message", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (event.getName().equals("help")) {
			System.out.println("Help checked");
			event.deferReply(true).queue();

			EmbedBuilder embedBuilder = new EmbedBuilder();

			embedBuilder.setTitle("Sync Bot (Command Help)");
			embedBuilder.setDescription(helpStrings().replaceAll("%prefix%", Bot.getInstance().getPrefix()));
			embedBuilder.setColor(Color.MAGENTA);

			event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
		}
	}

/*	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (Utils.canUseOther(event.getMember())) {
			event.deferReply(true).queue();

			EmbedBuilder embedBuilder = new EmbedBuilder();

			embedBuilder.setTitle("Sync Bot (Command Help)");
			embedBuilder.setDescription(helpStrings().replaceAll("%prefix%", Bot.getInstance().getPrefix()));
			embedBuilder.setColor(Color.MAGENTA);

			event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
		}
	}*/

	public String helpStrings() {

		StringBuilder builder = new StringBuilder();

		for (Command command : Bot.getInstance().getCommandHandler().getCommands()) {
			builder.append("\n ● **" + Bot.getInstance().getPrefix() + "" + command.getCmd() + command.getOtherArgs() + "** - *" + command.getDescription() + "*");
		}

		return builder.toString();
	}

}
