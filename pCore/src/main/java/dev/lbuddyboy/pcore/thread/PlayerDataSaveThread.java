package dev.lbuddyboy.pcore.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.IModule;

import java.util.HashMap;
import java.util.Map;

public class PlayerDataSaveThread extends Thread {

    private Map<IModule, Long> lastSaved = new HashMap<>();

    @Override
    public void run() {
        while (pCore.getInstance().isEnabled()) {

            pCore.getInstance().getModules().forEach(module -> {
                if (this.lastSaved.containsKey(module) && this.lastSaved.get(module) > System.currentTimeMillis()) return;

                module.save();
                this.lastSaved.put(module, System.currentTimeMillis() + module.cooldown());
            });

            try {
                Thread.sleep(5_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
