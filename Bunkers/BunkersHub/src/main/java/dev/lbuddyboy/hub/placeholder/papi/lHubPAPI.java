package dev.lbuddyboy.hub.placeholder.papi;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.queue.QueueImpl;
import dev.lbuddyboy.hub.util.CC;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class lHubPAPI extends PlaceholderExpansion {

    private final lHub hub;

    @Override
    public String getIdentifier() {
        return "lhub";
    }

    @Override
    public String getAuthor() {
        return "LBuddyBoy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String holder) {
        if (holder.equalsIgnoreCase("queue_info")) {
            QueueImpl queueImpl = lHub.getInstance().getQueueHandler().getQueueImpl();

            if (queueImpl.inQueue(player)) {
                return CC.YELLOW + queueImpl.getQueueName(player) + " [" + queueImpl.getPosition(player) + "/" + queueImpl.getQueueSize(player) + "]";
            }

            return "&cNot Queued";
        }

        return null;
    }
}
