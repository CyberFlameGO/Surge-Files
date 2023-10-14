package dev.lbuddyboy.bot.ticket.listener;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.ticket.Ticket;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TicketCloseEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        Member member = event.getMember();

        if (event.getGuildChannel().getType() != ChannelType.TEXT) return;
        if (!Objects.equals(event.getButton().getId(), "close-button")) return;

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

        event.reply("This ticket will be closed in 3 seconds!").queue();
        ticket.close(channel, member);
    }

}
