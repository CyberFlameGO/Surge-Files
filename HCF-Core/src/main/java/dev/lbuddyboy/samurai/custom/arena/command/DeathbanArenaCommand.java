package dev.lbuddyboy.samurai.custom.arena.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.maps.DeathbanMap;
import dev.lbuddyboy.samurai.server.deathban.Deathban;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.Cuboid;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CommandAlias("arena")
public class DeathbanArenaCommand extends BaseCommand {

    @Getter private static final Map<UUID, Location> locA = new HashMap<>();
    @Getter private static final Map<UUID, Location> locB = new HashMap<>();

    @Subcommand("tryrevive")
    public static void tryrevive(Player sender) {
        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou cannot do this if you aren't deathbanned."));
            return;
        }

        DeathbanMap deathbanMap = Samurai.getInstance().getDeathbanMap();
        int friendLives = Samurai.getInstance().getFriendLivesMap().getLives(sender.getUniqueId());
        if (friendLives > 0) {
            Samurai.getInstance().getFriendLivesMap().setLives(sender.getUniqueId(), friendLives - 1);
            deathbanMap.revive(sender.getUniqueId());

            sender.sendMessage(ChatColor.GREEN + "You have used a life to revive yourself!");
            sender.teleport(Samurai.getInstance().getServerHandler().getSpawnLocation());
            sender.getInventory().clear();
            return;
        }

        long seconds = (deathbanMap.getDeathban(sender.getUniqueId()) - System.currentTimeMillis()) / 1000;
        sender.sendMessage(CC.RED + "You are currently deathbanned! Come back in " + TimeUtils.formatLongIntoDetailedString(seconds) + "!");
    }

    @Subcommand("applykit")
    public static void applykit(Player sender) {
        if (!Samurai.getInstance().getArenaHandler().isDeathbanned(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&cYou cannot do this if you aren't deathbanned."));
            return;
        }

        Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit("PvP").apply(sender);
    }

    @Subcommand("deathbantest")
    @CommandPermission("op")
    public static void deathbantest(Player sender) {
        sender.teleport(Samurai.getInstance().getArenaHandler().getSpawn());
        Samurai.getInstance().getDeathbanMap().deathban(sender.getUniqueId(), 30);
    }

    @Subcommand("setspawn")
    @CommandPermission("op")
    public static void setspawn(Player sender) {
        Samurai.getInstance().getArenaHandler().setSpawn(sender.getLocation());
        Samurai.getInstance().getArenaHandler().save();
        sender.sendMessage(CC.translate("&aSet the spawn point of the deathban arena to your location."));
    }

    @Subcommand("seta")
    @CommandPermission("op")
    public static void setA(Player sender) {
        locA.put(sender.getUniqueId(), sender.getLocation());
        sender.sendMessage(CC.translate("&aSet the point a to your location."));
    }

    @Subcommand("setb")
    @CommandPermission("op")
    public static void setB(Player sender) {
        locB.put(sender.getUniqueId(), sender.getLocation());
        sender.sendMessage(CC.translate("&aSet the point b to your location."));
    }

    @Subcommand("setsafezone")
    @CommandPermission("op")
    public static void setsafezone(Player sender) {

        Location locA = DeathbanArenaCommand.getLocA().get(sender.getUniqueId());
        Location locB = DeathbanArenaCommand.getLocB().get(sender.getUniqueId());

        if (locA == null) {
            sender.sendMessage(CC.translate("&cYou need to set the a location."));
            return;
        }

        if (locB == null) {
            sender.sendMessage(CC.translate("&cYou need to set the b location."));
            return;
        }

        Samurai.getInstance().getArenaHandler().setSafeZone(new Cuboid(locA, locB));
        Samurai.getInstance().getArenaHandler().save();
        sender.sendMessage(CC.translate("&aSuccessfully set the safe zone bounds of the deathban arena."));

    }

}
