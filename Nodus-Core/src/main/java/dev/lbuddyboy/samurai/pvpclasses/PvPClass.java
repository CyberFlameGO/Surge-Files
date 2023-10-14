package dev.lbuddyboy.samurai.pvpclasses;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.ArcherClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.MinerClass;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.RogueClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.BardClass;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public abstract class PvPClass implements Listener {

    @Getter String name;
    @Getter int warmup;
    @Getter List<Material> consumables;
    
    private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public PvPClass(String name, int warmup, List<Material> consumables) {
        this.name = name;
        this.warmup = warmup;
        this.consumables = consumables;

        // Reduce warmup on kit maps
        if (Samurai.getInstance().getMapHandler().isKitMap()) {
            this.warmup = 5;
        }
    }

    public void apply(Player player) {

    }

    public void tick(Player player) {

    }

    public void remove(Player player) {

    }

    public boolean canApply(Player player) {
        Team team = Samurai.getInstance().getTeamHandler().getTeam(player);
        if (this instanceof MinerClass) return true;
        if (this instanceof RogueClass) return true;
        if (this instanceof BardClass) return true;
        if (this instanceof ArcherClass) return true;
        if (team != null) {
            int i = 0;
            for (Player member : team.getOnlineMembers()) {
                if (PvPClassHandler.hasKitOn(member, this)) {
                    i++;
                }
                if (i > 3) break;
            }
            if (i > 3) {
                player.sendMessage(CC.translate("&cYour team has " + i + " " + getName() + " classes. The limit is 3."));
                return false;
            }
        }
        return (true);
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() <= -1) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return (true);
    }

    public abstract boolean qualifies(PlayerInventory armor);

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return (armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null);
    }

    public static void smartAddPotion(final Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
        setRestoreEffect(player, potionEffect);
    }

    @AllArgsConstructor
    public static class SavedPotion {

        @Getter PotionEffect potionEffect;
        @Getter long time;
        @Getter private boolean perm;

    }

    public static void setRestoreEffect(final Player player, final PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();
        Iterator var5 = activeList.iterator();

        while(var5.hasNext()) {
            PotionEffect active = (PotionEffect)var5.next();
            if (active.getType().equals(effect.getType())) {
                if (effect.getAmplifier() < active.getAmplifier()) {
                    return;
                }

                if (effect.getAmplifier() == active.getAmplifier() && effect.getDuration() < active.getDuration()) {
                    return;
                }

                restores.put(player.getUniqueId(), active.getType(), active);
                shouldCancel = false;
                break;
            }
        }

        player.addPotionEffect(effect);
        (new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    player.addPotionEffect(restores.get(player.getUniqueId(), effect.getType()));
                } catch (Exception ignored) {

                }
            }
        }).runTaskLater(Samurai.getInstance(), (effect.getDuration() + 10));
        if (shouldCancel && effect.getDuration() > 100 && (long)effect.getDuration() < 1000) {
            restores.remove(player.getUniqueId(), effect.getType());
        }

    }

}
