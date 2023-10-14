package dev.aurapvp.samurai.listener;

import dev.aurapvp.samurai.faction.FactionConfiguration;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class GeneralListener implements Listener {

    @EventHandler
    public void onBreakDistance(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = player.getLocation();

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) return;

        if ((location.getBlockX() < FactionConfiguration.WARZONE_RADIUS.getInt()
                && location.getBlockX() > -FactionConfiguration.WARZONE_RADIUS.getInt())
                && (location.getBlockZ() < FactionConfiguration.WARZONE_RADIUS.getInt()
                && location.getBlockZ() > -FactionConfiguration.WARZONE_RADIUS.getInt())) {
            if (player.getGameMode() == GameMode.CREATIVE && player.hasMetadata("build")) return;

            event.setCancelled(true);
            player.sendMessage(CC.translate("&cYou cannot build in the &c&lWARZONE&c!"));
        }
    }

}
