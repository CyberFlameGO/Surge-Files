package dev.lbuddyboy.vouchers.util.cooldown;

import dev.lbuddyboy.vouchers.util.TimeUtils;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
public class Cooldown {

    private Map<UUID, Long> cooldowns = new HashMap<>();

    public void applyCooldown(Player player, long cooldown) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldown * 1000);
//        HCF.getInstance().getAbilityManager().getGlobalCooldown().getCooldowns().put(player.getUniqueId(), System.currentTimeMillis() + 10000);
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
        return TimeUtils.formatIntoHHMMSS((int) (l/1000));
    }

}
