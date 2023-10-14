package dev.lbuddyboy.lqueue.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.libs.pidgin.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueuePausedUpdatePacket implements Packet {

    private JsonObject object = new JsonObject();

    public QueuePausedUpdatePacket(String queue, boolean status, DistributionType type) {

        this.object.addProperty("queue", queue);
        this.object.addProperty("status", status);
        this.object.addProperty("distribution", type.name());
    }

    @Override
    public int id() {
        return 10000020;
    }

    @Override
    public JsonObject serialize() {
        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public String getQueue() {
        return this.object.get("queue").getAsString();
    }

    public boolean status() {
        return this.object.get("status").getAsBoolean();
    }

    public DistributionType distributionType() {
        return DistributionType.valueOf(this.object.get("distribution").getAsString());
    }

}
