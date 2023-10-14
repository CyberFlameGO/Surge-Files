package dev.aurapvp.samurai.util;

import dev.aurapvp.samurai.Samurai;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


public class ActionBarAPI {

    private static String nmsver;
    private static boolean useOldMethods = false;

    static {
        nmsver = Bukkit.getServer().getClass().getPackage().getName();
        nmsver = nmsver.substring(nmsver.lastIndexOf(".") + 1);

        if (nmsver.equalsIgnoreCase("v1_8_R1") || nmsver.startsWith("v1_7_")) { // Not sure if 1_7 works for the protocol hack?
            useOldMethods = true;
        }
    }

    public static void sendActionBar(Player player, String message) {
        if (!player.isOnline()) {
            return; // Player may have logged out
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(CC.translate(message)));
    }

    public static void sendActionBar(final Player player, final String message, int duration) {
        sendActionBar(player, message);

        if (duration >= 0) {
            // Sends empty message at the end of the duration. Allow messages shorter than 3 seconds, ensures precision.
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, "");
                }
            }.runTaskLater(Samurai.getInstance(), duration + 1);
        }

        // Re-sends the messages every 3 seconds, so it doesn't go away from the player's screen.
        while (duration > 40) {
            duration -= 40;
            new BukkitRunnable() {
                @Override
                public void run() {
                    sendActionBar(player, message);
                }
            }.runTaskLater(Samurai.getInstance(), duration);
        }
    }

    public static void sendActionBarToAllPlayers(String message) {
        sendActionBarToAllPlayers(message, -1);
    }

    public static void sendActionBarToAllPlayers(String message, int duration) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            sendActionBar(p, message, duration);
        }
    }
}