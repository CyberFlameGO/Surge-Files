package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.collect.Sets;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.MessageConfiguration;
import dev.lbuddyboy.samurai.custom.schedule.command.ScheduleCommand;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import dev.lbuddyboy.samurai.api.FoxtrotConfiguration;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.nametag.FrozenNametagHandler;
import dev.lbuddyboy.samurai.nametag.impl.FoxtrotNametagProvider;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.server.timer.command.ServerTimerCommand;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@CommandAlias("sotw|startoftheworld")
public class SOTWCommand extends BaseCommand {

    @Getter
    private static final Map<String, Long> customTimers = new HashMap<>();
    private static final Set<UUID> sotwEnabled = Sets.newHashSet();
    private static int currentMsg = 0;

    @Subcommand("unlockserver|unlock")
    @CommandPermission("foxtrot.sotw")
    public static void unlockserver(Player sender) {

        sender.sendMessage(CC.translate("&aThe multipliers will start in a little dont get your cock in a knot"));
        ScheduleCommand.startdefaults(Bukkit.getConsoleSender());
        SOTWCommand.sotwStart(Bukkit.getConsoleSender(), "2h");
        Bukkit.dispatchCommand(sender, "queuetoggle Aura");
        Bukkit.dispatchCommand(sender, "rawbc GLOBAL &a&lAura's &fqueue is now &eopen&f. You can now all join the server! &7(/joinq Aura)");
        Tasks.runLater(() -> {
            ServerTimerCommand.starthcfdefaults(Bukkit.getConsoleSender());
        }, 20 * 10);
    }

    @Subcommand("enable")
    public static void sotwEnable(Player sender) {
        if (!isSOTWTimer()) {
            sender.sendMessage(ChatColor.RED + "You can't /sotw enable when there is no SOTW timer...");
            return;
        }

        if (sotwEnabled.add(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "Your SOTW timer was already disabled...");
        }

        fixinvis(Bukkit.getConsoleSender());
        for (Player toRefresh : Samurai.getInstance().getServer().getOnlinePlayers()) {
            FrozenNametagHandler.reloadPlayer(toRefresh, sender);
            FoxtrotNametagProvider.updateLunarTag(toRefresh, sender);
        }
    }

    @Subcommand("fixinvis|fixinvisibles")
    @CommandPermission("foxtrot.sotw")
    public static void fixinvis(CommandSender sender) {
        List<Player> playersInSpawn = Bukkit.getOnlinePlayers().stream().filter(player -> !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())).collect(Collectors.toList());
        for (Player player : playersInSpawn) {
            if (player.hasMetadata("modmode")) {
                continue;
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.showPlayer(Samurai.getInstance(), player);
            }
        }

        sender.sendMessage(ChatColor.GREEN + "SOTW invis people has been fixed.");
    }

    @Subcommand("cancel")
    @CommandPermission("foxtrot.sotw")
    public static void sotwCancel(CommandSender sender) {
        ServerTimer sotwTimer = Samurai.getInstance().getTimerHandler().getSOTWTimer();

        if (sotwTimer != null) {
            ServerTimerCommand.delete(sender, sotwTimer);
            sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");

            List<Player> playersInSpawn = Bukkit.getOnlinePlayers().stream().filter(player -> DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())).collect(Collectors.toList());
            for (Player player : playersInSpawn) {
                if (player.hasMetadata("modmode")) {
                    continue;
                }

                for (Player target : Bukkit.getOnlinePlayers()) {
                    target.showPlayer(Samurai.getInstance(), player);
                }
            }
            return;
        }

        sender.sendMessage(ChatColor.RED + "SOTW timer is not active.");
    }

    @Subcommand("start")
    @CommandPermission("foxtrot.sotw")
    public static void sotwStart(CommandSender sender, @Name("time") String time) {
        int seconds = TimeUtils.parseTime(time);

        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        ServerTimer sotwTimer = new ServerTimer("SOTW",
                FoxtrotConfiguration.SOTW_TIMER_DISPLAY.getString(),
                FoxtrotConfiguration.SOTW_TIMER_CONTEXT.getString(),
                System.currentTimeMillis(),
                seconds * 1000L
        );

        Samurai.getInstance().getTimerHandler().getServerTimers().put("SOTW", sotwTimer);

        List<Player> playersInSpawn = Bukkit.getOnlinePlayers().stream().filter(player -> DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())).collect(Collectors.toList());
        for (Player player : playersInSpawn) {
            if (player.hasMetadata("modmode")) {
                continue;
            }
            for (Player target : Bukkit.getOnlinePlayers()) {
                target.hidePlayer(Samurai.getInstance(), player);
            }
        }

        sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (sotwTimer.isOver()) {
                    for (String s : FoxtrotConfiguration.SOTW_TIMER_DISPLAY.getStringList()) {
                        Bukkit.broadcastMessage(CC.translate(s));
                    }
                    SamuraiCommand.setsimulationdistance(Bukkit.getConsoleSender(), 8);
                    SamuraiCommand.setviewdist(Bukkit.getConsoleSender(), 8);
                    cancel();
                    return;
                }

                if (!isSOTWTimer()) {
                    cancel();
                    return;
                }

                for (int i = 0; i < MessageConfiguration.SOTW_AUTO_MESSAGES_DELAY.getInt(); i++) {
                    Bukkit.broadcastMessage(CC.translate(MessageConfiguration.SOTW_AUTO_MESSAGES.getStringList().get(currentMsg)));
                }
                currentMsg++;
                if (currentMsg >= MessageConfiguration.SOTW_AUTO_MESSAGES.getStringList().size()) {
                    currentMsg = 0;
                }
            }
        }.runTaskTimerAsynchronously(Samurai.getInstance(), 20, 20 * 60 * 3);

        Samurai.getInstance().getServerHandler().setSotwStartedAt();
        SamuraiCommand.setsimulationdistance(Bukkit.getConsoleSender(), 4);
        SamuraiCommand.setviewdist(Bukkit.getConsoleSender(), 4);
    }

    @Subcommand("extend")
    @CommandPermission("foxtrot.sotw")
    public static void sotwExtend(CommandSender sender, @Name("time") String time) {
        ServerTimer sotwTimer = Samurai.getInstance().getTimerHandler().getSOTWTimer();

        int seconds;
        try {
            seconds = TimeUtils.parseTime(time);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (seconds < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (sotwTimer == null) {
            sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
            return;
        }

        sotwTimer.setDuration(sotwTimer.getDuration() + (seconds * 1000L));
        sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
    }

    public static boolean isSOTWTimer() {
        return Samurai.getInstance().getTimerHandler().isSOTWTimer();
    }

    public static boolean hasSOTWEnabled(UUID uuid) {
        return sotwEnabled.contains(uuid);
    }

    public static boolean isPartnerPackageHour() {
        return Samurai.getInstance().getTimerHandler().isAbilityHour();
    }

}