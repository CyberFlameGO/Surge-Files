package dev.lbuddyboy.samurai.pvpclasses.pvpclasses;

import dev.lbuddyboy.samurai.pvpclasses.PvPClass;
import dev.lbuddyboy.samurai.pvpclasses.PvPClassHandler;
import dev.lbuddyboy.samurai.pvpclasses.pvpclasses.mage.MageEffect;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MageClass extends PvPClass implements Listener {

    /*
            Things commented with // CUSTOM
            are the 'unique' abilities, or things that have custom behaviour not seen by most other effects.
            An example is invis, whose passive cannot be used while its click is on cooldown.
            This is therefore commented with // CUSTOM
     */

    public static final Map<Material, MageEffect> MAGE_CLICK_EFFECTS = new HashMap<>();

    @Getter
    private static Map<String, Long> lastEffectUsage = new ConcurrentHashMap<>();
    @Getter
    private static Map<String, Float> energy = new ConcurrentHashMap<>();

    public static final int MAGE_RANGE = 20;
    public static final int EFFECT_COOLDOWN = 10 * 1000;
    public static final float MAX_ENERGY = 100;
    public static final float ENERGY_REGEN_PER_SECOND = 1;

    public MageClass() {
        super("Mage", 15, null);

        MAGE_CLICK_EFFECTS.put(Material.GREEN_DYE, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1), 35));
        MAGE_CLICK_EFFECTS.put(Material.WHEAT, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.HUNGER, 20 * 5, 0), 35));
        MAGE_CLICK_EFFECTS.put(Material.SPIDER_EYE, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WITHER, 20 * 5, 1), 40));
        MAGE_CLICK_EFFECTS.put(Material.GOLD_INGOT, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 5, 0), 45));
        MAGE_CLICK_EFFECTS.put(Material.COAL, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SLOW, 20 * 5, 0), 50));
        MAGE_CLICK_EFFECTS.put(Material.WHEAT_SEEDS, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 5, 0), 60));
        MAGE_CLICK_EFFECTS.put(Material.INK_SAC, MageEffect.fromPotionAndEnergy(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 5, 0), 75));

        new BukkitRunnable() {

            @Override
            public void run() {
                for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                    if (!PvPClassHandler.hasKitOn(player, MageClass.this) || Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
                        continue;
                    }

                    if (energy.containsKey(player.getName())) {
                        if (energy.get(player.getName()) == MAX_ENERGY) {
                            continue;
                        }

                        energy.put(player.getName(), Math.min(MAX_ENERGY, energy.get(player.getName()) + ENERGY_REGEN_PER_SECOND));
                    } else {
                        energy.put(player.getName(), 0F);
                    }

                    int manaInt = energy.get(player.getName()).intValue();

                    if (manaInt % 10 == 0) {
                        player.sendMessage(ChatColor.AQUA + "Mage Energy: " + ChatColor.GREEN + manaInt);
                    }
                }
            }

        }.runTaskTimer(Samurai.getInstance(), 15L, 20L);
    }

    @Override
    public boolean qualifies(PlayerInventory armor) {
        return wearingAllArmor(armor) &&
                armor.getHelmet().getType() == Material.GOLDEN_HELMET &&
                armor.getChestplate().getType() == Material.CHAINMAIL_CHESTPLATE &&
                armor.getLeggings().getType() == Material.CHAINMAIL_LEGGINGS &&
                armor.getBoots().getType() == Material.GOLDEN_BOOTS;
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0), true);

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
        }
    }

    @Override
    public void tick(Player player) {
        if (!player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, PotionEffect.INFINITE_DURATION, 1));
        }

        if (!player.hasPotionEffect(PotionEffectType.REGENERATION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, PotionEffect.INFINITE_DURATION, 0));
        }
        
        super.tick(player);
    }


    @Override
    public void remove(Player player) {
        energy.remove(player.getName());

        for (MageEffect mageEffect : MAGE_CLICK_EFFECTS.values()) {
            mageEffect.getLastMessageSent().remove(player.getName());
        }

        for (MageEffect mageEffect : MAGE_CLICK_EFFECTS.values()) {
            mageEffect.getLastMessageSent().remove(player.getName());
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT_") || !event.hasItem() || !MAGE_CLICK_EFFECTS.containsKey(event.getItem().getType()) || !PvPClassHandler.hasKitOn(event.getPlayer(), this) || !energy.containsKey(event.getPlayer().getName())) {
            return;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getPlayer().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Bard effects cannot be used while in spawn.");
            return;
        }

        if (Samurai.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You are in PvP Protection and cannot use Bard effects. Type '/pvp enable' to remove your protection.");
            return;
        }

        if (getLastEffectUsage().containsKey(event.getPlayer().getName()) && getLastEffectUsage().get(event.getPlayer().getName()) > System.currentTimeMillis() && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
            long millisLeft = getLastEffectUsage().get(event.getPlayer().getName()) - System.currentTimeMillis();

            double value = (millisLeft / 1000D);
            double sec = Math.round(10.0 * value) / 10.0;

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
            return;
        }

        MageEffect mageEffect = MAGE_CLICK_EFFECTS.get(event.getItem().getType());
        if (event.getItem().getType() == Material.INK_SAC && event.getItem().getDurability() != 0) {
            return;
        }

        if (mageEffect.getEnergy() > energy.get(event.getPlayer().getName())) {
            event.getPlayer().sendMessage(ChatColor.RED + "You do not have enough energy for this! You need " + mageEffect.getEnergy() + " energy, but you only have " + energy.get(event.getPlayer().getName()).intValue());
            return;
        }

        energy.put(event.getPlayer().getName(), energy.get(event.getPlayer().getName()) - mageEffect.getEnergy());

        getLastEffectUsage().put(event.getPlayer().getName(), System.currentTimeMillis() + EFFECT_COOLDOWN);
        SpawnTagHandler.addOffensiveSeconds(event.getPlayer(), SpawnTagHandler.getMaxTagTime());
        giveMageEffect(event.getPlayer(), mageEffect, false, true);

        InventoryUtils.removeAmountFromInventory(event.getPlayer().getInventory(), event.getItem(), 1);
    }

    public void giveMageEffect(Player source, MageEffect mageEffect, boolean friendly, boolean persistOldValues) {
        for (Player player : getNearbyPlayers(source, false)) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
                continue;
            }

            // CUSTOM
            // Bards can't get Strength.
            // Yes, that does need to use .equals. PotionEffectType is NOT an enum.
            if (PvPClassHandler.hasKitOn(player, this) && mageEffect.getPotionEffect() != null) {
                continue;
            }

            smartAddPotion(player, mageEffect.getPotionEffect(), persistOldValues, this);
        }
    }

    public List<Player> getNearbyPlayers(Player player, boolean friendly) {
        List<Player> valid = new ArrayList<>();
        Team sourceTeam = Samurai.getInstance().getTeamHandler().getTeam(player);

        // We divide by 2 so that the range isn't as much on the Y level (and can't be abused by standing on top of / under events)
        for (Entity entity : player.getNearbyEntities(MAGE_RANGE, MAGE_RANGE / 2, MAGE_RANGE)) {
            if (entity instanceof Player) {
                Player nearbyPlayer = (Player) entity;

                if (Samurai.getInstance().getPvPTimerMap().hasTimer(nearbyPlayer.getUniqueId())) {
                    continue;
                }

                if (sourceTeam == null) {
                    if (!friendly) {
                        valid.add(nearbyPlayer);
                    }

                    continue;
                }

                boolean isFriendly = sourceTeam.isMember(nearbyPlayer.getUniqueId());
                boolean isAlly = sourceTeam.isAlly(nearbyPlayer.getUniqueId());

                if (!friendly && isFriendly) {
                    valid.add(nearbyPlayer);
                } else if (!friendly && !isAlly) { // the isAlly is here so you can't give your allies negative effects, but so you also can't give them positive effects.
                    valid.add(nearbyPlayer);
                }
            }
        }

        valid.add(player);
        return (valid);
    }

}