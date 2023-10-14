package dev.lbuddyboy.bot.utils.pidgin.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;

public interface Packet {
    int id();

    JsonObject serialize();

    void deserialize(JsonObject var1);

    default void send(PidginHandler pidgin) {
        pidgin.sendPacket(this);
    }
}
