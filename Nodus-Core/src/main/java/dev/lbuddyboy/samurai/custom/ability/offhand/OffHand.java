package dev.lbuddyboy.samurai.custom.ability.offhand;

import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public abstract class OffHand extends AbilityItem implements Listener {

    private boolean agro;

    public OffHand(String statistic, boolean agro, String name) {
        super(statistic);
        this.agro = agro;
        this.name = name;
    }

    public abstract void onDamage(EntityDamageByEntityEvent event);

    @EventHandler
    public void onDamageOffHand(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (victim.getInventory().getItemInOffHand() == null) return;
        if (victim.getInventory().getItemInOffHand().getType() == Material.AIR) return;
        if (!isPartnerItem(victim.getInventory().getItemInOffHand())) return;

        onDamage(event);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getPlayer().getInventory().getItemInOffHand() == null) return;
        if (event.getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR) return;
        if (!isPartnerItem(event.getPlayer().getInventory().getItemInOffHand())) return;
        if (event.getBlock().getType() != event.getPlayer().getInventory().getItemInOffHand().getType()) return;

        event.setCancelled(true);
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        return false;
    }

}
