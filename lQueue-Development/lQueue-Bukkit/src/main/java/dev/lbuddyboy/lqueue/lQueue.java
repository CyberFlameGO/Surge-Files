package dev.lbuddyboy.lqueue;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.libs.lLib;
import dev.lbuddyboy.libs.pidgin.PidginHandler;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.command.*;
import dev.lbuddyboy.lqueue.listener.OfflineQueueListener;
import dev.lbuddyboy.lqueue.listener.QueueUpdateListener;
import dev.lbuddyboy.lqueue.packet.*;
import dev.lbuddyboy.lqueue.placeholder.PAPIHook;
import dev.lbuddyboy.lqueue.thread.QueueUpdateThread;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Getter
public class lQueue extends JavaPlugin {

    @Getter
    private static lQueue instance;

    private PaperCommandManager commandManager;
    private JedisPool jedisPool;
    private PidginHandler pidginHandler;

    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();

        this.loadListeners();
        this.loadCommands();
        this.loadBungeeCord();
        this.loadJedis();
        this.setupLocalQueue();
        this.loadThreads();

        if (this.getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIHook(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (isHub()) {
            this.jedisPool.close();
            return;
        }

        Queue localQueue = getLocalQueue();

        localQueue.setStartedAt(-1);
        localQueue.setStoppedAt(System.currentTimeMillis());
        localQueue.setOnlinePlayers(0);

        updateLocalQueue();
        this.jedisPool.close();
    }

    private void loadListeners() {
        this.getServer().getPluginManager().registerEvents(new OfflineQueueListener(), this);
    }

    private void loadCommands() {
        this.commandManager = new PaperCommandManager(this);

        this.commandManager.getCommandCompletions().registerCompletion("queues", s -> lQueueAPI.getQueues().stream().map(Queue::getName).collect(Collectors.toList()));
        this.commandManager.registerCommand(new ClearQueueCommand());
        this.commandManager.registerCommand(new GoCommand());
        this.commandManager.registerCommand(new JoinQueueCommand());
        this.commandManager.registerCommand(new LeaveQueueCommand());
        this.commandManager.registerCommand(new QueuesCommand());
        this.commandManager.registerCommand(new QueueToggleCommand());
    }

    private void loadBungeeCord() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void loadJedis() {
        this.jedisPool = lLib.getInstance().getBackendJedisPool();

        this.pidginHandler = new PidginHandler(getConfig().getString("channel-name", "lQueue|Global"), this.jedisPool);
        this.pidginHandler.registerPacket(QueueAddPlayerPacket.class);
        this.pidginHandler.registerPacket(QueueClearPacket.class);
        this.pidginHandler.registerPacket(QueueCreatePacket.class);
        this.pidginHandler.registerPacket(QueueMessagePlayerPacket.class);
        this.pidginHandler.registerPacket(QueuePausedUpdatePacket.class);
        this.pidginHandler.registerPacket(QueuePlayerJoinPacket.class);
        this.pidginHandler.registerPacket(QueuePlayerQuitPacket.class);
        this.pidginHandler.registerPacket(QueueRemovePlayerPacket.class);
        this.pidginHandler.registerPacket(QueueSendPlayerPacket.class);
        this.pidginHandler.registerPacket(QueueStatusUpdatePacket.class);
        this.pidginHandler.registerListener(new QueueUpdateListener());

        lQueueAPI.loadAllQueues(this.jedisPool);
    }

    private void setupLocalQueue() {
        if (isHub()) return;

        String serverName = getConfig().getString("server-name");
        Queue localQueue = getLocalQueue();

        if (localQueue == null) {
            localQueue = lQueueAPI.createQueue(new Queue(serverName, new ArrayList<>(), System.currentTimeMillis(), -1, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), Bukkit.hasWhitelist(), true));
            this.pidginHandler.sendPacket(new QueueCreatePacket(localQueue, DistributionType.GLOBAL));
        }

        localQueue.setStartedAt(System.currentTimeMillis());
        localQueue.setStoppedAt(-1);
    }

    private void loadThreads() {
        if (isHub()) return;

        new QueueUpdateThread().start();
    }

    public void updateLocalQueue() {
        Queue localQueue = getLocalQueue();

        if (localQueue == null) return;

        localQueue.setWhitelisted(Bukkit.hasWhitelist());
        localQueue.setOnlinePlayers(Bukkit.getOnlinePlayers().size());
        localQueue.setMaxPlayers(Bukkit.getMaxPlayers());
        localQueue.save(this.jedisPool);

        this.pidginHandler.sendPacket(new QueueStatusUpdatePacket(localQueue, DistributionType.GLOBAL));
    }

    public Queue getLocalQueue() {
        return lQueueAPI.getQueueByName(getConfig().getString("server-name"));
    }

    public boolean isHub() {
        return getConfig().getBoolean("hub");
    }

    public int getPriority(Player player) {
        int priority = 1;
        for (String key : this.getConfig().getConfigurationSection("priorities").getKeys(false)) {
            if (player.hasPermission("rank." + key)) {
                priority = this.getConfig().getInt("priorities." + key);
            }
        }
        return priority;
    }

    public int getOfflineQueue(Player player) {
        for (String key : this.getConfig().getConfigurationSection("offline-queue").getKeys(false)) {
            if (player.hasPermission("rank." + key)) return this.getConfig().getInt("offline-queue." + key);
        }
        return 10;
    }

}
