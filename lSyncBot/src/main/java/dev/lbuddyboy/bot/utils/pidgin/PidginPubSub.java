package dev.lbuddyboy.bot.utils.pidgin;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.utils.pidgin.packet.Packet;
import dev.lbuddyboy.bot.utils.pidgin.packet.listener.PacketListenerData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

@AllArgsConstructor
public class PidginPubSub extends JedisPubSub {

    @Getter
    private String channel;

    @Override
    public void onMessage(String channel, String message) {

        if (!channel.equalsIgnoreCase(this.channel)) {
            return;
        }

        try {

            final String[] args = message.split(";");
            final Integer id = Integer.valueOf(args[0]);
            final Packet packet = Bot.getInstance().getRedisHandler().getPidginHandler().buildPacket(id);

            if (packet == null) {
                return;
            }

            packet.deserialize(PidginHandler.PARSER.parse(args[1]).getAsJsonObject());

            for (PacketListenerData listener : Bot.getInstance().getRedisHandler().getPidginHandler().getListeners()) {

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
