package dev.lbuddyboy.lqueue.placeholder;

import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.lQueue;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Locale;

@RequiredArgsConstructor
public class PAPIHook extends PlaceholderExpansion {

    private final lQueue plugin;

    @Override
    public String getIdentifier() {
        return "lQueue";
    }

    @Override
    public String getAuthor() {
        return "LBuddyboy";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String holder) {

        for (Queue queue : lQueueAPI.getQueues()) {
            if (holder.equalsIgnoreCase("queue_" + queue.getName() + "_online")) {
                return NumberFormat.getInstance(Locale.ENGLISH).format((SpoofHandler.getSpoofedCount(queue.getOnlinePlayers())));
            }
            if (holder.equalsIgnoreCase("queue_" + queue.getName() + "_status")) {
                return queue.status();
            }
            if (holder.equalsIgnoreCase("queue_" + queue.getName() + "_paused")) {
                return queue.isPaused() ? "&c&lCLOSED" : "&a&lJOINABLE";
            }
        }

        return null;
    }
}
