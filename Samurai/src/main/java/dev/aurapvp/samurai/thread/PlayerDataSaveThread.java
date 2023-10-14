package dev.aurapvp.samurai.thread;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.IModule;

public class PlayerDataSaveThread extends Thread {

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {

            Samurai.getInstance().getModules().forEach(IModule::save);

            try {
                Thread.sleep(30_000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
