package dev.lbuddyboy.samurai.events.region.glowmtn.listeners;

import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowHandler;
import dev.lbuddyboy.samurai.events.region.glowmtn.GlowMountain;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.NumberFormat;
import java.util.Locale;

import static org.bukkit.ChatColor.*;

public class GlowListener implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled((!event.getPlayer().isOp()));
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        GlowHandler glowHandler = Samurai.getInstance().getGlowHandler();
        GlowMountain glowMountain = glowHandler.getGlowMountain();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        if (location.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled((!event.getPlayer().isOp()));
            return;
        }

        // If its unclaimed, or the server doesn't even have a mountain, or not even glowstone, why continue?
        if (Samurai.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || !glowHandler.hasGlowMountain() || event.getBlock().getType() != Material.GLOWSTONE) {
            return;
        }

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
            return;
        }

        if(!glowMountain.getGlowstone().contains(location.toVector().toBlockVector())) {
            return;
        }

        // Right, we can break this glowstone block, lets do it.
        event.setCancelled(false);

        // Now, we will decrease the value of the remaining glowstone
        glowMountain.setRemaining(glowMountain.getRemaining() - 1);

        // Let's announce when a glow mountain is a half and fully mined
        double total = glowMountain.getGlowstone().size();
        double remaining = glowMountain.getRemaining();


        // Lets broadcast
        if (total == remaining) {
            Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + AQUA + " 50% of Glowstone has been mined!");
        } else if (remaining == 0) {
            Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + RED + "  All Glowstone has been mined!");
        }
    }

    public static double GLOWSTONE_RESET_COST = 10000.0;
    public static long lastUsed = 0;

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        Player player = event.getPlayer();
        Block clicked = event.getClickedBlock();
        Location location = clicked.getLocation();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        GlowHandler glowHandler = Samurai.getInstance().getGlowHandler();
        GlowMountain glowMountain = glowHandler.getGlowMountain();

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals(GlowHandler.getGlowTeamName())) {
            return;
        }

        if (!clicked.getType().name().contains("_BUTTON")) return;

        double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());

        if (balance < GLOWSTONE_RESET_COST) {
            player.sendMessage(CC.translate("&cYou need " + NumberFormat.getNumberInstance(Locale.US).format(GLOWSTONE_RESET_COST) + " to reset the Glowstone Mountain."));
            return;
        }

        if (lastUsed > System.currentTimeMillis()) {
            player.sendMessage(CC.translate("&cThis is currently on cooldown. Try again later."));
            return;
        }

        FrozenEconomyHandler.setBalance(player.getUniqueId(), balance - GLOWSTONE_RESET_COST);
        glowMountain.reset();
        lastUsed = System.currentTimeMillis() + 15_000L;

        Bukkit.broadcastMessage(CC.translate("&6[Glowstone Mountain] &a" + player.getName() + " &fhas just &apurchased&f the Glowstone Mountain reset and the Glowstone Mountain is now reset!"));

    }

/*
    @EventHandler
    public void onHour(HourEvent event) {
        // Every hour(event) -- Since you want it every two hours lets do it this way
        GlowHandler handler = Foxtrot.getInstance().getGlowHandler();

        if (!handler.hasGlowMountain()) {
            return;
        }

        // Check if its divisible by 2 (making it an even hour)
        if (event.getHour() % 2 == 0) {
            // Reset the glowstone
            handler.getGlowMountain().reset();

            // Broadcast the reset
            Bukkit.broadcastMessage(GOLD + "[Glowstone Mountain]" + GREEN + " All glowstone has been reset!");
        }
    }*/
}