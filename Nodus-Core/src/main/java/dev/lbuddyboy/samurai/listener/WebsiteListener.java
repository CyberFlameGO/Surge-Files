package dev.lbuddyboy.samurai.listener;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class WebsiteListener implements Listener {

    public WebsiteListener() {
        Bukkit.getLogger().info("Creating indexes...");
        DBCollection mongoCollection = Samurai.getInstance().getMongoPool().getDB(Samurai.MONGO_DB_NAME).getCollection("Deaths");
        
        mongoCollection.createIndex(new BasicDBObject("uuid", 1));
        mongoCollection.createIndex(new BasicDBObject("killerUUID", 1));
        mongoCollection.createIndex(new BasicDBObject("ip", 1));
        Bukkit.getLogger().info("Creating indexes done.");
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockPlace(BlockPlaceEvent event) {
        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
            case GOLD_ORE:
            case IRON_ORE:
            case COAL_ORE:
            case REDSTONE_ORE:
            case LEGACY_GLOWING_REDSTONE_ORE:
            case LAPIS_ORE:
            case EMERALD_ORE:
                event.getBlock().setMetadata("PlacedByPlayer", new FixedMetadataValue(Samurai.getInstance(), true));
                break;
        }
    }

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onBlockBreak(BlockBreakEvent event) {
        if ((event.getPlayer().getItemInHand() != null && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) || event.getBlock().hasMetadata("PlacedByPlayer")) {
            return;
        }

        switch (event.getBlock().getType()) {
            case DIAMOND_ORE:
                Samurai.getInstance().getDiamondMinedMap().setMined(event.getPlayer(), Samurai.getInstance().getDiamondMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case GOLD_ORE:
                Samurai.getInstance().getGoldMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getGoldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case IRON_ORE:
                Samurai.getInstance().getIronMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getIronMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case COAL_ORE:
                Samurai.getInstance().getCoalMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getCoalMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case REDSTONE_ORE:
            case LEGACY_GLOWING_REDSTONE_ORE:
                Samurai.getInstance().getRedstoneMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getRedstoneMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case LAPIS_ORE:
                Samurai.getInstance().getLapisMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getLapisMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
            case EMERALD_ORE:
                Samurai.getInstance().getEmeraldMinedMap().setMined(event.getPlayer().getUniqueId(), Samurai.getInstance().getEmeraldMinedMap().getMined(event.getPlayer().getUniqueId()) + 1);
                break;
        }
    }

}