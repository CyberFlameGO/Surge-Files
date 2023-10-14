package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class JumpPadListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (hasPlayerMoved(event)) {
            if (event.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
                if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
                    double x = event.getPlayer().getLocation().getDirection().getX();
                    double y = event.getPlayer().getLocation().getDirection().getY();
                    double z = event.getPlayer().getLocation().getDirection().getZ();

                    event.getPlayer().setVelocity(new Vector(
                            x * 2.5,
                            y + 1.0,
                            z * 2.5));

                    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.BLOCK_LADDER_STEP, 10.0F, 5.0F);
                    event.getPlayer().playEffect(event.getPlayer().getLocation(), Effect.STEP_SOUND, Material.SPONGE);
                }
            }
        }
    }

    public static boolean hasPlayerMoved(PlayerMoveEvent event) {
        return event.getTo().getX() != event.getFrom().getX() || event.getTo().getY() != event.getFrom().getY() || event.getTo().getZ() != event.getFrom().getZ();
    }

}
