package dev.lbuddyboy.lqueue.distributed.pidgin;

import dev.lbuddyboy.lqueue.distributed.pidgin.packet.Packet;
import dev.lbuddyboy.lqueue.distributed.pidgin.packet.listener.PacketListenerData;
import lombok.AllArgsConstructor;
import redis.clients.jedis.JedisPubSub;

@AllArgsConstructor
public class PidginPubSub extends JedisPubSub {

    private PidginHandler pidginHandler;
    private String channel;

    @Override
    public void onMessage(String channel, String message) {

        if (!channel.equalsIgnoreCase(this.channel)) {
            return;
        }

        try {

            final String[] args = message.split(";");
            final Integer id = Integer.valueOf(args[0]);
            final Packet packet = this.pidginHandler.buildPacket(id);

            if (packet == null) {
                return;
            }

            packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

            for (PacketListenerData listener : this.pidginHandler.getListeners()) {

                if (!listener.matches(packet)) {
                    continue;
                }

                listener.getMethod().invoke(listener.getInstance(), packet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
