package dev.lbuddyboy.samurai.economy;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FrozenEconomyHandler {

    private static boolean initiated=false;

    private static final Map<UUID, Double> balances=new HashMap<>();

    private FrozenEconomyHandler() {
    }

    public static void init() {
        if (initiated) {
            return;
        }
        initiated=true;
        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent event) {
                FrozenEconomyHandler.save(event.getPlayer().getUniqueId());
            }
        }, Samurai.getInstance());
        Samurai.getInstance().runRedisCommand(redis -> {
            for ( String key : redis.keys("balance.*") ) {
                UUID uuid=UUID.fromString(key.substring(8));
                balances.put(uuid, Double.parseDouble(redis.get(key)));
            }
            return null;
        });
    }

    public static void setBalance(UUID uuid, double balance) {
        balances.put(uuid, balance);
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> FrozenEconomyHandler.save(uuid));
    }

    public static double getBalance(UUID uuid) {
        if (!balances.containsKey(uuid)) {
            FrozenEconomyHandler.load(uuid);
        }
        return balances.get(uuid);
    }

    public static void withdraw(UUID uuid, double amount) {
        FrozenEconomyHandler.setBalance(uuid, FrozenEconomyHandler.getBalance(uuid) - amount);
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> FrozenEconomyHandler.save(uuid));
    }

    public static void deposit(UUID uuid, double amount) {
        FrozenEconomyHandler.setBalance(uuid, FrozenEconomyHandler.getBalance(uuid) + amount);
        Bukkit.getScheduler().runTaskAsynchronously(Samurai.getInstance(), () -> FrozenEconomyHandler.save(uuid));
    }

    private static void load(UUID uuid) {
        Samurai.getInstance().runRedisCommand(redis -> {
            if (redis.exists("balance." + uuid.toString())) {
                balances.put(uuid, Double.parseDouble(redis.get("balance." + uuid)));
            } else {
                balances.put(uuid, 0.0);
            }
            return null;
        });
    }

    private static void save(UUID uuid) {
        Samurai.getInstance().runRedisCommand(redis -> redis.set("balance." + uuid.toString(), String.valueOf(FrozenEconomyHandler.getBalance(uuid))));
    }

    public static void saveAll() {
        Samurai.getInstance().runRedisCommand(redis -> {
            for ( Map.Entry<UUID, Double> entry : balances.entrySet() ) {
                redis.set("balance." + entry.getKey().toString(), String.valueOf(entry.getValue()));
            }
            return null;
        });
    }

    public static boolean isInitiated() {
        return initiated;
    }

    public static Map<UUID, Double> getBalances() {
        return balances;
    }
}

