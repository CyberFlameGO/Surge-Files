//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.libs.pidgin.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.libs.pidgin.PidginHandler;

public interface Packet {
    int id();

    JsonObject serialize();

    void deserialize(JsonObject var1);

    default void send(PidginHandler pidgin) {
        pidgin.sendPacket(this);
    }
}
