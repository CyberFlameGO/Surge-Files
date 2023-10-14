package dev.lbuddyboy.samurai.listener;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import java.util.concurrent.TimeUnit;


/**
 * Listener that limits the amount of entities in one chunk. STOLE FROM SODIUM
 */
public class EntityLimitListener implements Listener {

    private static final int MAX_CHUNK_GENERATED_ENTITIES = 25;
    private static final int MAX_NATURAL_CHUNK_ENTITIES = 25;

    private final Samurai plugin;
    
    private final static long TPS_CLEAR_DELAY = TimeUnit.SECONDS.toMillis(5L);
    private long lastTPSRun = 0L;

    public EntityLimitListener(Samurai plugin) {
        this.plugin = plugin;
    }
    
/*    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onCreatureSpawn1(CreatureSpawnEvent event){
        if(shouldClear()){
            for(Entity entity : plugin.getServer().getWorlds().get(0).getEntities()){
                if(entity instanceof Player || entity instanceof Projectile || entity instanceof ItemFrame ||
                        entity instanceof Cow || entity instanceof Pig || entity instanceof Minecart || entity instanceof Chicken){
                    continue;
                }

                if(entity instanceof Item item){

                    if(item.getItemStack() != null && item.getItemStack().getType().name().contains("DIAMOND")
                            && !item.getItemStack().getEnchantments().isEmpty())
                        continue;
                }

                entity.remove();
            }
        }
    }

    private boolean shouldClear(){
        if(!SOTWCommand.isSOTWTimer() &&
                lastTPSRun + TPS_CLEAR_DELAY < System.currentTimeMillis()){
            lastTPSRun = System.currentTimeMillis();
            return true;
        }

        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        int entities = plugin.getServer().getWorlds().get(0).getEntities().size() - onlinePlayers;

        return onlinePlayers >= 200 && entities >= 3500 || onlinePlayers >= 100 && entities >= 4000;
    }*/

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onCreatureSpawn(CreatureSpawnEvent event) {

    	Entity entity = event.getEntity();
        if (entity instanceof Squid) {
            event.setCancelled(true);
            return;
        }
        if (entity instanceof Warden) {
            return;
        }

        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) { // allow slimes to always split
            switch (event.getSpawnReason()) {
                case NATURAL:
                    if (event.getLocation().getChunk().getEntities().length > MAX_NATURAL_CHUNK_ENTITIES) {
                        event.setCancelled(true);
                    }
                    break;
                case CHUNK_GEN:
                    if (event.getLocation().getChunk().getEntities().length > MAX_CHUNK_GENERATED_ENTITIES) {
                        event.setCancelled(true);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
