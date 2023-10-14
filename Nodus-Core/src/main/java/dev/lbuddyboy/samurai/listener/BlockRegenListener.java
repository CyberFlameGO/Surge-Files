package dev.lbuddyboy.samurai.listener;

import com.google.common.collect.ImmutableSet;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.RegenUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BlockRegenListener implements Listener {

    private static final Set<Material> REGEN = new HashSet<>(Arrays.asList(
            Material.COBBLESTONE,
            Material.DIRT,
            Material.LEGACY_WOOD,
            Material.NETHERRACK,
            Material.LEGACY_LEAVES,
            Material.LEGACY_LEAVES_2
    ));

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (event.isCancelled() || Samurai.getInstance().getServerHandler().isAdminOverride(player)) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

        if ((team == null || !team.isMember(event.getPlayer().getUniqueId())) && (player.getItemInHand() != null && REGEN.contains(player.getItemInHand().getType()))) {
            RegenUtils.schedule(event.getBlock(), 1, TimeUnit.HOURS, (block) -> {}, (block) -> {
                Team currentTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());

                return !(currentTeam != null && currentTeam.isMember(player.getUniqueId()));
            });
        }
    }

    static {
        for (Material material : Material.values()) {
            if (material.name().endsWith("_LOG")) REGEN.add(material);
        }
    }

}
