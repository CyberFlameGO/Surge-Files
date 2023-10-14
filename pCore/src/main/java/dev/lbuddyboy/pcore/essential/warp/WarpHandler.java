package dev.lbuddyboy.pcore.essential.warp;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.essential.warp.command.WarpCommand;
import dev.lbuddyboy.pcore.essential.warp.command.completions.WarpCompletion;
import dev.lbuddyboy.pcore.essential.warp.command.contexts.WarpContext;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.PlayerTimer;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.timer.command.PlayerTimerCommand;
import dev.lbuddyboy.pcore.timer.command.completions.PlayerTimerCompletion;
import dev.lbuddyboy.pcore.timer.command.completions.ServerTimerCompletion;
import dev.lbuddyboy.pcore.timer.command.contexts.PlayerTimerContext;
import dev.lbuddyboy.pcore.timer.command.contexts.ServerTimerContext;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.menu.PagedGUISettings;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class WarpHandler implements IModule {

    private final Map<String, Warp> warps;
    private Config config;
    private File warpDirectory;
    private PagedGUISettings guiSettings;

    public WarpHandler() {
        this.warps = new HashMap<>();
        this.config = new Config(pCore.getInstance(), "warp");
        this.guiSettings = new PagedGUISettings(this.config);
    }

    @Override
    public void load(pCore plugin) {
        loadCommands();
        reload();
    }

    @Override
    public void reload() {
        loadDirectories();
        loadWarps();
    }

    @Override
    public void unload(pCore plugin) {

    }

    private void loadDirectories() {
        this.warpDirectory = new File(pCore.getInstance().getDataFolder(), "warps");
        if (!warpDirectory.exists()) warpDirectory.mkdirs();
    }

    private void loadWarps() {
        this.warps.clear();

        for (String s : warpDirectory.list()) {
            String name = s.replaceAll(".yml", "");

            this.warps.put(name, new Warp(new Config(pCore.getInstance(), name, this.warpDirectory)));
        }
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("warps", new WarpCompletion());
        pCore.getInstance().getCommandManager().getCommandContexts().registerContext(Warp.class, new WarpContext());
        pCore.getInstance().getCommandManager().registerCommand(new WarpCommand());
    }

}
