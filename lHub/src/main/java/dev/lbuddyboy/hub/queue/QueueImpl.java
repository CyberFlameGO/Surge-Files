package dev.lbuddyboy.hub.queue;

import dev.lbuddyboy.hub.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public interface QueueImpl {

	String id();
	int getPosition(Player player);
	int getQueueSize(Player player);
	int getQueueSize(String queueName);
	String getQueueName(Player player);
	boolean inQueue(Player player);
	void addToQueue(Player player, String queue);
	List<String> getQueues();
	default int getCount(String queue) {
		return 0;
	}
	void update(Placeholder placeholder);
	default void loadPlaceholders() {

	}

}
