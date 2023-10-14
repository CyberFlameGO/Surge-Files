package dev.lbuddyboy.bot.ticket;

import com.google.gson.JsonParser;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import lombok.Getter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class TicketHandler implements IHandler {

    private final Map<Long, Ticket> tickets;

    public TicketHandler() {
        this.tickets = new ConcurrentHashMap<>();
    }

    @Override
    public void load(Bot instance) {
        loadTickets();
    }

    @Override
    public void unload(Bot plugin) {
        for (Map.Entry<Long, Ticket> entry : this.tickets.entrySet()) {
            insertTickets(entry.getKey(), entry.getValue());
        }
        this.tickets.clear();
    }

    @Override
    public void save() {
        for (Map.Entry<Long, Ticket> entry : this.tickets.entrySet()) {
            insertTickets(entry.getKey(), entry.getValue());
        }
    }

    public Ticket fetchCache(long id) {
        return this.tickets.get(id);
    }

    public void setTicket(long id, Ticket ticket) {
        this.tickets.put(id, ticket);
    }

    public void removeTicket(long id) {
        this.tickets.remove(id);
        deleteTicket(id);
    }

    public void insertTickets(long id, Ticket ticket) {
        Bson bson = Filters.eq("id", id);
        Document document = Bot.getInstance().getSyncHandler().getTicketsCollection().find(bson).first();
        boolean insert = false;

        if (document == null) {
            insert = true;
            document = new Document();
        }

        document.put("id", id);
        document.put("ticket", ticket.serialize().toString());
        document.put("channel", ticket.getChannelId());

        if (insert) {
            Bot.getInstance().getSyncHandler().getTicketsCollection().insertOne(document);
            return;
        }

        Bot.getInstance().getSyncHandler().getTicketsCollection().replaceOne(bson, document, new ReplaceOptions().upsert(true));
    }

    public void deleteTicket(long id) {
        Bson bson = Filters.eq("id", id);
        Document document = Bot.getInstance().getSyncHandler().getTicketsCollection().find(bson).first();

        if (document == null) {
            return;
        }

        Bot.getInstance().getSyncHandler().getTicketsCollection().deleteOne(bson);
    }

    public Ticket loadTickets(long id) {
        Bson bson = Filters.eq("id", id);
        Document document = Bot.getInstance().getSyncHandler().getTicketsCollection().find(bson).first();

        if (document == null || !document.containsKey("ticket")) {
            return null;
        }

        return Ticket.deserialize(new JsonParser().parse(document.getString("ticket")).getAsJsonObject());
    }

    public Ticket loadChannelTickets(long id) {
        Bson bson = Filters.eq("channel", id);
        Document document = Bot.getInstance().getSyncHandler().getTicketsCollection().find(bson).first();

        if (document == null) {
            return null;
        }

        return Ticket.deserialize(new JsonParser().parse(document.getString("ticket")).getAsJsonObject());
    }

    public void loadTickets() {
        for (Document document : Bot.getInstance().getSyncHandler().getTicketsCollection().find()) {
            if (!document.containsKey("ticket")) continue;

            Ticket ticket = Ticket.deserialize(new JsonParser().parse(document.getString("ticket")).getAsJsonObject());

            this.tickets.put(ticket.getSender(), ticket);
        }

    }

}
