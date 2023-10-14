package dev.lbuddyboy.samurai.map.duel.arena;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.map.game.arena.select.Selection;
import dev.lbuddyboy.samurai.Samurai;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("duelarena")
@CommandPermission("op")
public class ArenaCommand extends BaseCommand {

    @Subcommand("create")
    public static void createArena(Player player, @Name("name") String arenaName) {
        if (Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenaByName(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena named `" + arenaName + "` already exists!");
            return;
        }

        DuelArena arena = new DuelArena(arenaName);
        Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().addArena(arena);

        player.sendMessage(ChatColor.GREEN + "Created a new arena named `" + arenaName + "`!");
    }

    @Subcommand("delete")
    @CommandCompletion("@duelArenas")
    public static void deleteArena(Player player, @Name("arena") DuelArena arena) {
        Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().removeArena(arena);

        player.sendMessage(ChatColor.GREEN + "Deleted the arena named `" + arena.getName() + "`!");
    }

    @Subcommand("list")
    public static void listArenas(Player player) {
        List<String> arenas = Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().getArenas().stream()
                .map(arena -> (arena.isSetup() ? ChatColor.GREEN : ChatColor.RED).toString() + arena.getName())
                .collect(Collectors.toList());

        player.sendMessage(ChatColor.YELLOW + "Arenas: " + StringUtils.join(arenas, ChatColor.GRAY.toString() + ", "));
    }

    @Subcommand("setpoint")
    @CommandCompletion("@duelArenas")
    public static void setPointSpawn(Player player, @Name("a/b") String point, @Name("arena") DuelArena arena) {
        if (point.equalsIgnoreCase("a") || point.equalsIgnoreCase("b")) {
            if (point.equalsIgnoreCase("a")) {
                arena.setPointA(player.getLocation());
            } else {
                arena.setPointB(player.getLocation());
            }

            Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().saveArenas();

            player.sendMessage(ChatColor.GREEN + "Updated point " + point.toUpperCase() + " of " + arena.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Point must be either a/b!");
        }
    }

    @Subcommand("wand")
    public static void wand(Player player) {
        player.getInventory().addItem(Selection.SELECTION_WAND.clone());
        player.sendMessage(ChatColor.GREEN + "Gave you a selection wand.");
        player.sendMessage(ChatColor.GREEN + "Left click to set 1st corner. Right click to set 2nd corner.");
    }

    @Subcommand("setbounds")
    @CommandCompletion("@duelArenas")
    public static void setBounds(Player player, @Name("arena") DuelArena arena) {
        Selection selection = Selection.getOrCreateSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        arena.setBounds(selection.getCuboid());
        Samurai.getInstance().getMapHandler().getDuelHandler().getArenaHandler().saveArenas();

        player.sendMessage(ChatColor.GREEN + "Updated the boundaries of " + arena.getName() + "!");
    }

}
