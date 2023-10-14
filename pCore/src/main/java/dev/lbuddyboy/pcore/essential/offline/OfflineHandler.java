package dev.lbuddyboy.pcore.essential.offline;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.command.EditOfflineCommand;
import dev.lbuddyboy.pcore.essential.offline.listener.OfflineListener;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;

import java.util.UUID;

@Getter
public class OfflineHandler implements IModule {

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
        pCore.getInstance().getCommandManager().registerCommand(new EditOfflineCommand());
    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new OfflineListener(), pCore.getInstance());
    }

    public OfflineData fetchCache(UUID uuid) {
        return pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid).getOfflineData();
    }

    public void updateCache(UUID uuid, OfflineData data) {
        MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);

        player.setOfflineData(data);
        player.flagUpdate();
    }

}
