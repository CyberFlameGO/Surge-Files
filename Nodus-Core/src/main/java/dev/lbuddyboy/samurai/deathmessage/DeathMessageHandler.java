package dev.lbuddyboy.samurai.deathmessage;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.deathmessage.listeners.DamageListener;
import dev.lbuddyboy.samurai.deathmessage.objects.Damage;
import dev.lbuddyboy.samurai.deathmessage.trackers.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeathMessageHandler {

    private static Map<String, List<Damage>> damage = new HashMap<>();

    public static void init() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new DamageListener(), Samurai.getInstance());

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new TridentTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new GeneralTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new PVPTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new EntityTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new FallTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ArrowTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new VoidTracker(), Samurai.getInstance());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new BurnTracker(), Samurai.getInstance());
    }

    public static List<Damage> getDamage(Player player) {
        return (damage.get(player.getName()));
    }

    public static void addDamage(Player player, Damage addedDamage) {
        if (!damage.containsKey(player.getName())) {
            damage.put(player.getName(), new ArrayList<Damage>());
        }

        List<Damage> damageList = damage.get(player.getName());

        while (damageList.size() > 30) {
            damageList.remove(0);
        }

        damageList.add(addedDamage);
    }

    public static void clearDamage(Player player) {
        damage.remove(player.getName());
    }

}