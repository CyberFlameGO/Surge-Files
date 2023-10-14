package dev.lbuddyboy.communicate.database.redis.sub;

import com.google.gson.Gson;
import dev.lbuddyboy.communicate.database.redis.JedisPacket;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor
public class JedisSubscriber extends redis.clients.jedis.JedisPubSub {

	@SneakyThrows
	@Override
	public void onMessage(String channel, String message) {
		Class<?> packetClass;
		int packetMessageSplit = message.indexOf("||");
		String packetClassStr = message.substring(0, packetMessageSplit);
		String messageJson = message.substring(packetMessageSplit + "||".length());
		try {
			packetClass = Class.forName(packetClassStr);
		} catch (ClassNotFoundException ignored) {
			return;
		}
		JedisPacket packet = (JedisPacket) new Gson().fromJson(messageJson, packetClass);
		if (packet != null) {
			packet.onReceive();
		}
	}
}

