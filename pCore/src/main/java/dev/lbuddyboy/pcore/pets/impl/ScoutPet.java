package dev.lbuddyboy.pcore.pets.impl;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.Cooldown;
import dev.lbuddyboy.pcore.util.IntRange;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoutPet implements IPet, Listener {

    private final Map<IntRange, Integer> VARIETIES = new HashMap<>();
    private final Cooldown cooldown = new Cooldown();
    private final Config config;

    public ScoutPet(Config config) {
        this.config = config;

        for (String s : this.config.getStringList("level-variations")) {
            String[] parts = s.split(";");
            IntRange levelRange = new IntRange(Integer.parseInt(parts[0].split("-")[0]), Integer.parseInt(parts[0].split("-")[1]));

            VARIETIES.put(levelRange, Integer.parseInt(parts[1]));
        }

        Bukkit.getPluginManager().registerEvents(this, pCore.getInstance());
    }

    @Override
    public String getName() {
        return "Scout";
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
        return this.config.getString("display-name", "&b&lScout Pet");
    }

    @Override
    public PetRarity getPetRarity() {
        return pCore.getInstance().getPetHandler().getPetRarity(this.config.getString("rarity", "uncommon"));
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
        return 60_000L;
    }

    @Override
    public Cooldown getCooldown() {
        return this.cooldown;
    }

    @Override
    public boolean isClickable() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return this.config.getBoolean("enabled", true);
    }

    @Override
    public void proc(Player player, int level) {
        for (IntRange levelRange : VARIETIES.keySet()) {
            if (level >= levelRange.getMin() && level <= levelRange.getMax()) {
                if (player.hasPotionEffect(PotionEffectType.SPEED) && player.getActivePotionEffects().stream().noneMatch(p -> p.getType() == PotionEffectType.SPEED && p.getAmplifier() > VARIETIES.get(levelRange) - 1)) return;

                player.removePotionEffect(PotionEffectType.SPEED);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 15, VARIETIES.get(levelRange) - 1));

                break;
            }
        }
    }

}
