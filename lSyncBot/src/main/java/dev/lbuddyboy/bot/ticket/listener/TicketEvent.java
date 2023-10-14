package dev.lbuddyboy.bot.ticket.listener;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.config.impl.BotConfiguration;
import dev.lbuddyboy.bot.config.impl.MessageConfiguration;
import dev.lbuddyboy.bot.ticket.Ticket;
import dev.lbuddyboy.bot.ticket.TicketType;
import dev.lbuddyboy.bot.ticket.packet.TicketMessagePacket;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class TicketEvent extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        TextChannel channel = BotConfiguration.TICKET_CHANNEL.getChannelById(TextChannel.class);
        Member member = event.getMember();
        Guild guild = event.getGuild();

        if (guild == null) return;
        if (member == null) return;

        if (channel == null) {
            event.reply("[!] Ticket channel could not be found.").queue();
            return;
        }

        if (event.getChannel().getIdLong() != channel.getIdLong()) return;

        for (TicketType type : TicketType.values()) {
            if (!Objects.equals(event.getButton().getId(), type.getButton().getId())) continue;

            Category category = BotConfiguration.TICKET_CATEGORY.getCategoryById();

            if (category == null) {
                event.reply("[!] Ticket category could not be found.").queue();
                return;
            }

            Ticket ticketQuery = Bot.getInstance().getTicketHandler().fetchCache(member.getIdLong());

            if (ticketQuery != null) {
                event.reply("You already have an active ticket: " + guild.getTextChannelById(ticketQuery.getChannelId()).getAsMention()).queue();
                return;
            }

            guild.createTextChannel("ticket-" + member.getUser().getName().toLowerCase(), category)
                    .syncPermissionOverrides()
                    .addMemberPermissionOverride(member.getIdLong(), Arrays.asList(
                            Permission.VIEW_CHANNEL,
                            Permission.MESSAGE_ATTACH_FILES,
                            Permission.MESSAGE_HISTORY,
                            Permission.MESSAGE_SEND
                    ), Collections.emptyList())
                    .queue(textChannel -> {
                        event.reply("Your ticket has been created! You can view it " + textChannel.getAsMention() + ".")
                                .setEphemeral(true)
                                .queue(m -> m.deleteOriginal().queueAfter(3, TimeUnit.SECONDS), mex -> {
                                    if (mex != null) mex.printStackTrace();
                                });

                        textChannel.sendMessageEmbeds(MessageConfiguration.TICKET_OPEN_CONFIGURATION.getEmbed().format(member).build())
                                .setActionRow(Button.danger("close-button", "Close Ticket"))
                                .queue(m -> {

                                }, mex -> {
                                    if (mex != null) mex.printStackTrace();
                                });

                        Ticket ticket = new Ticket(
                                UUID.randomUUID(),
                                member.getIdLong(),
                                textChannel.getIdLong(),
                                System.currentTimeMillis(),
                                type.name(),
                                new ArrayList<>()
                        );

                        Bot.getInstance().getTicketHandler().setTicket(member.getIdLong(), ticket);
                        Bot.getInstance().getTicketHandler().insertTickets(member.getIdLong(), ticket.clone());
                    }, ex -> {
                        if (ex != null) ex.printStackTrace();

                    });
            return;
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        Member member = event.getMember();

        if (member == null) return;
        if (event.getGuildChannel().getType() != ChannelType.TEXT) return;

        Ticket ticket = Bot.getInstance().getTicketHandler().loadChannelTickets(event.getGuildChannel().getIdLong());

        if (ticket == null) return;

        Ticket.TicketMessage message = new Ticket.TicketMessage(event.getMessage().getContentRaw(), member.getEffectiveName(), member.getIdLong(), System.currentTimeMillis());
        ticket = Bot.getInstance().getTicketHandler().fetchCache(ticket.getSender());

        ticket.getHistory().add(message);
        Bot.getInstance().getTicketHandler().insertTickets(ticket.getSender(), ticket);
        if (!Bot.getInstance().getSyncHandler().isSynced(ticket.getSender())) return;
        new TicketMessagePacket(ticket, message, Bot.getInstance().getSyncHandler().getSyncInformation(ticket.getSender()), false).send(Bot.getInstance().getRedisHandler().getPidginHandler());
    }
}
