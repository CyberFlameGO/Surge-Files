package dev.lbuddyboy.samurai.events.conquest;

import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.conquest.game.ConquestGame;
import org.bukkit.ChatColor;

public class ConquestHandler {

    public static final String PREFIX = ChatColor.YELLOW + "[Conquest]";

    public static final int POINTS_DEATH_PENALTY = 20;
    public static final String KOTH_PREFIX = "Conquest-";
    public static final int TIME_TO_CAP = 30;

    @Getter @Setter private ConquestGame game;

    public static int getPointsToWin() {
        return Samurai.getInstance().getConfig().getInt("conquestWinPoints", 150);
    }
}