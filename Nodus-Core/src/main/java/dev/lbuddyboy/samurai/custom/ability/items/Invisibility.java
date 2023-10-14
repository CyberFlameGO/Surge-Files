package dev.lbuddyboy.samurai.custom.ability.items;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.ability.AbilityItem;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.cooldown.Cooldown;
import lombok.Getter;
import net.minecraft.network.protocol.game.PacketPlayOutEntityEquipment;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class Invisibility extends AbilityItem implements Listener {

    private Map<UUID, BukkitTask> tasks;
    @Getter
    private static Cooldown invisiblity = new Cooldown();

    public Invisibility() {
        super("Invisibility");

        this.name = "invisibility";
        this.tasks = new HashMap<>();
    }

    @Override
    protected boolean onUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        setGlobalCooldown(player);
        setCooldown(player);
        consume(player, event.getItem());

        MessageConfiguration.INVISIBILITY_CLICKER.sendListMessage(player, "%ability-name%", this.getName());
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20 * 120, 1, true, false));
        player.setFireTicks(0);
        player.setArrowsInBody(0);
        invisiblity.applyCooldown(player.getEntityId(), 122);

        List<com.mojang.datafixers.util.Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();

        list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.f, net.minecraft.world.item.ItemStack.b));
        list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.e, net.minecraft.world.item.ItemStack.b));
        list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.d, net.minecraft.world.item.ItemStack.b));
        list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.c, net.minecraft.world.item.ItemStack.b));

        PacketPlayOutEntityEquipment packet = new PacketPlayOutEntityEquipment(player.getEntityId(), list);

        for (Entity entity : player.getNearbyEntities(50, 180, 50)) {
            if (entity instanceof Player other) {
                ((CraftPlayer) other).getHandle().c.a(packet);
            }
        }

        this.tasks.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
            showArmor(player);
            if (!this.tasks.get(player.getUniqueId()).isCancelled()) this.tasks.get(player.getUniqueId()).cancel();
            this.tasks.remove(player.getUniqueId());
        }, 20 * 60 * 2));

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if ((!(event.getEntity() instanceof Player player)) || (!(event.getDamager() instanceof Player damager))) {
            return;
        }

        if (!invisiblity.onCooldown(player.getEntityId())) {
            if (!invisiblity.onCooldown(damager.getEntityId())) return;

            damager.removePotionEffect(PotionEffectType.INVISIBILITY);
            showArmor(damager);
            if (!this.tasks.get(damager.getUniqueId()).isCancelled()) this.tasks.get(damager.getUniqueId()).cancel();
            this.tasks.remove(damager.getUniqueId());
            return;
        }

        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        showArmor(player);
        if (this.tasks.containsKey(player.getUniqueId())) {
            if (!this.tasks.get(player.getUniqueId()).isCancelled()) this.tasks.get(player.getUniqueId()).cancel();
            this.tasks.remove(player.getUniqueId());
        }
    }

    public void showArmor(Player player) {
        invisiblity.removeCooldown(player.getEntityId());
        player.sendMessage(CC.translate("&cYour full invisibility has been removed."));

        for (Player other : Bukkit.getOnlinePlayers()) {
            List<com.mojang.datafixers.util.Pair<EnumItemSlot, net.minecraft.world.item.ItemStack>> list = new ArrayList<>();

            list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.f, CraftItemStack.asNMSCopy(player.getInventory().getHelmet())));
            list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.e, CraftItemStack.asNMSCopy(player.getInventory().getChestplate())));
            list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.d, CraftItemStack.asNMSCopy(player.getInventory().getLeggings())));
            list.add(new com.mojang.datafixers.util.Pair<>(EnumItemSlot.c, CraftItemStack.asNMSCopy(player.getInventory().getBoots())));

            ((CraftPlayer) other).getHandle().c.a(new PacketPlayOutEntityEquipment(player.getEntityId(), list));
        }
    }

    public static class InvisibilityPacketAdapter extends PacketAdapter {

        public InvisibilityPacketAdapter() {
            super(Samurai.getInstance(), PacketType.Play.Server.ENTITY_EQUIPMENT);
        }

        @Override
        public void onPacketSending(PacketEvent event) {
            Integer entityId = event.getPacket().getIntegers().read(0);
            if (!invisiblity.onCooldown(entityId)) return;

            List<Pair<EnumWrappers.ItemSlot, ItemStack>> pairs = event.getPacket().getSlotStackPairLists().read(0);

            for (Pair<EnumWrappers.ItemSlot, ItemStack> pair : pairs) {
                if (pair.getFirst().equals(EnumWrappers.ItemSlot.FEET) || pair.getFirst().equals(EnumWrappers.ItemSlot.LEGS) ||
                        pair.getFirst().equals(EnumWrappers.ItemSlot.CHEST) || pair.getFirst().equals(EnumWrappers.ItemSlot.HEAD)) {
                    pair.setSecond(new ItemStack(Material.AIR));
                }
            }

            event.getPacket().getSlotStackPairLists().write(0, pairs);
        }
    }
}
