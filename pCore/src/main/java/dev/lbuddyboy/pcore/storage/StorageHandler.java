package dev.lbuddyboy.pcore.storage;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.storage.impl.FlatFileStorage;
import dev.lbuddyboy.pcore.storage.impl.MongoStorage;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class StorageHandler implements IModule {

    private IStorage storage;

    @Override
    public void load(pCore plugin) {
        FileConfiguration configuration = pCore.getInstance().getConfig();

        switch (configuration.getString("storage-type").toUpperCase()) {
            case "MONGO": {
                this.storage = new MongoStorage().initialize();
                break;
            }
            default: {
                this.storage = new FlatFileStorage().initialize();
                break;
            }
        }
    }

    @Override
    public void unload(pCore plugin) {

    }

}
