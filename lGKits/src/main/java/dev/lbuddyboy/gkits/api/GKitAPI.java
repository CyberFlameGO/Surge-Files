package dev.lbuddyboy.gkits.api;

import dev.lbuddyboy.gkits.API;
import dev.lbuddyboy.gkits.object.kit.GKit;
import org.bukkit.entity.Player;

public class GKitAPI extends API {

    @Override
    public boolean attemptUse(Player player, GKit kit) {
        return true;
    }

}
