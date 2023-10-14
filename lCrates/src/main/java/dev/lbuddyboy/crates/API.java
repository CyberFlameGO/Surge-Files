package dev.lbuddyboy.crates;

import dev.lbuddyboy.crates.model.Crate;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class API {

    public abstract boolean attemptUse(Player player, Crate crate);
    public void onEnable() {

    }
    public void registerCrate(Crate crate) {

    }
    public void unregisterCrate(Crate crate) {

    }
    public int getKeys(UUID query, Crate crate) {
        return 0;
    }
    public void removeKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) - amount);
    }
    public void addKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) + amount);
    }
    public void setKeys(UUID query, Crate crate, int amount) {

    }

}
