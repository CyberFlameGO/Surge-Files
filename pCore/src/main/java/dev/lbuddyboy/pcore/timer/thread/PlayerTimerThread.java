package dev.lbuddyboy.pcore.timer.thread;

import dev.lbuddyboy.pcore.essential.warp.Warp;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.ActionBarAPI;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerTimerThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    @Override
    public void run() {
        while (pCore.getInstance().isEnabled()) {

            try {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    List<PlayerTimer> timers = new ArrayList<>(pCore.getInstance().getTimerHandler().getPlayerTimers(player));

                    for (Warp warp : pCore.getInstance().getWarpHandler().getWarps().values()) {
                        if (warp.getCooldown().onCooldown(player)) {
                            timers.add(warp);
                        }
                    }

                    if (timers.isEmpty()) continue;

                    List<String> mapped = timers.stream().map(timer -> timer.getDisplayName() + "&7: &f" + timer.getCooldown().getRemaining(player) + "&7").collect(Collectors.toList());

                    ActionBarAPI.sendActionBar(player, CC.translate(StringUtils.join(mapped)));
                }
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(400L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
