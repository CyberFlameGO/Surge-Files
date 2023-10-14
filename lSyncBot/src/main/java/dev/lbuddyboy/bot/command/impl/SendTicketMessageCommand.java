package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import dev.lbuddyboy.bot.ticket.TicketType;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class SendTicketMessageCommand extends Command {

	public SendTicketMessageCommand() {
		super("sendticketmessage", Collections.emptyList(), Collections.emptyList(), DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR), "Displays the welcome message", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (!(event.getName().equals("sendticketmessage"))) return;

		TextChannel channel = BotConfiguration.TICKET_CHANNEL.getChannelById(TextChannel.class);

		if (channel == null) {
			event.reply("[!] Ticket channel could not be found.").queue();
			return;
		}

		event.deferReply().queue();

		List<Button> buttons = new ArrayList<>();

		for (TicketType type : TicketType.values()) {
			buttons.add(type.getButton());
		}

		channel.sendMessageEmbeds(MessageConfiguration.TICKET_CONFIGURATION.getEmbed().format(event.getMember()).build())
				.setActionRow(buttons)
				.submit().whenComplete((s, b) -> {
			event.reply("Sent the message!").setEphemeral(true).queue();
		});

	}

}
