package dev.lbuddyboy.bunkers.util.cooldown;

import dev.lbuddyboy.bunkers.util.cooldown.form.DurationFormatter;
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

    public void removeCooldown(Player player) {
        cooldowns.remove(player.getUniqueId());
    }

    public String getRemaining(Player player) {
        long l = this.cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return DurationFormatter.getRemaining(l, true);
    }

}
