package dev.lbuddyboy.samurai.map.shards;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.map.shards.listener.DoubloonListeners;
import dev.lbuddyboy.samurai.util.CC;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.map.shards.coinflip.CoinFlipHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Getter
public class ShardHandler {

    public static final String DOUBLE_GEM_PREFIX = "&6&lDouble Shard";

    private final Map<Material, Double> oreChances = new HashMap<>();
    private final CoinFlipHandler coinFlipHandler;
    public static List<AbilityItem> rotation = new ArrayList<>();
    public static long lastRotation;
    public static int[] ITEMS_SLOTS = new int[] {
            23,24,25,
            30,31,32,33,34
    };

    public static void rotate() {
        rotation.clear();
        List<AbilityItem> items = Samurai.getInstance().getAbilityItemHandler().getAbilityItems();

        while (rotation.size() < 8) {
            AbilityItem item = items.get(ThreadLocalRandom.current().nextInt(0, items.size()));
            if (rotation.contains(item)) continue;

            rotation.add(item);
            rotation = rotation.stream().sorted(Comparator.comparingInt(AbilityItem::getShardCost)).collect(Collectors.toList());
        }
    }

    public static long nextRotation() {
        return (lastRotation + 30_000L * 60) - System.currentTimeMillis();
    }

    public ShardHandler() {
        Bukkit.getPluginManager().registerEvents(new DoubloonListeners(), Samurai.getInstance());
        this.coinFlipHandler = new CoinFlipHandler();
        Tasks.runAsyncTimer(() -> {
            rotate();
            lastRotation = System.currentTimeMillis();
            Bukkit.broadcastMessage(CC.translate("#fb7a04&l[#fb8204&lS#fb8903&lH#fc9103&lA#fc9802&lR#fca002&lD #fca702&lS#fcaf01&lH#fdb601&lO#fdbe00&lP#fdc500&l] &aThe shard shop has just rotated the ability items!"));
        }, 20, 20 * 60 * 30);
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
