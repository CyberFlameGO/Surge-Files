package dev.lbuddyboy.pcore.essential.rollback.listener;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.HashUtil;
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
        MineUser samuraiPlayer = pCore.getInstance().getMineUserHandler().tryMineUserAsync(player.getUniqueId());
        MineUser killerPlayer = killer == null ? null : pCore.getInstance().getMineUserHandler().tryMineUserAsync(killer.getUniqueId());

        PlayerDeath death = new PlayerDeath(
                UUID.randomUUID(),
                player.getUniqueId(),
                killer == null ? null : killer.getUniqueId(),
                event.getDeathMessage(),
                System.currentTimeMillis(),
                player.getInventory().getArmorContents(),
                player.getInventory().getContents(),
                killer == null ? new ItemStack[4] : killer.getInventory().getArmorContents(),
                killer == null ? new ItemStack[36] : killer.getInventory().getContents(),
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
            killerPlayer.flagUpdate();
        }

        samuraiPlayer.getPlayerDeaths().add(death);
        samuraiPlayer.flagUpdate();
    }

}
