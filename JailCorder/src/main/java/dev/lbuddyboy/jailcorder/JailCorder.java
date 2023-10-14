package dev.lbuddyboy.jailcorder;

import co.aikar.commands.PaperCommandManager;
import dev.lbuddyboy.jailcorder.command.RecordingsCommand;
import dev.lbuddyboy.jailcorder.command.context.UUIDContext;
import dev.lbuddyboy.jailcorder.database.MongoHandler;
import dev.lbuddyboy.jailcorder.listener.CacheListener;
import dev.lbuddyboy.jailcorder.listener.RecordingListener;
import dev.lbuddyboy.jailcorder.record.RecordingUser;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public final class JailCorder extends JavaPlugin {

    @Getter private static JailCorder instance;

    private Map<UUID, RecordingUser> recordingUsers;
    private MongoHandler mongoHandler;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        instance = this;
        this.recordingUsers = new ConcurrentHashMap<>();

        this.mongoHandler = new MongoHandler();
        this.commandManager = new PaperCommandManager(this);
        this.commandManager.getCommandContexts().registerContext(UUID.class, new UUIDContext());
        this.commandManager.registerCommand(new RecordingsCommand());

        this.getServer().getPluginManager().registerEvents(new CacheListener(), this);
        this.getServer().getPluginManager().registerEvents(new RecordingListener(), this);
    }

    @Override
    public void onDisable() {
        for (RecordingUser user : JailCorder.getInstance().getRecordingUsers().values()) {
            user.save(false);
        }
    }

}
