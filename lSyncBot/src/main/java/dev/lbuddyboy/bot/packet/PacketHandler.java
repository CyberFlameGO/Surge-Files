package dev.lbuddyboy.bot.packet;

import dev.lbuddyboy.bot.packet.impl.SyncCodeUpdatePacket;
import dev.lbuddyboy.bot.packet.impl.SyncInformationUpdatePacket;
import dev.lbuddyboy.bot.packet.impl.SyncResetPacket;
import dev.lbuddyboy.bot.ticket.packet.TicketClosePacket;
import dev.lbuddyboy.bot.ticket.packet.TicketMessagePacket;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;

public class PacketHandler {

    public PacketHandler(PidginHandler pidgin) {
        pidgin.registerPacket(SyncCodeUpdatePacket.class);
        pidgin.registerPacket(SyncInformationUpdatePacket.class);
        pidgin.registerPacket(SyncResetPacket.class);
        pidgin.registerPacket(TicketMessagePacket.class);
        pidgin.registerPacket(TicketClosePacket.class);
        pidgin.registerListener(new SyncCodeUpdatePacket());
        pidgin.registerListener(new SyncInformationUpdatePacket());
        pidgin.registerListener(new SyncResetPacket());
        pidgin.registerListener(new TicketMessagePacket());
        pidgin.registerListener(new TicketClosePacket());
    }

}
