package dev.lbuddyboy.pcore.essential.rollback;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.command.DeathsCommand;
import dev.lbuddyboy.pcore.essential.rollback.listener.DeathListener;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class RollbackHandler implements IModule {

    @Override
    public void load(pCore plugin) {
        this.loadCommands();
        this.loadListeners();
    }

    @Override
    public void unload(pCore plugin) {
    }

    @Override
    public void save() {

    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().registerCommand(new DeathsCommand());
    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new DeathListener(), pCore.getInstance());
    }

    public List<PlayerDeath> fetchCache(UUID uuid) {
        return pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid).getPlayerDeaths();
    }

    public void updateCache(UUID uuid, List<PlayerDeath> deaths) {
        MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);

        player.setPlayerDeaths(deaths);
        player.flagUpdate();
    }

}
