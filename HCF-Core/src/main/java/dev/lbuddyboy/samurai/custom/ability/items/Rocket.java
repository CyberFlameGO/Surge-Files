package dev.lbuddyboy.samurai.custom.ability.items;

import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Rocket extends AbilityItem implements Listener {

    public Rocket() {
        super("Rocket");
        this.name = "rocket";
    }

    public static final List<UUID> rocket = new ArrayList<>();

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        player.setVelocity(new Vector(
                player.getLocation().getDirection().getX() * 2.15,
                player.getLocation().getDirection().getY() * 2.15,
                player.getLocation().getDirection().getZ() * 2.15));

        rocket.add(player.getUniqueId());

        MessageConfiguration.ROCKET_CLICKER.sendListMessage(player
                , "%ability-name%", this.getName()
        );

        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDamageGuardianAngel(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (rocket.contains(event.getEntity().getUniqueId())) {
                Player player = (Player) event.getEntity();

                if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    rocket.remove(player.getUniqueId());
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.rocket.remove(event.getPlayer().getUniqueId());
    }

}
