package dev.aurapvp.samurai.essential.rollback.listener;

import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.player.SamuraiPlayer;
import dev.aurapvp.samurai.util.HashUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class DeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Player killer = player.getKiller();
        SamuraiPlayer samuraiPlayer = Samurai.getInstance().getPlayerHandler().loadPlayer(player.getUniqueId(), true);
        SamuraiPlayer killerPlayer = killer == null ? null : Samurai.getInstance().getPlayerHandler().loadPlayer(killer.getUniqueId(), true);

        PlayerDeath death = new PlayerDeath(
                UUID.randomUUID(),
                player.getUniqueId(),
                killer == null ? null : killer.getUniqueId(),
                event.getDeathMessage(),
                System.currentTimeMillis(),
                player.getInventory().getArmorContents(),
                player.getInventory().getStorageContents(),
                killer == null ? new ItemStack[4] : killer.getInventory().getArmorContents(),
                killer == null ? new ItemStack[36] : killer.getInventory().getStorageContents(),
                player.getFoodLevel(),
                killer == null ? -1 : killer.getFoodLevel(),
                killer == null ? -1 : killer.getHealth(),
                HashUtil.encryptUsingKey(player.getAddress().getAddress().getHostAddress()),
                killer == null ? "" : HashUtil.encryptUsingKey(killer.getAddress().getAddress().getHostAddress()),
                samuraiPlayer.getKills(),
                samuraiPlayer.getDeaths(),
                killerPlayer == null ? -1 : killerPlayer.getKills(),
                killerPlayer == null ? -1 : killerPlayer.getDeaths()
        );

        if (killerPlayer != null) {
            killerPlayer.getPlayerKills().add(death);
            killerPlayer.updated();
        }

        samuraiPlayer.getPlayerDeaths().add(death);
        samuraiPlayer.updated();
    }

}
