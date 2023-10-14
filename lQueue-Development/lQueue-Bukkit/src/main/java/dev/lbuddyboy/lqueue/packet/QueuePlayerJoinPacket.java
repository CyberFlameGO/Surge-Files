package dev.lbuddyboy.lqueue.packet;

import com.google.gson.JsonObject;
import dev.lbuddyboy.libs.pidgin.packet.Packet;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QueuePlayerJoinPacket implements Packet {

    private JsonObject object = new JsonObject();

    public QueuePlayerJoinPacket(String queue, QueuePlayer player, DistributionType type) {

        this.object.addProperty("queue", queue);
        this.object.add("player", player.serialize());
        this.object.addProperty("distribution", type.name());
    }

    @Override
    public int id() {
        return 10000025;
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

    public QueuePlayer getQueuePlayer() {
        return QueuePlayer.deserialize(this.object.get("player").getAsJsonObject());
    }

    public DistributionType distributionType() {
        return DistributionType.valueOf(this.object.get("distribution").getAsString());
    }

}
