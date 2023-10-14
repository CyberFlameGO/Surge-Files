package dev.lbuddyboy.bot.packet.impl;

import com.google.gson.JsonObject;
import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.redis.RedisHandler;
import dev.lbuddyboy.bot.sync.SyncCode;
import dev.lbuddyboy.bot.utils.gson.GSONUtils;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.Packet;
import dev.lbuddyboy.bot.utils.pidgin.packet.handler.IncomingPacketHandler;
import dev.lbuddyboy.bot.utils.pidgin.packet.listener.PacketListener;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
public class SyncCodeUpdatePacket implements Packet, PacketListener {

    private List<SyncCode> codes;
    private JsonObject object = new JsonObject();

    public SyncCodeUpdatePacket(List<SyncCode> codes) {
        this.codes = codes;
    }

    @Override
    public int id() {
        return 2500;
    }

    @Override
    public JsonObject serialize() {

        this.object.addProperty("codes", GSONUtils.getGSON().toJson(this.codes, GSONUtils.SYNC_CODES));

        return this.object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public List<SyncCode> codes() {
        return GSONUtils.getGSON().fromJson(this.object.get("codes").getAsString(), GSONUtils.SYNC_CODES);
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
    public void onReceive(SyncCodeUpdatePacket packet) {
        Bot.getInstance().getSyncHandler().setSyncCodes(packet.codes());
    }

}
