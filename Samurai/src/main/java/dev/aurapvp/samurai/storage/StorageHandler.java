package dev.aurapvp.samurai.storage;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.storage.impl.FlatFileStorage;
import dev.aurapvp.samurai.storage.impl.MongoStorage;
import dev.aurapvp.samurai.util.IModule;
import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;

@Getter
public class StorageHandler implements IModule {

    private IStorage storage;

    @Override
    public String getId() {
        return "storage";
    }

    @Override
    public void load(Samurai plugin) {
        FileConfiguration configuration = Samurai.getInstance().getConfig();

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
    public void unload(Samurai plugin) {

    }

}
