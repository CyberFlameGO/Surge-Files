package dev.lbuddyboy.hub.scoreboard;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;

@Data
public class ScoreboardLine {

    private List<String> frames;
    private int currentFrame = 0, delay;
    private long lastUpdated;

    public String getActiveFrame() {
        return this.frames.get(this.currentFrame);
    }

    public void playNext() {
        if (!isNext()) return;

        this.currentFrame++;
        if (this.currentFrame >= this.frames.size()) {
            this.currentFrame = 0;
        }

        this.lastUpdated = System.currentTimeMillis();
    }

    public boolean isNext() {
        if (this.delay <= 0) return false;

        return this.lastUpdated + (this.delay * 50L) < System.currentTimeMillis();
    }

    public ScoreboardLine(ConfigurationSection section) {
        this.frames = section.getStringList("frames");
        this.delay = section.getInt("delay");
    }

}
