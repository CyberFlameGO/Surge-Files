package dev.lbuddyboy.hub.queue;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.queue.custom.CustomQueue;
import dev.lbuddyboy.hub.queue.custom.QueueListener;
import dev.lbuddyboy.hub.queue.impl.Custom;
import dev.lbuddyboy.hub.queue.impl.EzQueue;
import dev.lbuddyboy.hub.queue.impl.Portal;
import dev.lbuddyboy.hub.queue.impl.lQueue;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.object.BungeeServer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:26 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.queue.custom
 */

@Getter
public class QueueHandler implements lModule {

    private QueueImpl queueImpl;
    private final List<CustomQueue> customQueues;
    private final Map<UUID, BukkitTask> playerTaskMap;
    private final List<BungeeServer> servers;

    public QueueHandler() {
        this.customQueues = new ArrayList<>();
        this.servers = new ArrayList<>();
        this.playerTaskMap = new HashMap<>();
    }

    public CustomQueue getQueueByName(String name) {
        for (CustomQueue queue : this.customQueues) {
            if (queue.getName().equals(name)) {
                return queue;
            }
        }
        return null;
    }

    public CustomQueue getQueueByPlayer(Player player) {
        for (CustomQueue queue : this.customQueues) {
            if (queue.getPlayers().contains(player.getUniqueId())) {
                return queue;
            }
        }
        return null;
    }

    @Override
    public void load(lHub plugin) {
        reload();

        lHub.getInstance().getServer().getPluginManager().registerEvents(new QueueListener(), lHub.getInstance());
    }

    @Override
    public void unload(lHub plugin) {

    }

    public void reload() {
        String queueString = lHub.getInstance().getConfig().getString("queue");
        if (queueString.equalsIgnoreCase("ezqueue")) {
            this.queueImpl = new EzQueue();
        } else if (queueString.equalsIgnoreCase("portal")) {
            this.queueImpl = new Portal();
        } else if (queueString.equalsIgnoreCase("lqueue")) {
            this.queueImpl = new lQueue();
        }
        if (lHub.getInstance().getConfig().getString("queue").equalsIgnoreCase("custom")) {
            this.queueImpl = new Custom(this.customQueues);
            loadQueues();
        }

        loadPlaceholders();
    }

    public void loadQueues() {
        this.customQueues.clear();
        this.servers.clear();
        lHub.getInstance().getSettingsHandler().getPlaceholders().clear();
        for (String key : lHub.getInstance().getDocHandler().getQueueDoc().getConfig().getConfigurationSection("queues").getKeys(false)) {
            YamlConfiguration config = lHub.getInstance().getDocHandler().getQueueDoc().getConfig();
            String ip = config.getString("queues." + key + ".ip");
            int port = config.getInt("queues." + key + ".port");
            String name = config.getString("queues." + key + ".name");
            boolean paused = config.getBoolean("queues." + key + ".paused");
            CustomQueue customQueue = new CustomQueue(name, new LinkedList<>(), new BungeeServer(name, key, ip, port));
            customQueue.setPaused(paused);
            customQueue.load();
            this.customQueues.add(customQueue);
            Bukkit.getConsoleSender().sendMessage(CC.translate("&6[lHub] &fLoaded the '" + name + "' - " + ip + ":" + port));
        }
        for (CustomQueue queue : this.customQueues) {
            servers.add(queue.getServer());
        }
    }

    public void loadPlaceholders() {
        Bukkit.getScheduler().runTaskLater(lHub.getInstance(), () -> {
            queueImpl.loadPlaceholders();
        }, 20);
    }

    public BungeeServer getServer(String bungeeName) {
        return this.servers.stream().filter(bs -> Objects.equals(bs.getBungeeName(), bungeeName)).findFirst().get();
    }

}
