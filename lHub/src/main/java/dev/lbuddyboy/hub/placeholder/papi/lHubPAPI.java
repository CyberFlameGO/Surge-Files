package dev.lbuddyboy.hub.placeholder.papi;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.listener.BungeeListener;
import dev.lbuddyboy.hub.queue.QueueImpl;
import dev.lbuddyboy.hub.util.CC;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

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
        if (holder.equalsIgnoreCase("bungee_total")) {
            return NumberFormat.getInstance(Locale.ENGLISH).format(BungeeListener.PLAYER_COUNT);
        }
        if (holder.equalsIgnoreCase("queue_Bunkers_online")) {
            QueueImpl queueImpl = lHub.getInstance().getQueueHandler().getQueueImpl();

            int count = 0;
            for (String queue : queueImpl.getQueues()) {
                if (queue.contains("Bunkers")) count += queueImpl.getCount(queue);
            }

            return String.valueOf(count);
        }

        return null;
    }
}
