package dev.lbuddyboy.samurai.util.cooldown;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Data;
import dev.lbuddyboy.samurai.util.cooldown.form.DurationFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Cooldown {

    private Map<UUID, Long> cooldowns = new HashMap<>();
    private Map<Integer, Long> cooldownsEntityIds = new HashMap<>();

    public void applyCooldown(Player player, long cooldown) {
        applyCooldown(player.getUniqueId(), cooldown);
    }

    public void applyCooldown(UUID uuid, long cooldown) {
        cooldowns.put(uuid, System.currentTimeMillis() + cooldown * 1000);
    }

    public void applyCooldown(Integer entityId, long cooldown) {
        cooldownsEntityIds.put(entityId, System.currentTimeMillis() + cooldown * 1000);
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

    public boolean onCooldown(Integer integer) {
        return cooldownsEntityIds.containsKey(integer) && (cooldownsEntityIds.get(integer) >= System.currentTimeMillis());
    }

    public boolean onCooldown(UUID player) {
        return cooldowns.containsKey(player) && (cooldowns.get(player) >= System.currentTimeMillis());
    }

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public void removeCooldown(int id) {
        cooldownsEntityIds.remove(id);
    }

    public String getRemainingFancy(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return l >= 60_000L ? TimeUtils.formatIntoMMSS((int) (l / 1000)) : (l / 1000) + "s";
    }

    public void cleanUp() {
        List<UUID> uuids = this.cooldowns.keySet().stream().filter(uuid -> !onCooldown(uuid) || Bukkit.getPlayer(uuid) == null && !onCooldown(uuid)).toList();

        uuids.forEach(cooldowns::remove);
    }

    public String getRemaining(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatter.getRemaining(l, true);
    }

}
