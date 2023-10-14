package dev.lbuddyboy.bot.ticket.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.bot.redis.RedisHandler;
import dev.lbuddyboy.bot.sync.cache.SyncInformation;
import dev.lbuddyboy.bot.ticket.Ticket;
import dev.lbuddyboy.bot.utils.gson.GSONUtils;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.Packet;
import dev.lbuddyboy.bot.utils.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.listener.PacketListener;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TicketMessagePacket implements Packet, PacketListener {

    private Ticket ticket;
    private Ticket.TicketMessage message;
    private SyncInformation information;
    private boolean discord;
    private JsonObject object = new JsonObject();

    public TicketMessagePacket(Ticket ticket, Ticket.TicketMessage message, SyncInformation information, boolean discord) {
        this.ticket = ticket;
        this.message = message;
        this.information = information;
        this.discord = discord;
    }

    @Override
    public int id() {
        return 2800;
    }

    @Override
    public JsonObject serialize() {

        this.object.addProperty("discord", this.discord);
        this.object.addProperty("message", GSONUtils.getGSON().toJson(this.message, GSONUtils.TICKET_MESSAGE));
        this.object.addProperty("information", GSONUtils.getGSON().toJson(this.information, GSONUtils.SYNC_INFORMATION));
        this.object.add("ticket", this.ticket.serialize());

        return this.object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public boolean discord() {
        return this.object.get("discord").getAsBoolean();
    }

    public Ticket ticket() {
        return Ticket.deserialize(this.object.get("ticket").getAsJsonObject());
    }

    public Ticket.TicketMessage ticketMessage() {
        return GSONUtils.getGSON().fromJson(this.object.get("message").getAsString(), GSONUtils.TICKET_MESSAGE);
    }

    public SyncInformation information() {
        return GSONUtils.getGSON().fromJson(this.object.get("information").getAsString(), GSONUtils.SYNC_INFORMATION);
    }

    @Override
    public void send(PidginHandler pidgin) {
        if (!RedisHandler.isEnabled()) {
            serialize();
            onReceive(this);
            return;
        }

        Packet.super.send(pidgin);
    }

    @IncomingPacketHandler
    public void onReceive(TicketMessagePacket packet) {
        if (!packet.discord()) return;

    }

}
