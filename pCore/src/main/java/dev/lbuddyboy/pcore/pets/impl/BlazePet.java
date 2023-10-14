package dev.lbuddyboy.pcore.pets.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BlazePet implements IPet, Listener {

    private final Map<IntRange, IntRange> VARIETIES = new HashMap<>();
    private final Map<IntRange, Long> COOLDOWNS = new HashMap<>();
    private final Cooldown cooldown = new Cooldown(), multiplier = new Cooldown();
    private final Config config;
    private final double xpMultiplier;

    public BlazePet(Config config) {
        this.config = config;

        for (String s : this.config.getStringList("level-variations")) {
            String[] parts = s.split(";");
            IntRange levelRange = new IntRange(Integer.parseInt(parts[0].split("-")[0]), Integer.parseInt(parts[0].split("-")[1]));
            IntRange potionRange = new IntRange(Integer.parseInt(parts[1].split("-")[0]), Integer.parseInt(parts[1].split("-")[1]));

            VARIETIES.put(levelRange, potionRange);
        }

        for (String s : this.config.getStringList("cooldown-times")) {
            String[] parts = s.split(";");
            IntRange levelRange = new IntRange(Integer.parseInt(parts[0].split("-")[0]), Integer.parseInt(parts[0].split("-")[1]));

            COOLDOWNS.put(levelRange, Integer.parseInt(parts[1]) * 1_000L);
        }

        this.xpMultiplier = this.config.getDouble("multiplier");

        Bukkit.getPluginManager().registerEvents(this, pCore.getInstance());
    }

    @Override
    public String getName() {
        return "Blaze";
    }

    @Override
    public Config getConfig() {
        return this.config;
    }

    @Override
    public String getHeadURL() {
        return this.config.getString("texture", "282c2bf9d82f40d711eff5ad2d520baba3e7b4eab5101bfc4d0d86709fd0ea39");
    }

    @Override
    public String getDisplayName() {
        return this.config.getString("display-name", "&c&lBlaze Pet");
    }

    @Override
    public PetRarity getPetRarity() {
        return pCore.getInstance().getPetHandler().getPetRarity(this.config.getString("rarity", "legendary"));
    }

    @Override
    public List<String> getMenuLore() {
        return this.config.getStringList("menu-lore");
    }

    @Override
    public List<String> getLore() {
        return this.config.getStringList("lore");
    }

    @Override
    public int getMaxLevel() {
        return this.config.getInt("max-level", 100);
    }

    @Override
    public long getCooldownTime(int level) {
        for (IntRange levelRange : COOLDOWNS.keySet()) {
            if (level >= levelRange.getMin() && level <= levelRange.getMax()) {
                return COOLDOWNS.get(levelRange);
            }
        }
        return 60_000L;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.config.getBoolean("enabled", true);
    }

    @Override
    public void proc(Player player, int level) {
        for (IntRange levelRange : VARIETIES.keySet()) {
            if (level >= levelRange.getMin() && level <= levelRange.getMax()) {
                IntRange range = VARIETIES.get(levelRange);

                this.multiplier.applyCooldownLong(player, ThreadLocalRandom.current().nextInt(range.getMin(), range.getMax()) * 1000L);
                this.cooldown.applyCooldownLong(player, getCooldownTime(level));

                break;
            }
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (event.getEntity().getKiller() == null) return;
        if (event.getEntity() instanceof Player) return;
        if (!this.multiplier.onCooldown(killer.getUniqueId())) return;
        
        event.setDroppedExp((int) (event.getDroppedExp() * this.xpMultiplier));
        killer.getWorld().playEffect(event.getEntity().getEyeLocation(), Effect.WITCH_MAGIC, 0);
    }

}
