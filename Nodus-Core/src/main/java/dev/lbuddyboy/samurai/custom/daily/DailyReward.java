package dev.lbuddyboy.samurai.custom.daily;

import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseTimeMap;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseToggleMap;
import dev.lbuddyboy.samurai.util.CC;
import lombok.Data;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Getter
public class DailyReward extends BaseToggleMap {

    private final String name, displayName;
    private final List<String> commands;
    private final int weight;

    public DailyReward(String statistic, String name, String displayName, List<String> commands, int weight) {
        super(statistic);
        this.name = name;
        this.displayName = displayName;
        this.commands = commands;
        this.weight = weight;
    }

    public void claim(Player player) {
        setActive(player.getUniqueId(), true);
        for (String command : this.commands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                    .replaceAll("%player%", player.getName()));
        }

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);
        player.sendTitle(CC.translate("&6&lDAILY REWARDS"), CC.translate("&fYou have just claimed your daily reward!"));
        Samurai.getInstance().getDailyHandler().getRewardsMap().setTime(player.getUniqueId(), System.currentTimeMillis());
    }

}
