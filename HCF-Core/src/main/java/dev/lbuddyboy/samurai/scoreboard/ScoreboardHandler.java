package dev.lbuddyboy.samurai.scoreboard;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.scoreboard.assemble.Assemble;
import dev.lbuddyboy.samurai.scoreboard.assemble.AssembleAdapter;
import dev.lbuddyboy.samurai.scoreboard.assemble.AssembleStyle;
import dev.lbuddyboy.samurai.scoreboard.impl.*;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.scoreboard.thread.ScoreboardUpdateThread;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:01 AM
 * LBuddyBoy Development / me.lbuddyboy.hub.scoreboard
 */

@Getter
public class ScoreboardHandler implements AssembleAdapter {

    private Assemble assemble;
    private File scoreboardDirectory;
    private List<SamuraiScoreboard> scoreboards;
    private int progressInt = 0;

    private ScoreboardUpdateThread updateThread;

    public ScoreboardHandler() {
        reload();
    }

    public void unload() {
        try {
            if (this.assemble != null) {
                this.assemble.cleanup();
            }
        } catch (Exception e) {

        }
    }

    public void reload() {
        Tasks.run(() -> {
            this.loadDirectories();
            this.scoreboards = Arrays.asList(
                    new FFAScoreboard(),
//                    new EOTWFocusedScoreboard(),
//                    new EOTWScoreboard(),
                    new MiniGameScoreboard(),
                    new VaultFocusedScoreboard(),
                    new VaultScoreboard(),
                    new FocusedKoTHScoreboard(),
                    new FocusedScoreboard(),
                    new KoTHScoreboard()
            );

            if (this.assemble != null) this.assemble.cleanup();
            if (this.updateThread != null) this.updateThread.interrupt();

            this.updateThread = new ScoreboardUpdateThread();
            this.assemble = new Assemble(Samurai.getInstance(), this);

            this.assemble.setTicks(2);
            this.assemble.setAssembleStyle(AssembleStyle.KOHI);
            this.updateThread.start();
        });
    }

    private void loadDirectories() {
        this.scoreboardDirectory = new File(Samurai.getInstance().getDataFolder(), "scoreboard");
        if (!scoreboardDirectory.exists()) scoreboardDirectory.mkdirs();
    }

    @Override
    public String getTitle(Player player) {
        ScoreboardTitle scoreboardTitle = null;

        for (SamuraiScoreboard scoreboard : this.scoreboards) {
            if (!scoreboard.qualifies(player)) continue;

            scoreboardTitle = scoreboard.getTitle();
            break;
        }

        String title = CC.translate(scoreboardTitle == null ? "None" : scoreboardTitle.getActiveFrame());

        if (scoreboardTitle != null && scoreboardTitle.isProgress()) {
            String start = scoreboardTitle.getActiveFrame().substring(0, scoreboardTitle.getProgressInt());
            String end = scoreboardTitle.getActiveFrame().substring(scoreboardTitle.getProgressInt(), scoreboardTitle.getActiveFrame().length() - 1);

            title = scoreboardTitle.getCurrentHex() + start + scoreboardTitle.getStartingHex() + end;
        }

        return CC.translate(title.substring(0, Math.min(title.length(), 128)));
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        for (SamuraiScoreboard scoreboard : this.scoreboards) {
            if (!scoreboard.qualifies(player)) continue;

            lines = scoreboard.translateLines(scoreboard.getLines().stream().map(ScoreboardLine::getActiveFrame).collect(Collectors.toList()), player);
            return CC.translate(lines);
        }

        return CC.translate(lines);
    }

}
