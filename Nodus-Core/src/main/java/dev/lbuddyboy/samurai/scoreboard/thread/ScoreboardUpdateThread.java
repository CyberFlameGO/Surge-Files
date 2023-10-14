package dev.lbuddyboy.samurai.scoreboard.thread;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.scoreboard.SamuraiScoreboard;

public class ScoreboardUpdateThread extends Thread {

    @Override
    public void run() {
        while (true) {
            try {
                tick();
                sleep(50L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void tick() {
        Samurai.getInstance().getScoreboardHandler().getScoreboards().forEach(SamuraiScoreboard::update);
    }

}
