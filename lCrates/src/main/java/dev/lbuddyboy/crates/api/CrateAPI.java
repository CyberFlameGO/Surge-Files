package dev.lbuddyboy.crates.api;

import dev.lbuddyboy.crates.API;
import dev.lbuddyboy.crates.model.Crate;
import org.bukkit.entity.Player;

public class CrateAPI extends API {

    @Override
    public boolean attemptUse(Player player, Crate crate) {
        return false;
    }

}
