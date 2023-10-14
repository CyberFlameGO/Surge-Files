package dev.lbuddyboy.samurai.map.game.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.map.game.impl.gungame.GunGameGame;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.modsuite.ModUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameState;
import dev.lbuddyboy.samurai.map.game.menu.HostMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("game")
public class GameCommand extends BaseCommand {

    @Subcommand("host")
    public static void host(Player sender) {
        if (SpawnTagHandler.isTagged(sender)) {
            sender.sendMessage(ChatColor.RED + "You can't host an event while spawn-tagged!");
            return;
        }

        new HostMenu().openMenu(sender);
    }

    @Subcommand("join")
    public static void join(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        try {
            ongoingGame.addPlayer(player);
        } catch (IllegalStateException e) {
            player.sendMessage(ChatColor.RED.toString() + e.getMessage());
        }
    }

    @Subcommand("leave")
    public static void leave(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (!ongoingGame.isPlaying(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in the ongoing event!");
            return;
        }

        ongoingGame.removePlayer(player);
    }

    @Subcommand("spec")
    public static void spec(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.isPlayingOrSpectating(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in the event.");
            return;
        }

        if (ModUtils.isModMode(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while in mod-mode.");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't join the event while spawn-tagged.");
            return;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return;
        }

        ongoingGame.addSpectator(player);
    }

    @Subcommand("forceend")
    @CommandPermission("op")
    public static void end(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame().endGame();
        player.sendMessage(ChatColor.GREEN + "Successfully ended the ongoing event!");
    }

    @Subcommand("forcestart")
    @CommandPermission("op")
    public static void forcestart(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (ongoingGame.getState() == GameState.WAITING) {
            ongoingGame.forceStart();
        } else {
            player.sendMessage(ChatColor.RED + "Can't force start an event that has already been started.");
        }
    }

    @Subcommand("setgemreq|setgemrequirement|setgemsrequired")
    @CommandPermission("op")
    public static void setgemreq(Player player) {
        if (Samurai.getInstance().getMapHandler().getGameHandler() != null) {
            Game game = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (game != null) {
                game.setGemRequiredToJoin(!game.isGemRequiredToJoin());
                player.sendMessage(ChatColor.GREEN + "Shard requirement has been " + ChatColor.WHITE + (game.isGemRequiredToJoin() ? "enabled" : "disabled") + ChatColor.GREEN + "!");
            } else {
                player.sendMessage(ChatColor.RED + "There is no ongoing game.");
            }
        }
    }

    @Subcommand("setlobby")
    @CommandPermission("op")
    public static void setlobby(Player player) {
        Samurai.getInstance().getMapHandler().getGameHandler().getConfig().setLobbySpawnLocation(player.getLocation());
        player.sendMessage(ChatColor.GREEN + "Updated KitMap event lobby location!");
    }

    @Subcommand("setmaxplayers")
    @CommandPermission("op")
    public static void setlobby(Player player, @Name("maxPlayers") int maxPlayers) {
        if (Samurai.getInstance().getMapHandler().getGameHandler() != null) {
            Game game = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
            if (game != null) {
                game.setMaxPlayers(maxPlayers);
                player.sendMessage(ChatColor.GREEN + "Set max players of " + game.getGameType().getDisplayName() + ChatColor.GREEN + " event to " + ChatColor.WHITE + maxPlayers + ChatColor.GREEN + "!");
            } else {
                player.sendMessage(ChatColor.RED + "There is no ongoing game.");
            }
        }
    }

    @Subcommand("start")
    @CommandPermission("op")
    public static void start(Player player) {
        if (!Samurai.getInstance().getMapHandler().getGameHandler().isOngoingGame()) {
            player.sendMessage(ChatColor.RED.toString() + "There is no ongoing event.");
            return;
        }

        Game ongoingGame = Samurai.getInstance().getMapHandler().getGameHandler().getOngoingGame();
        if (!ongoingGame.isPlaying(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are not in the ongoing event!");
            return;
        }

        if (ongoingGame.getHost() != player.getUniqueId()) {
            player.sendMessage(ChatColor.RED + "You are not the host of the ongoing event!");
            return;
        }

        if (ongoingGame.getPlayers().size() < ongoingGame.getGameType().getMinForceStartPlayers()) {
            player.sendMessage(ChatColor.RED + "There must be at least " + ongoingGame.getGameType().getMinForceStartPlayers() + " players to start the game!");
            return;
        }

        if (ongoingGame.getState() == GameState.WAITING) {
            ongoingGame.hostForceStart();
        } else {
            player.sendMessage(ChatColor.RED + "Can't start an event that has already been started.");
        }
    }

    @Subcommand("toggle")
    @CommandPermission("op")
    public static void toggle(Player sender) {
        Samurai.getInstance().getMapHandler().getGameHandler().setDisabled(!Samurai.getInstance().getMapHandler().getGameHandler().isDisabled());

        if (Samurai.getInstance().getMapHandler().getGameHandler().isDisabled()) {
            sender.sendMessage(ChatColor.YELLOW + "Events are now disabled!");
        } else {
            sender.sendMessage(ChatColor.GREEN + "Events are now enabled!");
        }
    }

    @Subcommand("gungame")
    @CommandPermission("op")
    public static void gungame(Player sender) {
        sender.getInventory().addItem(GunGameGame.getShotGun());
        sender.getInventory().addItem(GunGameGame.getRifle());
        sender.getInventory().addItem(GunGameGame.getSmg());
    }

}
