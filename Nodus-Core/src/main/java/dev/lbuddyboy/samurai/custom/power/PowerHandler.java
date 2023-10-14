package dev.lbuddyboy.samurai.custom.power;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.power.map.PowerMap;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.power.command.PowerCommand;
import dev.lbuddyboy.samurai.custom.power.listener.PowerListener;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Getter
public class PowerHandler {

    private final PowerMap powerMap;

    public PowerHandler() {

        (this.powerMap = new PowerMap()).loadFromRedis();

        Bukkit.getPluginManager().registerEvents(new PowerListener(), Samurai.getInstance());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new PowerCommand());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (Feature.POWER.isDisabled()) {
                    return;
                }

                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (!hasPower(p)) {
                        p.sendMessage(CC.translate("&cYou still haven't claimed a power! Do /power selector to claim one!"));
                    }
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 0, 20 * 60);
    }

    public String getPower(Player p) {
        return this.powerMap.getPower(p.getUniqueId());
    }

    public boolean hasPower(Player p) {
        return this.powerMap.hasPower(p.getUniqueId());
    }

}
