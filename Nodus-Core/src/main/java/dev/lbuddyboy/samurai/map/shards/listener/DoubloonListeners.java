package dev.lbuddyboy.samurai.map.shards.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.ThreadLocalRandom;

public class DoubloonListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (Samurai.getInstance().getShardHandler().getOreChances().containsKey(event.getBlock().getType())) {
            event.getBlock().setMetadata("GEM_ANTI_DUPE", new FixedMetadataValue(Samurai.getInstance(), true));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (event.getBlock().hasMetadata("GEM_ANTI_DUPE")) {
            return;
        }

        if (Samurai.getInstance().getShardHandler().getOreChances().containsKey(event.getBlock().getType())) {
            if (percent(Samurai.getInstance().getShardHandler().getOreChances().get(event.getBlock().getType()))) {
                long added = Samurai.getInstance().getShardMap().addShards(event.getPlayer().getUniqueId(), ThreadLocalRandom.current().nextInt(1, (event.getPlayer().hasPermission("foxtrot.donor") ? 6 : 3)));
                String gem = added > 1 ? "Shards" : "Shard";
                event.getPlayer().sendMessage(CC.translate("&6&l[SHARDS] ") + CC.WHITE + "You found " + CC.GOLD + "+" + added + " " + gem + CC.WHITE + " while mining!");

            }
        }
    }

    public static boolean percent(double percent) {
        return percent > 0.0 && ThreadLocalRandom.current().nextDouble(100.0) >= 100.0 - percent;
    }

}
