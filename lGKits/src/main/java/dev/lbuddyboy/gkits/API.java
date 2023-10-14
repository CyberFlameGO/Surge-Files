package dev.lbuddyboy.gkits;

import dev.lbuddyboy.gkits.object.kit.GKit;
import org.bukkit.entity.Player;

public abstract class API {

    public abstract boolean attemptUse(Player player, GKit kit);
    public boolean attemptHit(Player attacker, Player victim) {
        return true;
    }

}
