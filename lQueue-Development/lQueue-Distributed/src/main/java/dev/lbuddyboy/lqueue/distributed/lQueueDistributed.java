package dev.lbuddyboy.lqueue.distributed;

import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.distributed.config.Config;
import dev.lbuddyboy.lqueue.distributed.listener.QueueUpdateListener;
import dev.lbuddyboy.lqueue.distributed.packet.*;
import dev.lbuddyboy.lqueue.distributed.pidgin.PidginHandler;
import dev.lbuddyboy.lqueue.distributed.thread.QueueUpdateThread;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class lQueueDistributed {

    @Getter
    private static lQueueDistributed instance;

    private final Config config;
    public boolean threads = true;
    private JedisPool jedisPool;
    private PidginHandler pidginHandler;

    public lQueueDistributed() {
        this.config = new Config();
        System.out.println(this.config);

        this.loadJedis();
        this.loadThreads();
        this.loadShutdownLogic();
    }

    private void loadThreads() {
        (new QueueUpdateThread(this)).start();
//        (new RedisClearThread()).start();
    }

    private void loadJedis() {
        this.jedisPool = new JedisPool(new JedisPoolConfig(),
                this.getConfig().getRedisHost(), this.getConfig().getRedisPort(), 20000,
                (this.getConfig().getRedisPassword() == null || this.getConfig().getRedisPassword().isEmpty() ? null : this.getConfig().getRedisPassword()),
                this.getConfig().getRedisDatabase());

        this.pidginHandler = new PidginHandler(this.getConfig().getRedisChannel(), this.jedisPool);
        this.pidginHandler.registerPacket(QueueAddPlayerPacket.class);
        this.pidginHandler.registerPacket(QueueClearPacket.class);
        this.pidginHandler.registerPacket(QueueCreatePacket.class);
        this.pidginHandler.registerPacket(QueueMessagePlayerPacket.class);
        this.pidginHandler.registerPacket(QueuePausedUpdatePacket.class);
        this.pidginHandler.registerPacket(QueuePlayerJoinPacket.class);
        this.pidginHandler.registerPacket(QueuePlayerQuitPacket.class);
        this.pidginHandler.registerPacket(QueueRemovePlayerPacket.class);
        this.pidginHandler.registerPacket(QueueSendPlayerPacket.class);
        this.pidginHandler.registerPacket(QueueStatusUpdatePacket.class);
        this.pidginHandler.registerListener(new QueueUpdateListener());

        lQueueAPI.loadAllQueues(this.jedisPool);
    }

    private void loadShutdownLogic() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!this.jedisPool.isClosed()) {
                this.jedisPool.close();
                threads = false;
            }
        }));
    }

    public static void main(String[] args) {
        instance = new lQueueDistributed();
    }

}