package dev.lbuddyboy.lqueue.api.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class QueuePlayer {

    private final String uniqueId;
    private int priority = 1;
    private long leftAt = -1, leftAtDuration = 10_000L, lastMessage;
    private boolean bot = false;

    public boolean canBeMessage(double delay) {
        return lastMessage + (delay * 1000L) < System.currentTimeMillis();
    }

    public long getLeftAtExpiry() {
        return (this.leftAt + leftAtDuration) - System.currentTimeMillis();
    }

    public boolean canBypass() {
        return priority <= -1;
    }

    public boolean isOfflineExpired() {
        return getLeftAtExpiry() <= 0;
    }

    public boolean isOnline() {
        return leftAt == -1;
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", this.uniqueId);
        object.addProperty("priority", priority);
        object.addProperty("leftAt", leftAt);
        object.addProperty("leftAtDuration", leftAtDuration);
        object.addProperty("lastMessage", lastMessage);
        object.addProperty("bot", bot);

        return object;
    }

    public static QueuePlayer deserialize(JsonObject object) {
        return new QueuePlayer(
                object.get("uuid").getAsString(),
                object.get("priority").getAsInt(),
                object.get("leftAt").getAsLong(),
                object.get("leftAtDuration").getAsLong(),
                object.get("lastMessage").getAsLong(),
                object.get("bot").getAsBoolean()
        );
    }

    public UUID getUuid() {
        return UUID.fromString(this.uniqueId);
    }

}
