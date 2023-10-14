package dev.lbuddyboy.lqueue.distributed.pidgin.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.lqueue.distributed.pidgin.PidginHandler;

public interface Packet {

    int id();

    JsonObject serialize();

    void deserialize(JsonObject object);

    default void send(PidginHandler pidgin) {
        pidgin.sendPacket(this);
    }

}
