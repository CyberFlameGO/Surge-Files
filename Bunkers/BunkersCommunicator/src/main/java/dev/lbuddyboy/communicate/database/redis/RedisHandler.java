package dev.lbuddyboy.communicate.database.redis;

import com.google.gson.Gson;
import dev.lbuddyboy.communicate.BunkersCom;
import dev.lbuddyboy.communicate.database.redis.sub.JedisSubscriber;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 07/09/2021 / 2:44 PM
 * BuddyLibs / me.lbuddyboy.core.dev.lbuddyboy.communicate.database.redis
 */

@Getter
public class RedisHandler {

	@Getter private static final Gson GSON = new Gson();
	private static BunkersCom instance;

	public RedisHandler(BunkersCom instance) {
		RedisHandler.instance = instance;

		connect();
	}

	private static final String GLOBAL_MESSAGE_CHANNEL = "JedisPacket:All";
	static final String PACKET_MESSAGE_DIVIDER = "||";

	public static void connect() {
		JedisPool connectTo = new JedisPool(new JedisPoolConfig(),
				instance.getConfig().getString("redis.host"), instance.getConfig().getInt("redis.port"), 20000,
				(instance.getConfig().getString("redis.auth.password").isEmpty() ? null : instance.getConfig().getString("redis.auth.password")),
				instance.getConfig().getInt("redis.channel-id", 0));

		Thread subscribeThread = new Thread(() -> {
			while (true) {
				try {
					Jedis jedis = connectTo.getResource();
					Throwable throwable = null;
					try {
						JedisSubscriber pubSub = new JedisSubscriber();
						jedis.subscribe(pubSub, GLOBAL_MESSAGE_CHANNEL);
					} catch (Throwable pubSub) {
						throwable = pubSub;
						throw pubSub;
					} finally {
						if (jedis == null) continue;
						if (throwable != null) {
							try {
								jedis.close();
							} catch (Throwable pubSub) {
								throwable.addSuppressed(pubSub);
							}
							continue;
						}
						jedis.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "lCore - Packet Subscribe Thread");
		subscribeThread.setDaemon(true);
		subscribeThread.start();
	}

	public void sendToAll(JedisPacket packet) {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),
				instance.getConfig().getString("redis.host"), instance.getConfig().getInt("redis.port"), 20000,
				(instance.getConfig().getString("redis.auth.password").isEmpty() ? null : instance.getConfig().getString("redis.auth.password")),
				instance.getConfig().getInt("redis.channel-id", 0));

		new Thread(() -> {
			try (Jedis jedis = jedisPool.getResource()) {
				String encodedPacket = packet.getClass().getName() + "||" + GSON.toJson(packet);
				jedis.publish(GLOBAL_MESSAGE_CHANNEL, encodedPacket);
			}
		}).start();
	}

	public static <T> T runRedisCommand(RedisCommand<T> redisCommand) {
		JedisPool jedisPool = new JedisPool(new JedisPoolConfig(),
				instance.getConfig().getString("redis.host"), instance.getConfig().getInt("redis.port"), 20000,
				(instance.getConfig().getString("redis.auth.password").isEmpty() ? null : instance.getConfig().getString("redis.auth.password")),
				instance.getConfig().getInt("redis.channel-id", 0));
		Jedis jedis = jedisPool.getResource();

		T result = null;
		try {
			result = redisCommand.execute(jedis);
		} catch (Exception e) {
			e.printStackTrace();
			if (jedis != null) {
				jedisPool.getResource();
				jedis = null;
			}
		} finally {
			if (jedis != null) {
				jedisPool.getResource();
			}
		}
		return result;
	}

}
