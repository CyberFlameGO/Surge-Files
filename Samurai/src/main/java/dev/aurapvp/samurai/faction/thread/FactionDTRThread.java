package dev.aurapvp.samurai.faction.thread;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.timer.PlayerTimer;
import dev.aurapvp.samurai.util.ActionBarAPI;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class FactionDTRThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {

            try {
                for (Faction faction : Samurai.getInstance().getFactionHandler().getFactions().values()) {
                    if (!faction.isRegeneratingDTR() && faction.getDtr() >= faction.getMembers().size() + 1) continue;
                    if (faction.getDtrRegen() + 60_000L > System.currentTimeMillis()) continue;

                    faction.setDtr(faction.getMaxDTR());
                }
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(2000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
