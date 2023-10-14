package dev.lbuddyboy.pcore.scoreboard.thread;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.scoreboard.SamuraiScoreboard;

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
        pCore.getInstance().getScoreboardHandler().getScoreboards().forEach(SamuraiScoreboard::update);
    }

}
