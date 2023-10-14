package dev.lbuddyboy.crates.api;

import dev.lbuddyboy.crates.API;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.timer.impl.CombatTagTimer;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.user.model.KeyInfo;
import org.bukkit.entity.Player;

import java.util.UUID;

public class pCoreAPI extends API {

    @Override
    public boolean attemptUse(Player player, Crate crate) {
        for (PlayerTimer timer : pCore.getInstance().getTimerHandler().getPlayerTimers(player)) {
            if (timer instanceof CombatTagTimer) return false;
        }
        return true;
    }

    public int getKeys(UUID query, Crate crate) {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(query);
        if (user == null) return 0;
        KeyInfo info = user.getKeyInfo();

        return info.getVirtualKeys().getOrDefault(crate.getName(), 0);
    }

    public void removeKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) - amount);
    }

    public void addKeys(UUID query, Crate crate, int amount) {
        setKeys(query, crate, getKeys(query, crate) + amount);
    }

    public void setKeys(UUID query, Crate crate, int amount) {
        MineUser user = pCore.getInstance().getMineUserHandler().tryMineUserAsync(query);
        if (user == null) return;
        KeyInfo info = user.getKeyInfo();

        info.getVirtualKeys().put(crate.getName(), amount);
        user.flagUpdate();
    }

}
