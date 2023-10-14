package dev.lbuddyboy.hub.queue.impl;

import com.google.gson.JsonObject;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.queue.QueueImpl;
import me.joeleoli.portal.shared.jedis.JedisAction;
import me.joeleoli.portal.shared.queue.QueueRank;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:26 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.impl
 */
public class Portal implements QueueImpl {
    @Override
    public String id() {
        return "PORTAL";
    }

    @Override
    public int getPosition(Player player) {
        return inQueue(player) ? getQueue(player).getPosition(player.getUniqueId()) : -1;
    }

    @Override
    public int getQueueSize(Player player) {
        return inQueue(player) ? getQueue(player).getPlayers().size() : 0;
    }

    @Override
    public int getQueueSize(String queueName) {
        return getQueue(queueName).getPlayers().size();
    }

    @Override
    public String getQueueName(Player player) {
        return inQueue(player) ? getQueue(player).getName() : "";
    }

    @Override
    public boolean inQueue(Player player) {
        return getQueue(player) != null;
    }

    @Override
    public void addToQueue(Player bukkitPlayer, String queue) {
        if (bukkitPlayer.hasPermission("portal.bypass")) {
            JsonObject data = new JsonObject();
            data.addProperty("uuid", bukkitPlayer.getUniqueId().toString());
            data.addProperty("server", queue);
            me.joeleoli.portal.bukkit.Portal.getInstance().getPublisher().write("portal-bukkit", JedisAction.SEND_PLAYER_SERVER, data);
            return;
        }
        QueueRank queueRank = me.joeleoli.portal.bukkit.Portal.getInstance().getPriorityProvider().getPriority(bukkitPlayer);
        JsonObject rank = new JsonObject();
        rank.addProperty("name", queueRank.getName());
        rank.addProperty("priority", queueRank.getPriority());
        JsonObject player = new JsonObject();
        player.addProperty("uuid", bukkitPlayer.getUniqueId().toString());
        player.add("rank", rank);
        JsonObject data = new JsonObject();
        data.addProperty("queue", queue);
        data.add("player", player);
        me.joeleoli.portal.bukkit.Portal.getInstance().getPublisher().write("portal-independent", JedisAction.ADD_PLAYER, data);
    }

    @Override
    public List<String> getQueues() {
        return me.joeleoli.portal.shared.queue.Queue.getQueues().stream().map(me.joeleoli.portal.shared.queue.Queue::getName).collect(Collectors.toList());
    }

    @Override
    public void update(Placeholder placeholder) {

    }

    public me.joeleoli.portal.shared.queue.Queue getQueue(Player player) {
        return me.joeleoli.portal.shared.queue.Queue.getByPlayer(player.getUniqueId());
    }

    public me.joeleoli.portal.shared.queue.Queue getQueue(String queue) {
        return me.joeleoli.portal.shared.queue.Queue.getByName(queue);
    }

}
