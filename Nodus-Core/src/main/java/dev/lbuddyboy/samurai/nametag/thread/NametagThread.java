package dev.lbuddyboy.samurai.nametag.thread;

import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.NametagUpdate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class NametagThread extends Thread {
    public static final Map<NametagUpdate, Boolean> pendingUpdates = new ConcurrentHashMap<>();

    public NametagThread() {
        super("qLib - Nametag Thread");
        this.setDaemon(true);
    }

    @Override
    public void run() {
        while (true) {
            for (NametagUpdate pendingUpdate : NametagThread.pendingUpdates.keySet()) {
                try {
                    FrozenNametagHandler.applyUpdate(pendingUpdate);
                } catch (Exception ignored) {
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }
    }

    public static Map<NametagUpdate, Boolean> getPendingUpdates() {
        return pendingUpdates;
    }
}

