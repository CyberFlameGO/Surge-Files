package dev.lbuddyboy.samurai.custom.teamwar.model;

import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.thread.WarThread;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.util.object.Callback;
import lombok.AllArgsConstructor;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.List;

@AllArgsConstructor
@Getter
public enum WarState {

    NOT_STARTED((thread) -> {

    }),

    LOBBY((thread) -> {
        List<WarTeam> teams = Samurai.getInstance().getTeamWarHandler().getQueuedTeams();

        if (WarThread.lastNotified + 15_000L < System.currentTimeMillis()) {
            Tasks.run(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for (FancyMessage message : thread.createHostNotification()) {
                        message.send(player);
                    }
                }
            });
            WarThread.lastNotified = System.currentTimeMillis();
        }

        if (teams.size() >= Samurai.getInstance().getTeamWarHandler().getMinimumTeamSize()) {
            if (WarThread.INITIATED == -1) WarThread.INITIATED = System.currentTimeMillis();

            if (WarThread.INITIATED + WarThread.DURATION < System.currentTimeMillis()) {
                Samurai.getInstance().getTeamWarHandler().setState(valueOf("COUNTING"));
                WarThread.countdown = System.currentTimeMillis();
            }
        } else {
            WarThread.INITIATED = -1;
        }

    }),

    COUNTING((thread) -> {
        List<WarTeam> teams = Samurai.getInstance().getTeamWarHandler().getQueuedTeams();

        if (teams.size() == 1) {
            Samurai.getInstance().getTeamWarHandler().setState(valueOf("ENDING"));
            return;
        }

        WarThread.notifyIntervals.forEach(l -> {
            if (WarThread.reminders.contains(l) || l < thread.getTimeLeft()) return;

            Samurai.getInstance().getTeamWarHandler().getPlayers().forEach(player -> {
                player.sendTitle(CC.translate("&7" + CC.UNICODE_ARROWS_RIGHT + " &6&lTEAM WAR &7" + CC.UNICODE_ARROWS_LEFT),
                        CC.translate(CC.YELLOW + "Round #" + Samurai.getInstance().getTeamWarHandler().getRound() + "&f will commence in &7" + TimeUtils.formatLongIntoDetailedString((l / 1000)))
                );
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
            });

            WarThread.reminders.add(l);
        });

        if (thread.getTimeLeft() <= 0) {
            Samurai.getInstance().getTeamWarHandler().setState(valueOf("PICKING"));
        }
    }),
    FIGHTING((thread) -> {

    }),
    PICKING((thread) -> {
        List<WarTeam> teams = Samurai.getInstance().getTeamWarHandler().getQueuedTeams();

        if (teams.size() > 1) {
            Samurai.getInstance().getTeamWarHandler().pickNext();
        } else {
            Samurai.getInstance().getTeamWarHandler().setState(valueOf("ENDING"));
        }
    }),
    ENDING((thread) -> {
        for (Player player : Bukkit.getOnlinePlayers()) {
            Tasks.run(() -> {
                for (FancyMessage part : thread.createWinNotification(Samurai.getInstance().getTeamWarHandler().getLastWinner())) {
                    part.send(player);
                }
            });
        }

        Samurai.getInstance().getTeamWarHandler().endGame();
    });

    final Callback<WarThread> tick;

}
