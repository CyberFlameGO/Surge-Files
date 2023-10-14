package dev.lbuddyboy.hub.scoreboard;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.scoreboard.assemble.Assemble;
import dev.lbuddyboy.hub.scoreboard.assemble.AssembleAdapter;
import dev.lbuddyboy.hub.scoreboard.assemble.AssembleStyle;
import dev.lbuddyboy.hub.scoreboard.impl.DefaultScoreboard;
import dev.lbuddyboy.hub.scoreboard.impl.QueueScoreboard;
import dev.lbuddyboy.hub.scoreboard.impl.bedrock.DefaultBedrockScoreboard;
import dev.lbuddyboy.hub.scoreboard.impl.bedrock.QueueBedrockScoreboard;
import dev.lbuddyboy.hub.scoreboard.impl.legacy.DefaultLegacyScoreboard;
import dev.lbuddyboy.hub.scoreboard.impl.legacy.QueueLegacyScoreboard;
import dev.lbuddyboy.hub.scoreboard.thread.ScoreboardUpdateThread;
import dev.lbuddyboy.hub.util.CC;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:01 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.scoreboard
 */

@Getter
public class ScoreboardHandler implements AssembleAdapter, lModule {

    private Assemble assemble;
    private File scoreboardDirectory;
    private List<HubScoreboard> scoreboards;

    private ScoreboardUpdateThread updateThread;

    @Override
    public void load(lHub plugin) {
        reload();
    }

    @Override
    public void unload(lHub plugin) {

    }

    @Override
    public void reload() {
        this.loadDirectories();
        this.scoreboards = Arrays.asList(
                new DefaultLegacyScoreboard(),
                new QueueLegacyScoreboard(),
                new QueueBedrockScoreboard(),
                new DefaultBedrockScoreboard(),
                new QueueScoreboard(),
                new DefaultScoreboard()
        );

        if (this.assemble != null) this.assemble.cleanup();
        if (this.updateThread != null) this.updateThread.interrupt();

        this.updateThread = new ScoreboardUpdateThread();
        this.assemble = new Assemble(lHub.getInstance(), this);

        this.assemble.setTicks(3);
        this.assemble.setAssembleStyle(AssembleStyle.KOHI);
        this.updateThread.start();
    }

    private void loadDirectories() {
        this.scoreboardDirectory = new File(lHub.getInstance().getDataFolder(), "scoreboard");
        if (!scoreboardDirectory.exists()) scoreboardDirectory.mkdirs();
    }

    @Override
    public String getTitle(Player player) {
        String title = "None";

        for (HubScoreboard scoreboard : this.scoreboards) {
            if (!scoreboard.qualifies(player)) continue;

            title = scoreboard.getTitle().getActiveFrame();
            break;
        }

        return title;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        for (HubScoreboard scoreboard : this.scoreboards) {
            if (!scoreboard.qualifies(player)) continue;

            lines = scoreboard.translateLines(scoreboard.getLines().stream().map(ScoreboardLine::getActiveFrame).collect(Collectors.toList()), player);
            return CC.translate(lines);
        }

        return CC.translate(lines);
    }


}
