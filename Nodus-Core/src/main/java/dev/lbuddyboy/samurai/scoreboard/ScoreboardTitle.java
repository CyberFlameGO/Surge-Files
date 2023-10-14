package dev.lbuddyboy.samurai.scoreboard;

import lombok.Data;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.*;
import java.util.List;

@Data
public class ScoreboardTitle {

    private List<String> frames;
    private int currentFrame, delay;
    private long lastUpdated;
    private ChatColor startingHex, currentHex, finalHex;
    private boolean progress;
    private int progressInt;

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

    public ScoreboardTitle(ConfigurationSection section) {
        this.frames = section.getStringList("frames");
        this.delay = section.getInt("delay");
        if (section.contains("progress") && section.getBoolean("progress.enabled")) {
            this.startingHex = ChatColor.of(section.getString("progress.starting-hex"));
            this.currentHex = ChatColor.of(section.getString("progress.starting-hex"));
            this.finalHex = ChatColor.of(section.getString("progress.final-hex"));
            this.progress = section.getBoolean("progress.enabled");
        }
    }

    public ChatColor getStartingHexColor() {
        return this.startingHex;
    }

    public ChatColor getCurrentHexColor() {
        return this.currentHex;
    }

    public ChatColor getFinalHexColor() {
        return this.finalHex;
    }

    public void progress() {
        if (!progress) return;

        int r = getStartingHexColor().getColor().getRed() + getFinalHexColor().getColor().getRed();
        int g = getStartingHexColor().getColor().getGreen() + getFinalHexColor().getColor().getGreen();
        int b = getStartingHexColor().getColor().getBlue() + getFinalHexColor().getColor().getBlue();

        this.currentHex = ChatColor.of(new Color(
                Math.min(r - getFinalHexColor().getColor().getRed(), 255),
                Math.min(g - getFinalHexColor().getColor().getGreen(), 255),
                Math.min(b - getFinalHexColor().getColor().getBlue(), 255)
        ));

        progressInt++;

        if (this.currentHex.getColor() == this.finalHex.getColor()) {
            this.currentHex = this.startingHex;
        }

        if (this.progressInt >= getActiveFrame().length() - 1) {
            this.progressInt = 0;
        }
    }

}
