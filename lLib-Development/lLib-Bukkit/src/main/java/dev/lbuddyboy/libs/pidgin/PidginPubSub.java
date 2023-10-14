//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.libs.pidgin;

import dev.lbuddyboy.libs.pidgin.packet.Packet;
import dev.lbuddyboy.libs.pidgin.packet.listener.PacketListenerData;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@Getter
public class PidginPubSub extends JedisPubSub {

    private final PidginHandler pidginHandler;
    private final String channel;

    public void onMessage(String channel, String message) {
        if (channel.equalsIgnoreCase(this.channel)) {
            try {
                String[] args = message.split(";");
                int id = Integer.parseInt(args[0]);
                Packet packet = this.pidginHandler.buildPacket(id);
                if (packet == null) {
                    return;
                }

                packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

                for (PacketListenerData listener : this.pidginHandler.getListeners()) {
                    if (listener.matches(packet)) {
                        listener.getMethod().invoke(listener.getInstance(), packet);
                    }
                }
            } catch (Exception var8) {
                var8.printStackTrace();
            }

        }
    }

    public PidginPubSub(PidginHandler pidginHandler, String channel) {
        this.pidginHandler = pidginHandler;
        this.channel = channel;
    }
}
