package dev.lbuddyboy.bot.redis;

import dev.lbuddyboy.bot.Bot;
import dev.lbuddyboy.bot.IHandler;
import dev.lbuddyboy.bot.packet.PacketHandler;
import dev.lbuddyboy.bot.utils.pidgin.PidginHandler;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class RedisHandler implements IHandler {

	@Getter private static boolean enabled;
	private JedisPool pool;
	private PidginHandler pidginHandler;

	@Override
	public void load(Bot instance) {
		this.pool = new JedisPool(new JedisPoolConfig(),
				instance.getConfig().getString("redis.host"), instance.getConfig().getInt("redis.port"), 20000,
				(instance.getConfig().getString("redis.auth.password").isEmpty() ? null : instance.getConfig().getString("redis.auth.password")),
				instance.getConfig().getInt("redis.channel-id", 0));
		this.pidginHandler = new PidginHandler(Bot.getInstance().getConfig().getString("redis.channel-name", "Flash:Global"), this.pool);

		new PacketHandler(this.pidginHandler);
		enabled = true;
	}

	@Override
	public void unload(Bot instance) {

	}

	public JedisPool getJedisPool() {
		if (!enabled) return null;

		return this.pool;
	}

}
