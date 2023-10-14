package dev.lbuddyboy.samurai.util.object;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
    T execute(Jedis var1);
}
