package dev.aurapvp.samurai.timer;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.timer.command.PlayerTimerCommand;
import dev.aurapvp.samurai.timer.command.ServerTimerCommand;
import dev.aurapvp.samurai.timer.command.completions.PlayerTimerCompletion;
import dev.aurapvp.samurai.timer.command.completions.ServerTimerCompletion;
import dev.aurapvp.samurai.timer.command.contexts.PlayerTimerContext;
import dev.aurapvp.samurai.timer.command.contexts.ServerTimerContext;
import dev.aurapvp.samurai.timer.impl.*;
import dev.aurapvp.samurai.timer.listener.ServerTimerListener;
import dev.aurapvp.samurai.timer.thread.PlayerTimerThread;
import dev.aurapvp.samurai.timer.thread.ServerTimerThread;
import dev.aurapvp.samurai.util.IModule;
import lombok.Data;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TimerHandler implements IModule {

    private InvincibilityTimer invincibilityTimer;

    private final List<PlayerTimer> playerTimers;
    private final Map<String, ServerTimer> serverTimers;
    private ServerTimer currentlyDisplayed = null;

    public TimerHandler() {
        this.playerTimers = new ArrayList<>();
        this.serverTimers = new HashMap<>();
    }

    @Override
    public String getId() {
        return "timer";
    }

    @Override
    public void load(Samurai plugin) {
        this.loadTimers();
        this.loadListeners();
        this.loadCommands();
        this.loadThreads();
    }

    @Override
    public void unload(Samurai plugin) {

    }

    @Override
    public void save() {
        this.playerTimers.forEach(pt -> pt.getCooldown().cleanUp());
    }

    private void loadTimers() {
        invincibilityTimer = new InvincibilityTimer();
        this.playerTimers.addAll(Arrays.asList(
                new CrappleTimer(),
                new PearlTimer(),
                new HomeTimer(),
                new CombatTagTimer(),
                invincibilityTimer
        ));
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ServerTimerListener(), Samurai.getInstance());
    }

    private void loadCommands() {
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("playertimers", new PlayerTimerCompletion());
        Samurai.getInstance().getCommandManager().getCommandCompletions().registerCompletion("servertimers", new ServerTimerCompletion());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(ServerTimer.class, new ServerTimerContext());
        Samurai.getInstance().getCommandManager().getCommandContexts().registerContext(PlayerTimer.class, new PlayerTimerContext());
        Samurai.getInstance().getCommandManager().registerCommand(new PlayerTimerCommand());
        Samurai.getInstance().getCommandManager().registerCommand(new ServerTimerCommand());
    }

    private void loadThreads() {
        new PlayerTimerThread().start();
        new ServerTimerThread().start();
    }

    public List<PlayerTimer> getPlayerTimers(Player player) {
        return this.playerTimers.stream().filter(pt -> pt.getCooldown().onCooldown(player.getUniqueId())).collect(Collectors.toList());
    }

    public boolean hasInvincibilityTimer(Player player) {
        return this.invincibilityTimer.getCooldown().onCooldown(player.getUniqueId());
    }

}
