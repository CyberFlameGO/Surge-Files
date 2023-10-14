package dev.lbuddyboy.pcore.timer;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.command.PlayerTimerCommand;
import dev.lbuddyboy.pcore.timer.command.ServerTimerCommand;
import dev.lbuddyboy.pcore.timer.command.completions.PlayerTimerCompletion;
import dev.lbuddyboy.pcore.timer.command.completions.ServerTimerCompletion;
import dev.lbuddyboy.pcore.timer.command.contexts.PlayerTimerContext;
import dev.lbuddyboy.pcore.timer.command.contexts.ServerTimerContext;
import dev.lbuddyboy.pcore.timer.impl.*;
import dev.lbuddyboy.pcore.timer.listener.ServerTimerListener;
import dev.lbuddyboy.pcore.timer.thread.PlayerTimerThread;
import dev.lbuddyboy.pcore.timer.thread.ServerTimerThread;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.timer.impl.*;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TimerHandler implements IModule {

    private final List<PlayerTimer> playerTimers;
    private final Map<String, ServerTimer> serverTimers;
    private ServerTimer currentlyDisplayed = null;

    public TimerHandler() {
        this.playerTimers = new ArrayList<>();
        this.serverTimers = new HashMap<>();
    }

    @Override
    public void load(pCore plugin) {
        this.loadTimers();
        this.loadListeners();
        this.loadCommands();
        this.loadThreads();
    }

    @Override
    public void unload(pCore plugin) {

    }

    @Override
    public void save() {
        this.playerTimers.forEach(pt -> pt.getCooldown().cleanUp());
    }

    private void loadTimers() {
        this.playerTimers.addAll(Arrays.asList(
                new CrappleTimer(),
                new PearlTimer(),
                new TeleportTimer(),
                new CombatTagTimer()
        ));
    }

    private void loadListeners() {
        pCore.getInstance().getServer().getPluginManager().registerEvents(new ServerTimerListener(), pCore.getInstance());
    }

    private void loadCommands() {
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("playertimers", new PlayerTimerCompletion());
        pCore.getInstance().getCommandManager().getCommandCompletions().registerCompletion("servertimers", new ServerTimerCompletion());
        pCore.getInstance().getCommandManager().getCommandContexts().registerContext(ServerTimer.class, new ServerTimerContext());
        pCore.getInstance().getCommandManager().getCommandContexts().registerContext(PlayerTimer.class, new PlayerTimerContext());
        pCore.getInstance().getCommandManager().registerCommand(new PlayerTimerCommand());
        pCore.getInstance().getCommandManager().registerCommand(new ServerTimerCommand());
    }

    private void loadThreads() {
        new PlayerTimerThread().start();
        new ServerTimerThread().start();
    }

    public List<PlayerTimer> getPlayerTimers(Player player) {
        return this.playerTimers.stream().filter(pt -> pt.getCooldown().onCooldown(player.getUniqueId())).collect(Collectors.toList());
    }

}
