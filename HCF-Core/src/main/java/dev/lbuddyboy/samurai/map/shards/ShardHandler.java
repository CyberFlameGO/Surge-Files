package dev.lbuddyboy.samurai.map.shards;

import dev.lbuddyboy.samurai.map.shards.listener.DoubloonListeners;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlipHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ShardHandler {

    public static final String DOUBLE_GEM_PREFIX = "&6&lDouble Shard";

    private final Map<Material, Double> oreChances = new HashMap<>();
    private final CoinFlipHandler coinFlipHandler;

    public ShardHandler() {
        Bukkit.getPluginManager().registerEvents(new DoubloonListeners(), Samurai.getInstance());
        this.coinFlipHandler = new CoinFlipHandler();
    }

    public void loadChances() {

        oreChances.put(Material.COAL_ORE, 2.5);
        oreChances.put(Material.IRON_ORE, 7.5);
        oreChances.put(Material.GOLD_ORE, 10.0);
        oreChances.put(Material.REDSTONE_ORE, 5.0);
        oreChances.put(Material.LAPIS_ORE, 5.0);
        oreChances.put(Material.DIAMOND_ORE, 30.0);
        oreChances.put(Material.EMERALD_ORE, 40.0);
    }

    public static boolean isDoubleGem() {
        return SOTWCommand.getCustomTimers().containsKey(DOUBLE_GEM_PREFIX);
    }
}
