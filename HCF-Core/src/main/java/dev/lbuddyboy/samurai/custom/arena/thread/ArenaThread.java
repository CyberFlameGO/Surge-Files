package dev.lbuddyboy.samurai.custom.arena.thread;

import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArenaThread extends Thread {

    @Override
    public void run() {
        while (true) {

            List<UUID> toRemove = new ArrayList<>();
            for (UUID uuid : Samurai.getInstance().getArenaHandler().getUuids()) {
                if (Samurai.getInstance().getArenaHandler().isDeathbanned(uuid)) continue;

                Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
                    Samurai.getInstance().getDeathbanMap().revive(uuid);
                });
                toRemove.add(uuid);
            }
            for (UUID uuid : toRemove) {
                Samurai.getInstance().getArenaHandler().getUuids().remove(uuid);
            }

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
