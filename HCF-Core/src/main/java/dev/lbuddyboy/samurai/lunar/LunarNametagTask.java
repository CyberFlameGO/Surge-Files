package dev.lbuddyboy.samurai.lunar;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.nametag.impl.FoxtrotNametagProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LunarNametagTask implements Runnable {

    @Override
    public void run() {

        if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.LUNAR_NAMETAGS)) {
            return;
        }

        for (Player target : Bukkit.getOnlinePlayers()) {
            for (Player viewer : Bukkit.getOnlinePlayers()) {
                FoxtrotNametagProvider.updateLunarTag(target, viewer);
            }
        }
    }
}
