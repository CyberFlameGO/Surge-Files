package dev.lbuddyboy.samurai.events.region.cavern;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Stray;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;

public class Cavern {
    
    @Getter private final Map<String, String> ores = Maps.newHashMap();
    @Getter @Setter private int remaining = 0; // We don't need a whole set for numbers???
    
    public void scan() {
        ores.clear(); // clean storage
        
        Claim claim = CavernHandler.getClaim();
        
        World world = Bukkit.getWorld(claim.getWorld());
        for (int x = claim.getX1(); x <= claim.getX2(); x++) {
            for (int y = claim.getY1(); y <= claim.getY2(); y++) {
                for (int z = claim.getZ1(); z <= claim.getZ2(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (isOre(block.getType())) {
                        ores.put(toString(block.getLocation()), block.getType().name());
                    }
                }
            }
        }
    }

    public void reset() {
        World world = Bukkit.getWorld(CavernHandler.getClaim().getWorld());

        for (String location : ores.keySet()) {
            getBlockAt(world, location).setType(Material.valueOf(ores.get(location)));
        }

        for (Entity entity : world.getEntitiesByClasses(Stray.class)) {
            entity.remove();
        }

        Team team = Samurai.getInstance().getTeamHandler().getTeam(CavernHandler.getCavernTeamName());
        if (team != null && team.getHQ() != null) {
            for (int i = 0; i < 5; i++) {
                Stray stray = world.spawn(team.getHQ(), Stray.class);

                stray.setCustomName(CC.translate("&bCavern Sprite"));
                stray.setCustomNameVisible(true);
                stray.setMetadata("sprite", new FixedMetadataValue(Samurai.getInstance(), true));

            }
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
        return
                type == Material.EMERALD_BLOCK ||type == Material.IRON_BLOCK ||type == Material.GOLD_BLOCK ||type == Material.DIAMOND_BLOCK ||
                type == Material.DIAMOND_ORE || type == Material.EMERALD_ORE || type == Material.COAL_ORE || type == Material.IRON_ORE || type == Material.REDSTONE_ORE || type == Material.LEGACY_GLOWING_REDSTONE_ORE || type == Material.LAPIS_ORE || type == Material.GOLD_ORE;
    }
}