package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class TriggerWelcomeCommand extends Command {

	public TriggerWelcomeCommand() {
		super("triggerwelcome", Collections.emptyList(), Collections.emptyList(), DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR), "Displays the welcome message", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (!(event.getName().equals("triggerwelcome"))) return;

		TextChannel channel = BotConfiguration.WELCOME_CHANNEL.getChannelById(TextChannel.class);

		event.deferReply().queue();

		if (channel == null) {
			event.reply("[!] Welcome channel could not be found.").queue();
			return;
		}

		channel.sendMessageEmbeds(MessageConfiguration.WELCOME_CONFIGURATION.getEmbed().format(event.getMember()).build()).submit().whenComplete((s, b) -> {
			event.getHook().editOriginal("Sent the message!").queue();
		});
	}

}
