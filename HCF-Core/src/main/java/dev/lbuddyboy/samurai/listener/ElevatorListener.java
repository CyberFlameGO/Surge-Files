package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.Elevator;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class ElevatorListener implements Listener {

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Up")) {
            event.setLine(0, CC.translate("&g[Elevator]"));
            event.setLine(1, "Up");
        }
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Down")) {
            event.setLine(0, CC.translate("&g[Elevator]"));
            event.setLine(1, "Down");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(PlayerInteractEvent event) {

        Player player = event.getPlayer();

        if (event.getClickedBlock() == null) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !event.getClickedBlock().getType().name().contains("_SIGN")) {
            return;
        }

        BlockState blockState = event.getClickedBlock().getState();

        if (!(blockState instanceof Sign sign)) {
            return;
        }

        if (!sign.getLine(0).contains("[Elevator]")) {
            return;
        }

        Block block = player.getTargetBlock(null,(int)sign.getLocation().distance(player.getLocation()));

        if (!(block.getState() instanceof Sign)) {
            return;
        }

        Elevator elevator;

        try {
            elevator = Elevator.valueOf(ChatColor.stripColor(sign.getLine(1).toUpperCase()));
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            ex.printStackTrace();
            return;
        }

        Location toTeleport = elevator.getCalculatedLocation(sign.getLocation(),Elevator.Type.SIGN);

        if (toTeleport == null) {
            player.sendMessage(ChatColor.RED + "There was an issue trying to find a valid location!");
            return;
        }

        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport.add(0.5,0,0.5));
    }

}
