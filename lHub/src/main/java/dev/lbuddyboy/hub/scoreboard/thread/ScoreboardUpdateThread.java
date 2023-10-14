package dev.lbuddyboy.hub.scoreboard.thread;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.scoreboard.HubScoreboard;

public class ScoreboardUpdateThread extends Thread {

    @Override
    public void run() {
        while (lHub.getInstance().isEnabled()) {
            try {
                tick();
                sleep(500L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        lHub.getInstance().getScoreboardHandler().getScoreboards().forEach(HubScoreboard::update);
    }

}
