package dev.lbuddyboy.communicate.database.redis;

import dev.lbuddyboy.communicate.BunkersCom;

public interface JedisPacket {

    default BunkersCom getPlugin() {
        return BunkersCom.getInstance();
    }

    void onReceive();

    String getID();

    default void send() {
        getPlugin().getRedisHandler().sendToAll(this);
    }
}

