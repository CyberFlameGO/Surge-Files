package dev.lbuddyboy.lqueue.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import lombok.Getter;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class lQueueAPI {

    @Getter
    private static final List<Queue> queues = new ArrayList<>();
    @Getter
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .enableComplexMapKeySerialization()
            .create();

    public static Queue getQueueByName(String name) {
        for (Queue queue : queues) {
            if (queue.getName().equals(name)) return queue;
        }
        return null;
    }

    public static Queue getQueueByPlayer(UUID player) {
        for (Queue queue : queues) {
            QueuePlayer queuePlayer = queue.getQueuePlayer(player);
            if (queuePlayer == null) continue;

            return queue;
        }
        return null;
    }

    public static Queue createQueue(Queue queue) {
        queues.removeIf(q -> q.getName().equals(queue.getName()));
        queues.add(queue);
        return queue;
    }

    public static void loadAllQueues(JedisPool pool) {
        try (Jedis jedis = pool.getResource()) {
            for (Map.Entry<String, String> entry : jedis.hgetAll("Queues").entrySet()) {
                Queue queue = Queue.deserialize(new JsonParser().parse(entry.getValue()).getAsJsonObject());

                createQueue(queue);
            }
        }
    }

}
