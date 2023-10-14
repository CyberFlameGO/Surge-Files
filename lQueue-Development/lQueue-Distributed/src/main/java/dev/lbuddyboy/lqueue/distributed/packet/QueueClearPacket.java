package dev.lbuddyboy.lqueue.distributed.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.distributed.pidgin.packet.Packet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueueClearPacket implements Packet {

    private JsonObject object = new JsonObject();

    public QueueClearPacket(Queue queue, DistributionType type) {

        this.object.add("queue", queue.serialize());
        this.object.addProperty("distribution", type.name());
    }

    @Override
    public int id() {
        return 1000005;
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
