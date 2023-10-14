package dev.lbuddyboy.samurai.scoreboard;

import dev.lbuddyboy.flash.util.Config;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class SamuraiScoreboard {

    public abstract Config getFile();
    public abstract ScoreboardTitle getTitle();
    public abstract List<ScoreboardLine> getLines();
    public abstract List<String> translateLines(List<String> lines, Player player);
    public abstract boolean qualifies(Player player);
    public void update() {
        this.getTitle().progress();
        this.getTitle().playNext();
        this.getLines().forEach(ScoreboardLine::playNext);
    }

}
