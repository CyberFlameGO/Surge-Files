package dev.lbuddyboy.lqueue.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.libs.pidgin.packet.Packet;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueUpdatePacket implements Packet {

    private JsonObject object = new JsonObject();

    public QueueUpdatePacket(Queue queue, DistributionType type) {

        this.object.add("queue", queue.serialize());
        this.object.addProperty("distribution", type.name());
    }

    @Override
    public int id() {
        return 10000050;
    }

    @Override
    public JsonObject serialize() {
        return object;
    }

    @Override
    public void deserialize(JsonObject object) {
        this.object = object;
    }

    public Queue getQueue() {
        return Queue.deserialize(this.object.get("queue").getAsJsonObject());
    }

    public DistributionType distributionType() {
        return DistributionType.valueOf(this.object.get("distribution").getAsString());
    }

}
