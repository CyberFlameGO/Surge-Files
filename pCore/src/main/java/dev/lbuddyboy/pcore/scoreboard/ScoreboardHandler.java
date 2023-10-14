package dev.lbuddyboy.pcore.scoreboard;

import dev.lbuddyboy.pcore.events.koth.KoTH;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.scoreboard.assemble.Assemble;
import dev.lbuddyboy.pcore.scoreboard.assemble.AssembleAdapter;
import dev.lbuddyboy.pcore.scoreboard.assemble.AssembleStyle;
import dev.lbuddyboy.pcore.scoreboard.impl.*;
import dev.lbuddyboy.pcore.scoreboard.thread.ActiveEventThread;
import dev.lbuddyboy.pcore.scoreboard.thread.ScoreboardUpdateThread;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.IModule;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/09/2021 / 2:01 AM
 * LBuddyBoy Development / me.lbuddyboy.hub.scoreboard
 */

@Getter
public class ScoreboardHandler implements AssembleAdapter, IModule {

    private Assemble assemble;
    private File scoreboardDirectory;
    private List<SamuraiScoreboard> scoreboards;
    @Setter private KoTH activeKoTH = null;

    private ActiveEventThread activeEventThread;
    private ScoreboardUpdateThread updateThread;

    @Override
    public void load(pCore plugin) {
        reload();
        this.activeEventThread = new ActiveEventThread();
        this.updateThread = new ScoreboardUpdateThread();

        this.activeEventThread.start();
        this.updateThread.start();
    }

    @Override
    public void unload(pCore plugin) {
        try {
            if (this.assemble != null) {
                this.assemble.cleanup();
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void reload() {
        this.loadDirectories();
        this.scoreboards = Arrays.asList(
                new GangKoTHScoreboard(),
                new GangMineScoreboard(),
                new GangScoreboard(),
                new DefaultKoTHScoreboard(),
                new MineScoreboard(),
                new DefaultScoreboard()
        );

        if (this.assemble != null) this.assemble.cleanup();
        if (this.updateThread != null) this.updateThread.interrupt();

        this.assemble = new Assemble(pCore.getInstance(), this);

        this.assemble.setTicks(3);
        this.assemble.setAssembleStyle(AssembleStyle.KOHI);
    }

    private void loadDirectories() {
        this.scoreboardDirectory = new File(pCore.getInstance().getDataFolder(), "scoreboard");
        if (!scoreboardDirectory.exists()) scoreboardDirectory.mkdirs();
    }

    @Override
    public String getTitle(Player player) {
        String title = "None";

        for (SamuraiScoreboard scoreboard : this.scoreboards) {
            if (!scoreboard.qualifies(player)) continue;

            title = scoreboard.getTitle().getActiveFrame();
            break;
        }

        return title;
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
