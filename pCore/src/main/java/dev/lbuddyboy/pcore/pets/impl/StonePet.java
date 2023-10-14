package dev.lbuddyboy.pcore.pets.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.IntRange;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class StonePet implements IPet {

    private final Map<IntRange, IntRange> VARIETIES = new HashMap<>();
    private final Map<IntRange, Long> COOLDOWNS = new HashMap<>();
    private final Cooldown cooldown = new Cooldown();
    private final Config config;

    public StonePet(Config config) {
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
    }

    @Override
    public String getName() {
        return "Stone";
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
        return this.config.getString("display-name", "&e&lStone Pet");
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

                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * ThreadLocalRandom.current().nextInt(range.getMin(), range.getMax()), 2));

                this.cooldown.applyCooldownLong(player, getCooldownTime(level));
                break;
            }
        }

    }
}
