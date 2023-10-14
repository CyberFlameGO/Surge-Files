package dev.lbuddyboy.lqueue.api.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class Queue {

    private String name;
    private List<QueuePlayer> queuePlayers;
    private long startedAt, stoppedAt;
    private int onlinePlayers, maxPlayers;
    private boolean whitelisted, paused;

    public void prioritize() {
        this.queuePlayers = this.queuePlayers.stream().sorted(new QueuePriorityComparator().reversed()).collect(Collectors.toList());
    }

    public void save(JedisPool pool) {
        try (Jedis jedis = pool.getResource()) {
            jedis.hset("Queues", this.name, serialize().toString());
        }
    }

    public QueuePlayer getQueuePlayer(UUID uuid) {
        for (QueuePlayer qp : this.queuePlayers) {
            String qpId = qp.getUniqueId();
            String uuidId = uuid.toString();

            if (qpId.equals(uuidId)) {
                return qp;
            }
        }
        return null;
    }

    public int getPosition(QueuePlayer player) {
        return this.queuePlayers.indexOf(player) + 1;
    }

    public boolean isOffline() {
        return this.stoppedAt != -1 && this.startedAt == -1;
    }

    public String status() {
        return !isOffline() ? whitelisted ? "&eWhitelisted" : "&aOnline" : "&cOffline";
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("name", this.name);

        JsonArray array = new JsonArray();
        for (QueuePlayer player : this.queuePlayers) {
            array.add(player.serialize());
        }

        object.add("queuePlayers", array);
        object.addProperty("startedAt", this.startedAt);
        object.addProperty("stoppedAt", this.stoppedAt);
        object.addProperty("onlinePlayers", this.onlinePlayers);
        object.addProperty("maxPlayers", this.maxPlayers);
        object.addProperty("whitelisted", this.whitelisted);
        object.addProperty("paused", this.paused);

        return object;
    }

    public static Queue deserialize(JsonObject object) {
        JsonArray array = object.get("queuePlayers").getAsJsonArray();
        List<QueuePlayer> queuePlayers = new ArrayList<>();

        for (JsonElement element : array) {
            queuePlayers.add(QueuePlayer.deserialize(element.getAsJsonObject()));
        }

        return new Queue(
                object.get("name").getAsString(),
                queuePlayers,
                object.get("startedAt").getAsLong(),
                object.get("stoppedAt").getAsLong(),
                object.get("onlinePlayers").getAsInt(),
                object.get("maxPlayers").getAsInt(),
                object.get("whitelisted").getAsBoolean(),
                object.get("paused").getAsBoolean()
        );
    }

}
