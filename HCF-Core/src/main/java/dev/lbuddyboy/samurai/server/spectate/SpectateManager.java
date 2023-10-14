package dev.lbuddyboy.samurai.server.spectate;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.UUID;

/**
 * @author Kansio
 */
public class SpectateManager {

    public SpectateManager() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new SpectateListener(), Samurai.getInstance());
    }

    public static final ItemStack TELEPORTER = new ItemBuilder(Material.COMPASS, 1).displayName("§aPlayer Teleporter").build();
    private final ArrayList<UUID> spectators = new ArrayList<>();

    public void addSpectator(UUID player) {
        spectators.add(player);
    }

    public void removeSpectator(UUID player) {
        spectators.remove(player);
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        giveItems(player);
    }

    public void giveItems(Player player) {
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.setAllowFlight(true);
        player.setHealth(20);
        player.setGameMode(GameMode.SPECTATOR);

        player.getInventory().setItem(0, TELEPORTER);
    }

    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
    }

    public boolean isSpectator(UUID player) {
        return spectators.contains(player);
    }

    public ArrayList<UUID> getSpectators() {
        return spectators;
    }
}
