package dev.lbuddyboy.samurai.custom.ability.items.exotic;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public final class KitDisabler extends AbilityItem implements Listener {

    public KitDisabler() {
        super("KitDisabler");

        this.name = "kit-disabler";
    }

    @Getter private static final Cooldown disabled = new Cooldown();

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
        int affected = 0;
        for (Entity entity : player.getNearbyEntities(15, 15, 15)) {
            if (!(entity instanceof Player target)) continue;
            if (team.getMembers().contains(target.getUniqueId())) continue;

            affected++;
            disabled.applyCooldown(target.getUniqueId(), 20);
        }

        MessageConfiguration.KIT_DISABLER_CLICKER.sendListMessage(player, "%ability-name%", this.getName(), "%amount%", affected);
        return true;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        cooldown.cleanUp();
        cooldown.removeCooldown(event.getPlayer());
        disabled.cleanUp();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        disabled.cleanUp();
    }

}
