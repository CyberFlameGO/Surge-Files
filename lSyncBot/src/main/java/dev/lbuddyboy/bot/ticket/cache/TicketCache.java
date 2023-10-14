package dev.lbuddyboy.bot.ticket.cache;

import dev.lbuddyboy.bot.ticket.Ticket;
import lombok.Data;

@Data
public class TicketCache {

    private Ticket ticket;
    private long lastMentioned;

    public TicketCache(long id, Ticket ticket) {
        this.ticket = ticket;

//        Bot.getInstance().getTicketHandler().getTickets().put(id, this);
    }

}
