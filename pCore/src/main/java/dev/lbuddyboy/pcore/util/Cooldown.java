package dev.lbuddyboy.pcore.util;

import lombok.Data;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Cooldown {

    private Map<UUID, Long> cooldowns = new HashMap<>();

    public void applyCooldown(Player player, long cooldown) {
        applyCooldown(player.getUniqueId(), cooldown);
    }

    public void applyCooldown(UUID uuid, long cooldown) {
        cooldowns.put(uuid, System.currentTimeMillis() + cooldown * 1000);
    }

    public void applyCooldownLong(UUID uuid, long cooldown) {
        cooldowns.put(uuid, System.currentTimeMillis() + cooldown);
    }

    public void applyCooldownLong(Player player, long cooldown) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown);
    }

    public boolean onCooldown(Player player) {
        return cooldowns.containsKey(player.getUniqueId()) && (cooldowns.get(player.getUniqueId()) >= System.currentTimeMillis());
    }

    public boolean onCooldown(UUID player) {
        return cooldowns.containsKey(player) && (cooldowns.get(player) >= System.currentTimeMillis());
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public String getRemaining(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return l >= 60_000L ? TimeUtils.formatIntoMMSS((int) (l / 1000)) : (l / 1000) + "s";
    }

    public void cleanUp() {
        List<UUID> uuids = this.cooldowns.keySet().stream().filter(uuid -> !onCooldown(uuid)).collect(Collectors.toList());

        uuids.forEach(cooldowns::remove);
    }

}
