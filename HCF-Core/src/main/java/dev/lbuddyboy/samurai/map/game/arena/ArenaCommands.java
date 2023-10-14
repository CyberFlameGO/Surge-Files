package dev.lbuddyboy.samurai.map.game.arena;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.map.game.arena.select.Selection;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.GameType;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@CommandAlias("gamearena")
@CommandPermission("op")
public class ArenaCommands extends BaseCommand {

    @Subcommand("list")
    public static void listArenas(Player player) {
        List<String> arenas = new ArrayList<>();

        for (GameArena arena : Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
            if (arena.isSetup()) {
                arenas.add(ChatColor.GREEN.toString() + ChatColor.BOLD + arena.getName());
            } else {
                arenas.add(ChatColor.RED.toString() + ChatColor.BOLD + arena.getName());
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Arenas: " + StringUtils.join(arenas, ChatColor.GRAY.toString() + ", "));
    }

    @Subcommand("create")
    public static void createArena(Player player, @Name("arena") String arenaName) {
        if (Samurai.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena named `" + arenaName + "` already exists!");
            return;
        }

        GameArena arena = new GameArena(arenaName);
        Samurai.getInstance().getMapHandler().getGameHandler().getConfig().trackArena(arena);

        player.sendMessage(ChatColor.GREEN + "Created a new arena named `" + arenaName + "`!");
    }

    @Subcommand("delete")
    @CommandCompletion("@gameArenas")
    public static void deleteArena(Player player, @Name("arena") GameArena arena) {
        Samurai.getInstance().getMapHandler().getGameHandler().getConfig().forgetArena(arena);

        player.sendMessage(ChatColor.GREEN + "Deleted the arena named `" + arena.getName() + "`!");
    }

    @Subcommand("setpoint")
    @CommandCompletion("@gameArenas")
    public static void setPointSpawn(Player player, @Name("a/b") String point, @Name("arena") GameArena arena) {
        if (point.equalsIgnoreCase("a") || point.equalsIgnoreCase("b")) {
            if (point.equalsIgnoreCase("a")) {
                arena.setPointA(player.getLocation());
            } else {
                arena.setPointB(player.getLocation());
            }

            Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

            player.sendMessage(ChatColor.GREEN + "Updated point " + point.toUpperCase() + " of " + arena.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Point must be either a/b!");
        }
    }

    @Subcommand("setspec")
    @CommandCompletion("@gameArenas")
    public static void setSpectatorSpawn(Player player, @Name("arena") GameArena arena) {
        arena.setSpectatorSpawn(player.getLocation());
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated Sumo's spectator spawn location!");
    }

    @Subcommand("wand")
    public static void wand(Player player) {
        player.getInventory().addItem(Selection.SELECTION_WAND.clone());
        player.sendMessage(ChatColor.GREEN + "Gave you a selection wand.");
        player.sendMessage(ChatColor.GREEN + "Left click to set 1st corner. Right click to set 2nd corner.");
    }

    @Subcommand("setbounds")
    @CommandCompletion("@gameArenas")
    public static void setBounds(Player player, @Name("arena") GameArena arena) {
        Selection selection = Selection.getOrCreateSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        arena.setBounds(selection.getCuboid());
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated the boundaries of " + arena.getName() + "!");
    }

    @Subcommand("addtype")
    @CommandCompletion("@gameArenas @gameTypes")
    public static void addType(Player player, @Name("arena") GameArena arena, @Name("gametype") GameType gameType) {
        arena.getCompatibleGameTypes().add(gameType.name().toLowerCase());
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is now compatible with the " + gameType.getDisplayName() + " event!");
    }

    @Subcommand("removetype")
    @CommandCompletion("@gameArenas @gameTypes")
    public static void removeType(Player player, @Name("arena") GameArena arena, @Name("gametype") GameType gameType) {
        arena.getCompatibleGameTypes().remove(gameType.name().toLowerCase());
        Samurai.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is no longer compatible with the " + gameType.getDisplayName() + " event!");
    }

}
