package dev.lbuddyboy.crates;

import dev.lbuddyboy.crates.model.Crate;

public class CrateThread extends Thread {

    @Override
    public void run() {
        while (lCrates.getInstance().isEnabled()) {
            try {
                lCrates.getInstance().getCrates().values().forEach(Crate::save);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                sleep(60_000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
