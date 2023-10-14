package dev.aurapvp.samurai.scoreboard;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.economy.BankAccount;
import dev.aurapvp.samurai.economy.IEconomy;
import dev.aurapvp.samurai.faction.Faction;
import dev.aurapvp.samurai.scoreboard.assemble.Assemble;
import dev.aurapvp.samurai.scoreboard.assemble.AssembleAdapter;
import dev.aurapvp.samurai.scoreboard.assemble.AssembleStyle;
import dev.aurapvp.samurai.scoreboard.impl.*;
import dev.aurapvp.samurai.scoreboard.thread.ScoreboardUpdateThread;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.IModule;
import dev.aurapvp.samurai.util.YamlDoc;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.text.SimpleDateFormat;
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

    private ScoreboardUpdateThread updateThread;

    @Override
    public String getId() {
        return "scoreboard";
    }

    @Override
    public void load(Samurai plugin) {
        reload();
    }

    @Override
    public void unload(Samurai plugin) {
        if (this.assemble != null) {
            this.assemble.cleanup();
        }
    }

    @Override
    public void reload() {
        this.loadDirectories();
        this.scoreboards = Arrays.asList(
//                new FactionKoTHFocusedScoreboard(),
//                new FactionKoTHScoreboard(),
                new FactionFocusedScoreboard(),
                new FactionScoreboard(),
                new DefaultKoTHScoreboard(),
                new DefaultScoreboard()
        );

        if (this.assemble != null) this.assemble.cleanup();
        if (this.updateThread != null) this.updateThread.interrupt();

        this.updateThread = new ScoreboardUpdateThread();
        this.assemble = new Assemble(Samurai.getInstance(), this);

        this.assemble.setTicks(3);
        this.assemble.setAssembleStyle(AssembleStyle.KOHI);
        this.updateThread.start();
    }

    private void loadDirectories() {
        this.scoreboardDirectory = new File(Samurai.getInstance().getDataFolder(), "scoreboard");
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
