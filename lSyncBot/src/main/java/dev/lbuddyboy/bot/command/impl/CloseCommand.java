package dev.lbuddyboy.bot.command.impl;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.command.Command;
import dev.lbuddyboy.bot.ticket.Ticket;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/08/2021 / 4:07 AM
 * AuraBot / rip.orbit.bot.command
 */
public class CloseCommand extends Command {

	public CloseCommand() {
		super("close", Collections.emptyList(), Collections.emptyList(), DefaultMemberPermissions.enabledFor(Permission.VIEW_CHANNEL), "Closes a ticket.", "");
	}

	@Override
	public void send(SlashCommandInteractionEvent event) {
		if (event.getName().equals("close")) {
			Member member = event.getMember();

			if (event.getGuildChannel().getType() != ChannelType.TEXT) return;

			TextChannel channel = event.getGuildChannel().asTextChannel();
			Ticket ticket = Bot.getInstance().getTicketHandler().loadChannelTickets(channel.getIdLong());

			if (ticket == null) {
				event.reply("This is not a valid ticket. Please delete the ticket manually and contact an admin.").queue();
				return;
			}

			if (ticket.getSender() != member.getIdLong() && !member.hasPermission(channel, Permission.MESSAGE_MANAGE)) {
				event.reply("You do not have proper clearance to close a ticket.").queue();
				return;
			}

			ticket.close(channel, member);
			event.reply("This ticket will be closed in 3 seconds!").queue();
		}
	}
}
