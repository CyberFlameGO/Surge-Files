package dev.aurapvp.samurai.scoreboard.thread;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.scoreboard.SamuraiScoreboard;
import dev.aurapvp.samurai.scoreboard.assemble.Assemble;
import dev.aurapvp.samurai.scoreboard.assemble.AssembleBoard;
import dev.aurapvp.samurai.scoreboard.assemble.AssembleBoardEntry;
import dev.aurapvp.samurai.scoreboard.assemble.AssembleException;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.Collections;
import java.util.List;

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
