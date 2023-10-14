package dev.lbuddyboy.samurai.custom.teamwar.thread;

import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarState;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WarThread extends Thread {

    public static long INITIATED = -1, DURATION;
    public static long lastNotified = System.currentTimeMillis();
    public static long countdown = System.currentTimeMillis();
    @Getter
    public static final List<Long> notifyIntervals = Arrays.asList(
            JavaUtils.parse("120s"),
            JavaUtils.parse("110s"),
            JavaUtils.parse("100s"),
            JavaUtils.parse("90s"),
            JavaUtils.parse("75s"),
            JavaUtils.parse("60s"),
            JavaUtils.parse("45s"),
            JavaUtils.parse("30s"),
            JavaUtils.parse("20s"),
            JavaUtils.parse("15s"),
            JavaUtils.parse("10s"),
            JavaUtils.parse("5s"),
            JavaUtils.parse("4s"),
            JavaUtils.parse("3s"),
            JavaUtils.parse("2s"),
            JavaUtils.parse("1s")
    );
    public static List<Long> reminders = new ArrayList<>();

    @Override
    public void run() {
        while (true) {

            Samurai.getInstance().getTeamWarHandler().getState().getTick().callback(this);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public long getTimeLeft() {
        return (DURATION + countdown) - System.currentTimeMillis();
    }

    public List<FancyMessage> createHostNotification() {
        int queued = Samurai.getInstance().getTeamWarHandler().getQueuedTeams().size();
        int minTeamSize = Samurai.getInstance().getTeamWarHandler().getMinimumTeamSize();

        return Arrays.asList(
                new FancyMessage(" ").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("███").color(ChatColor.YELLOW)
                        .then("██").color(ChatColor.GRAY)
                        .then(CC.translate(" &6&lTeam War Event")),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY)
                        .then(CC.translate(" &7Teams: &f" + queued + " &e" + CC.UNICODE_VERTICAL_BAR + " &7Minimum: " + minTeamSize)),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Hosted by the ").color(ChatColor.GRAY)
                        .then("Management Team").color(ChatColor.DARK_RED),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY)
                        .command("/teamwar join")
                        .then(" [").color(ChatColor.GRAY)
                        .then("Click to join the event").color(ChatColor.YELLOW)
                        .command("/teamwar join")
                        .formattedTooltip(new FancyMessage("Click here to join the event.").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage(" ").color(ChatColor.GRAY)
        );
    }

    public List<FancyMessage> createWinNotification(WarTeam winner) {
        return Arrays.asList(
                new FancyMessage(" ").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("██").color(ChatColor.GRAY)
                        .then("███").color(ChatColor.YELLOW)
                        .then("██").color(ChatColor.GRAY)
                        .then(CC.translate(" &6&lTeam War Event")),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY)
                        .then(CC.translate(" &7Winner: &f" + winner.getTeam().getName())),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Hosted by the ").color(ChatColor.GRAY)
                        .then("Management Team").color(ChatColor.DARK_RED),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.YELLOW)
                        .then("███").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage(" ").color(ChatColor.GRAY)
        );
    }

}
