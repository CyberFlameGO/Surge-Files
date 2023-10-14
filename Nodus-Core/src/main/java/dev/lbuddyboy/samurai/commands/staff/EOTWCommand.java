package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.flash.util.bukkit.Tasks;
import dev.lbuddyboy.samurai.listener.EndListener;
import dev.lbuddyboy.samurai.server.threads.EOTWTask;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@CommandAlias("eotw")
@CommandPermission("foxtrot.eotw")
public class EOTWCommand extends BaseCommand {

    @Getter
    @Setter
    private static boolean ffaEnabled = false;
    @Getter
    @Setter
    private static long ffaActiveAt = -1L;
    @Getter
    @Setter
    private static Location ffaLocation = null;
    public static EOTWTask eotwTask;

    public static List<UUID> eotwWhitelist = new ArrayList<>();

    @Subcommand("start")
    public static void eotw(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in console.");
            return;
        }

        Samurai.getInstance().getServerHandler().setEOTW(!Samurai.getInstance().getServerHandler().isEOTW());

        EndListener.endActive = !Samurai.getInstance().getServerHandler().isEOTW();

        if (Samurai.getInstance().getServerHandler().isEOTW()) {
            for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW has commenced.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All SafeZones are now Deathban.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");

            for (Team team : Samurai.getInstance().getTeamHandler().getTeams()) {
                if (team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
                    team.rename("EOTW");
                    team.setDTR(DTRBitmask.KOTH.getBitmask());
                    continue;
                }
                if (team.getMembers().size() == 0) {
                    team.disband();
                    continue;
                }

                team.setDTR(-999);
                team.setDTRCooldown(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                LandBoard.getInstance().clear(team);
            }

            if (Samurai.getInstance().getEventHandler().getEvent("EOTW") == null) {
                KOTH eotw = new KOTH("EOTW", Bukkit.getWorld("world").getSpawnLocation());
                eotw.setCapTime(8 * 60);
                eotw.setCapDistance(3);
                Tasks.runLater(eotw::activate, 20);
            } else {
                Tasks.runLater(() -> {
                    Bukkit.dispatchCommand(sender, "koth activate EOTW");
                }, 20);
            }

            Bukkit.getOnlinePlayers().forEach(player -> eotwWhitelist.add(player.getUniqueId()));
            eotwTask = new EOTWTask();
            eotwTask.runTaskTimer(Samurai.getInstance(), 20 * 5, 20 * 5);
        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in EOTW mode.");
        }
    }

    @Subcommand("tpall|tp")
    public static void eotwTpAll(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (!Samurai.getInstance().getServerHandler().isEOTW()) {
            sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW. (/eotw)");
            return;
        }

        for (Player onlinePlayer : Samurai.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.teleport(sender.getLocation());
        }

        sender.sendMessage(ChatColor.RED + "Players teleported.");
    }

    @Subcommand("invis|invisibility")
    public static void eotwInvis(Player sender) {
        if (sender.getGameMode() != GameMode.CREATIVE) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in creative.");
            return;
        }

        if (!Samurai.getInstance().getServerHandler().isEOTW() || !ffaEnabled) {
            sender.sendMessage(ChatColor.RED + "This command must be ran during EOTW FFA. (/eotw ffa)");
            return;
        }

        PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false);
        for (Player onlinePlayer : Samurai.getInstance().getServer().getOnlinePlayers()) {
            if (ModUtils.isModMode(onlinePlayer)) continue;
            onlinePlayer.addPotionEffect(invis, true);
        }

        sender.sendMessage(ChatColor.RED + "Gave everyone invisibility.");
    }

    @Subcommand("preeotw|pre|prestage")
    public static void preeotw(CommandSender sender) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in console.");
            return;
        }

        Samurai.getInstance().getServerHandler().setPreEOTW(!Samurai.getInstance().getServerHandler().isPreEOTW());
        Samurai.getInstance().getDeathbanMap().wipeDeathbans();

        if (Samurai.getInstance().getServerHandler().isPreEOTW()) {
            for (Player player : Samurai.getInstance().getServer().getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1F, 1F);
            }

            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.DARK_RED + "[Pre-EOTW]");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW is about to commence.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "████" + ChatColor.RED + "██" + " " + ChatColor.RED + "PvP Protection is disabled.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "All players have been un-deathbanned.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.DARK_RED + "█████" + ChatColor.RED + "█" + " " + ChatColor.RED + " ");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");

        } else {
            sender.sendMessage(ChatColor.RED + "The server is no longer in Pre-EOTW mode.");
        }
    }

    @Subcommand("setffaspawn")
    public static void ffaspawn(Player sender) {
        ffaLocation = sender.getLocation();
        sender.sendMessage(CC.translate("&aSuccessfully set the location of the ffa! This location does not store after a reboot!"));
    }

    @Subcommand("startffa")
    public static void ffa(CommandSender sender, @Name("minute-delay") int delay) {
        if (sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This command must be ran in console.");
            return;
        }

        ffaEnabled = true;
        ffaActiveAt = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(delay);
        sender.sendMessage(ChatColor.GREEN + "FFA countdown initiated.");

        Bukkit.getScheduler().runTask(Samurai.getInstance(), () -> {
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " will commence in: " + delay + " minutes.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED.toString() + "If you ally, you will be punished.");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
        });

        Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
            if (!ffaEnabled) return;
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█████" + ChatColor.RED + "█");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.DARK_RED + "[EOTW]");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "████" + ChatColor.RED + "██" + " " + ChatColor.RED.toString() + ChatColor.BOLD + "EOTW " + ChatColor.GOLD.toString() + ChatColor.BOLD + "FFA" + ChatColor.RED.toString() + ChatColor.BOLD + " has now commenced!");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████" + " " + ChatColor.RED + "Good luck and have fun!");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "█" + ChatColor.GOLD + "█" + ChatColor.RED + "█████");
            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED + "███████");

            List<Team> teams = new ArrayList<>(Samurai.getInstance().getTeamHandler().getTeams());

            for (Team team : teams) {
                team.disband();
            }

            Samurai.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");

            Map<Feature, Boolean> features = Samurai.getInstance().getFeatureHandler().getDisabledFeatures();

            features.put(Feature.BUY_SHOP, true);
            features.put(Feature.SELL_SHOP, true);
            features.put(Feature.CLAY_SHOP, true);
            features.put(Feature.CONCRETE_SHOP, true);
            features.put(Feature.DECO_SHOP, true);
            features.put(Feature.SHARD_SHOP, true);
            features.put(Feature.GLASS_SHOP, true);
            features.put(Feature.MAIN_SHOP, true);
            features.put(Feature.PLAYTIME_REWARDS, true);
            features.put(Feature.BATTLE_PASS, true);
            features.put(Feature.TEAM_BREW, true);
            features.put(Feature.SUPPLY_DROP, true);
            features.put(Feature.END_WORLD, true);
            features.put(Feature.NETHER_WORLD, true);

            Samurai.getInstance().getFeatureHandler().save();

            if (ffaLocation != null) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(ffaLocation);
                }
            }
            PotionEffect invis = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false);
            for (Player onlinePlayer : Samurai.getInstance().getServer().getOnlinePlayers()) {
                if (ModUtils.isModMode(onlinePlayer)) continue;
                onlinePlayer.addPotionEffect(invis, true);
            }

        }, (long) delay * 60 * 20);
    }

    public static boolean realFFAStarted() {
        return ffaEnabled && ffaActiveAt < System.currentTimeMillis();
    }

}