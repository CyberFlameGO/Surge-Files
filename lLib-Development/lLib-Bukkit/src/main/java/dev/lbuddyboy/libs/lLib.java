package dev.lbuddyboy.libs;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class lLib extends JavaPlugin {

    @Getter private static lLib instance;

    private JedisPool jedisPool, backendJedisPool;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        if (this.getConfig().getBoolean("redis.use")) {
            this.jedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("redis.local.host"), this.getConfig().getInt("redis.local.port"), 20000, this.getConfig().getString("redis.local.auth.password").isEmpty() ? null : this.getConfig().getString("redis.local.auth.password"), this.getConfig().getInt("redis.local.channel-id", 0));
            this.backendJedisPool = new JedisPool(new JedisPoolConfig(), this.getConfig().getString("redis.backend.host"), this.getConfig().getInt("redis.backend.port"), 20000, this.getConfig().getString("redis.backend.auth.password").isEmpty() ? null : this.getConfig().getString("redis.backend.auth.password"), this.getConfig().getInt("redis.backend.channel-id", 0));
        }
    }

/*    @Override
    public void onDisable() {
        if (this.getConfig().getBoolean("redis.use")) {
            this.jedisPool.close();
            this.backendJedisPool.close();
        }
    }*/

}
