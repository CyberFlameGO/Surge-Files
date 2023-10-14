package dev.lbuddyboy.samurai.events.region.loothill.listeners;

import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.region.loothill.LootHillHandler;
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
import org.bukkit.event.player.PlayerInteractEvent;

import java.text.NumberFormat;
import java.util.Locale;

public class HillListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Location location = event.getBlock().getLocation();
        LootHillHandler abilityHillHandler = Samurai.getInstance().getLootHillHandler();
        Team teamAt = LandBoard.getInstance().getTeam(location);

        if (location.getWorld().getEnvironment() == World.Environment.THE_END) {
            event.setCancelled((!event.getPlayer().isOp()));
            return;
        }

        // If its unclaimed, or the server doesn't even have a mountain, or not even glowstone, why continue?
        if (Samurai.getInstance().getServerHandler().isUnclaimedOrRaidable(location) || event.getBlock().getType() != Material.DIAMOND_BLOCK) {
            return;
        }

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals("LootHill")) {
            return;
        }

        event.setDropItems(false);
        event.setCancelled(false);
        Samurai.getInstance().getLootHillHandler().getLootTable().open(event.getPlayer());
        abilityHillHandler.getLootHill().setRemaining(abilityHillHandler.getLootHill().getRemaining() - 1);
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

        // Check if the block broken is even in the mountain, and lets check the team to be safe
        if (teamAt == null || !teamAt.getName().equals("LootHill")) {
            return;
        }

        if (!clicked.getType().name().contains("_BUTTON")) return;

        double balance = FrozenEconomyHandler.getBalance(player.getUniqueId());

        if (balance < GLOWSTONE_RESET_COST) {
            player.sendMessage(CC.translate("&cYou need " + NumberFormat.getNumberInstance(Locale.US).format(GLOWSTONE_RESET_COST) + " to reset the Ability Hill."));
            return;
        }

        if (lastUsed > System.currentTimeMillis()) {
            player.sendMessage(CC.translate("&cThis is currently on cooldown. Try again later."));
            return;
        }

        FrozenEconomyHandler.setBalance(player.getUniqueId(), balance - GLOWSTONE_RESET_COST);
        Samurai.getInstance().getLootHillHandler().getLootHill().reset();
        lastUsed = System.currentTimeMillis() + 15_000L;

        Bukkit.broadcastMessage(CC.translate("&x&f&b&9&e&0&9[Ability Hill] &a" + player.getName() + " &fhas just &apurchased&f the Ability Hill reset and the Ability Blocks are now reset!"));

    }

}