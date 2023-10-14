package dev.aurapvp.samurai.essential.rollback;

import dev.aurapvp.samurai.essential.command.DeathsCommand;
import dev.aurapvp.samurai.essential.rollback.listener.DeathListener;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class RollbackHandler implements IModule {

    @Override
    public String getId() {
        return "rollbacks";
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
        Samurai.getInstance().getCommandManager().registerCommand(new DeathsCommand());
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new DeathListener(), Samurai.getInstance());
    }

    public List<PlayerDeath> fetchCache(UUID uuid) {
        return Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true).getPlayerDeaths();
    }

    public void updateCache(UUID uuid, List<PlayerDeath> deaths) {
        SamuraiPlayer player = Samurai.getInstance().getPlayerHandler().loadPlayer(uuid, true);

        player.setPlayerDeaths(deaths);
        player.updated();
    }

}
