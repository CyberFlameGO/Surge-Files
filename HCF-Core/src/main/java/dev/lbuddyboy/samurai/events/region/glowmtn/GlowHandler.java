package dev.lbuddyboy.samurai.events.region.glowmtn;

import dev.lbuddyboy.samurai.events.region.glowmtn.listeners.GlowListener;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.FileHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;

public class GlowHandler {

    private static File file;
    @Getter private final static String glowTeamName = "Glowstone";
    @Getter @Setter private GlowMountain glowMountain;

    private long lastReset = System.currentTimeMillis();

    public GlowHandler() {
        try {
            file = new File(Samurai.getInstance().getDataFolder(), "glowmtn.json");

            if (!file.exists()) {
                glowMountain = null;

                if (file.createNewFile()) {
                    Samurai.getInstance().getLogger().warning("Created a new glow mountain json file.");
                }
            } else {
                glowMountain = Samurai.GSON.fromJson(FileHelper.readFile(file), GlowMountain.class);
                Samurai.getInstance().getLogger().info("Successfully loaded the glow mountain from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int secs = getBroadcastInterval() * 20;

        Samurai.getInstance().getServer().getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
            getGlowMountain().reset();

            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Glowstone Mountain]" + ChatColor.GREEN + " All glowstone has been reset!");
            lastReset = System.currentTimeMillis();
        }, secs, secs);

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new GlowListener(), Samurai.getInstance());
    }

    public void save() {
        FileHelper.writeFile(file, Samurai.GSON.toJson(glowMountain));
    }

    public boolean hasGlowMountain() {
        return glowMountain != null;
    }

    public static Claim getClaim() {
        return Samurai.getInstance().getTeamHandler().getTeam(glowTeamName).getClaims().get(0); // null if no glowmtn is set!
    }

    private int getBroadcastInterval() {
        return Samurai.getInstance().getServerHandler().isHardcore() ? (90 * 60) : 60 * 15;
    }

    public long getResetTime() {
        long timeSinceLast = (System.currentTimeMillis() - lastReset) / 1000;

        return getBroadcastInterval() - timeSinceLast;
    }
}