package dev.lbuddyboy.samurai.events.region.cavern.listeners;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.cavern.Cavern;
import dev.lbuddyboy.samurai.events.region.cavern.CavernHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class CavernListener implements Listener {
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        CavernHandler cavernHandler = Samurai.getInstance().getCavernHandler();
        Cavern cavern = cavernHandler.getCavern();
        Team teamAt = LandBoard.getInstance().getTeam(location);
        
        // If its unclaimed, or the server doesn't even have a mountain, or not even glowstone, why continue?
        if (Samurai.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !cavernHandler.hasCavern()) {
            return;
        }
        
        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals(CavernHandler.getCavernTeamName())) {
            return;
        }
        
        if (!cavern.getOres().containsKey(Cavern.toString(location))) {
            return;
        }
        
        // Right, we can break this glowstone block, lets do it.
        event.setCancelled(false);
    }

}