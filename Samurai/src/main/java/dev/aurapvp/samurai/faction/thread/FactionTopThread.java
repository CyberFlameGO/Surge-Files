package dev.aurapvp.samurai.faction.thread;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.util.CC;

import java.util.Comparator;
import java.util.stream.Collectors;

public class FactionTopThread extends Thread {

    private long lastErrored = System.currentTimeMillis();

    @Override
    public void run() {
        while (Samurai.getInstance().isEnabled()) {
            try {
                Samurai.getInstance().getFactionHandler().updateFTop();
            } catch (Exception e) {
                if (lastErrored + 5_000L > System.currentTimeMillis()) continue;
                lastErrored = System.currentTimeMillis();
                e.printStackTrace();
            }

            try {
                Thread.sleep(60_000L * 5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static class FactionPointComparator implements Comparator<Faction> {

        @Override
        public int compare(Faction o1, Faction o2) {
            return Double.compare(o1.getPoints(), o2.getPoints());
        }

    }

}
