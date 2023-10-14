package dev.lbuddyboy.samurai.server.timer;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.timer.command.ServerTimerCommand;
import dev.lbuddyboy.samurai.server.timer.impl.*;
import dev.lbuddyboy.samurai.server.timer.listener.ServerTimerListener;
import dev.lbuddyboy.samurai.server.timer.command.PlayerTimerCommand;
import dev.lbuddyboy.samurai.server.timer.thread.PlayerTimerThread;
import dev.lbuddyboy.samurai.server.timer.thread.ServerTimerThread;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class TimerHandler {

    private final List<PlayerTimer> playerTimers;
    private final Map<String, ServerTimer> serverTimers;
    private ServerTimer currentlyDisplayed = null;

    public TimerHandler() {
        this.playerTimers = new ArrayList<>();
        this.serverTimers = new HashMap<>();

        this.loadTimers();
        this.loadListeners();
        this.loadCommands();
        this.loadThreads();
    }

    public void save() {
        this.playerTimers.forEach(pt -> pt.getCooldown().cleanUp());
    }

    private void loadTimers() {
        this.playerTimers.addAll(Arrays.asList(
                new ArcherMarkTimer(),
                new BardEffectTimer(),
                new BardEnergyTimer(),
                new CrappleTimer(),
                new FStuckTimer(),
                new PearlTimer(),
                new HomeTimer(),
                new CombatTagTimer(),
                new LogoutTimer(),
                new InvincibilityTimer()
        ));
    }

    private void loadListeners() {
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new ServerTimerListener(), Samurai.getInstance());
    }

    private void loadCommands() {
        Samurai.getInstance().getPaperCommandManager().registerCommand(new PlayerTimerCommand());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new ServerTimerCommand());
    }

    private void loadThreads() {
        new PlayerTimerThread().start();
        new ServerTimerThread().start();
    }

    public List<PlayerTimer> getPlayerTimers(Player player) {
        return this.playerTimers.stream().filter(pt -> pt.onCooldown(player)).collect(Collectors.toList());
    }

    public ServerTimer getSOTWTimer() {
        return this.serverTimers.get("SOTW");
    }

    public ServerTimer getAbilityEvent() {
        return this.serverTimers.get("AbilityEvent");
    }

    public boolean isSOTWTimer() {
        return getSOTWTimer() != null;
    }

    public boolean isAbilityHour() {
        return getAbilityEvent() != null;
    }

}
