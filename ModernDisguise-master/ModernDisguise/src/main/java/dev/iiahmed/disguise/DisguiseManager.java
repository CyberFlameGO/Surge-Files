package dev.iiahmed.disguise;

import dev.iiahmed.disguise.listener.PlayerListener;
import dev.iiahmed.disguise.placeholder.PAPIExpantion;
import dev.iiahmed.disguise.vs.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public final class DisguiseManager {

    private static DisguiseProvider PROVIDER;
    private static boolean expantionRegistered = false;

    static {
        try {
            System.out.println(DisguiseUtil.VERSION);
            switch (DisguiseUtil.VERSION) {
                case "1_8_R3":
                    PROVIDER = new VS1_8_R3();
                    break;
                case "1_20_R1":
                    PROVIDER = new VS1_20_R1();
                    break;
                default:
                    PROVIDER = new VS1_19_R3();
                    break;
            }
        } catch (Exception e) {
            PROVIDER = new VS1_19_R3();
            e.printStackTrace();
        }
    }

    /**
     * Sets the plugin for the provider and registers the litsners
     */
    public static void setPlugin(@NotNull final Plugin plugin) {
        final Plugin old = PROVIDER.getPlugin();
        if (old == null || !old.isEnabled()) {
            PROVIDER.plugin = plugin;
            plugin.getServer().getPluginManager().registerEvents(new PlayerListener(), plugin);
        }
    }

    /**
     * @return the available DisguiseProvider for current version
     */
    @NotNull
    public static DisguiseProvider getProvider() {
        return PROVIDER;
    }

    /**
     * Registers a PlaceholderAPI expantion if PAPI exists
     */
    public static void registerExpantion() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") == null) {
            return;
        }
        if (!expantionRegistered) {
            new PAPIExpantion().register();
            expantionRegistered = true;
        }
    }

}
