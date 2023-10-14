package dev.lbuddyboy.samurai.events.region.cavern;

import dev.lbuddyboy.samurai.events.region.cavern.listeners.CavernListener;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.FileHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class CavernHandler {

    private static File file;
    @Getter private final static String cavernTeamName = "Cavern";
    @Setter private Cavern cavern;
    private final List<UUID> sprites;
    private long lastReset;

    public CavernHandler() {
        this.sprites = new ArrayList<>();
        this.lastReset = System.currentTimeMillis();
        try {
            file = new File(Samurai.getInstance().getDataFolder(), "cavern.json");

            if (!file.exists()) {
                cavern = null;

                if (file.createNewFile()) {
                    Samurai.getInstance().getLogger().warning("Created a new Cavern json file.");
                }
            } else {
                cavern = Samurai.GSON.fromJson(FileHelper.readFile(file), Cavern.class);
                Samurai.getInstance().getLogger().info("Successfully loaded the Cavern from file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Samurai.getInstance().getServer().getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
            if (getCavern() == null || Samurai.getInstance().getTeamHandler().getTeam(cavernTeamName) == null) return;
            getCavern().reset();
            // Broadcast the reset
            Bukkit.broadcastMessage(ChatColor.AQUA + "[Cavern]" + ChatColor.GREEN + " All ores have been reset!");
            lastReset = System.currentTimeMillis();
        }, 20 * 60 * 45, 20 * 60 * 45);

        Samurai.getInstance().getServer().getPluginManager().registerEvents(new CavernListener(), Samurai.getInstance());
    }

    public void save() {
        FileHelper.writeFile(file, Samurai.GSON.toJson(cavern));
    }

    public boolean hasCavern() {
        return cavern != null;
    }

    public static Claim getClaim() {
        return Samurai.getInstance().getTeamHandler().getTeam(cavernTeamName).getClaims().get(0); // null if no glowmtn is set!
    }


    public long getResetTime() {
        long timeSinceLast = (System.currentTimeMillis() - lastReset) / 1000;

        return (60 * 45) - timeSinceLast;
    }

}