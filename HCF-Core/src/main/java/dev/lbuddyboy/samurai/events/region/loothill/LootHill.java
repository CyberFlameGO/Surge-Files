package dev.lbuddyboy.samurai.events.region.loothill;

import com.google.common.collect.Maps;
import dev.lbuddyboy.samurai.events.region.cavern.CavernHandler;
import dev.lbuddyboy.samurai.team.claims.Claim;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Map;

public class LootHill {
    
    @Getter private final Map<String, String> locations = Maps.newHashMap();
    @Getter @Setter private int remaining = 0; // We don't need a whole set for numbers???
    
    public void scan() {
        locations.clear(); // clean storage
        
        Claim claim = LootHillHandler.getClaim();
        
        World world = Bukkit.getWorld(claim.getWorld());
        for (int x = claim.getX1(); x <= claim.getX2(); x++) {
            for (int y = claim.getY1(); y <= claim.getY2(); y++) {
                for (int z = claim.getZ1(); z <= claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (isOre(block.getType())) {
                        locations.put(toString(block.getLocation()), block.getType().name());
                    }
                }
            }
        }
    }

    public void reset() {
        remaining = 0;
        World world = Bukkit.getWorld(LootHillHandler.getClaim().getWorld());

        for (String location : locations.keySet()) {
            getBlockAt(world, location).setType(Material.valueOf(locations.get(location)));
            remaining++;
        }
    }

    public static String toString(Location location) {
        return new StringBuilder(Integer.toString(location.getBlockX())).append(',').append(location.getBlockY()).append(',').append(location.getBlockZ()).toString();
    }

    public static Block getBlockAt(World world, String location) {
        String[] xyz = location.split(",");
        return world.getBlockAt(Integer.parseInt(xyz[0]), Integer.parseInt(xyz[1]), Integer.parseInt(xyz[2]));
    }

    private boolean isOre(Material type) {
        return type == Material.DIAMOND_BLOCK;
    }
}