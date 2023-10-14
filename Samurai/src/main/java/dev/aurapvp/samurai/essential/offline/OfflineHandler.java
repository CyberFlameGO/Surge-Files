package dev.aurapvp.samurai.essential.offline;

import dev.aurapvp.samurai.essential.command.EditOfflineCommand;
import dev.aurapvp.samurai.essential.offline.listener.OfflineListener;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class OfflineHandler implements IModule {

    @Override
    public String getId() {
        return "offline-inventories";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void save() {

    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().registerCommand(new EditOfflineCommand());
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new OfflineListener(), Samurai.getInstance());
    }

    public OfflineData fetchCache(UUID uuid) {
        return Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true).getOfflineData();
    }

    public void updateCache(UUID uuid, OfflineData data) {
        SamuraiPlayer player = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true);

        player.setOfflineData(data);
        player.updated();
    }

}
