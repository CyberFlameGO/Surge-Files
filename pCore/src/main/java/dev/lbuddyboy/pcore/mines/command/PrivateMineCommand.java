package dev.lbuddyboy.pcore.mines.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.drawethree.xprison.utils.misc.ProgressBar;
import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.mines.menu.PrivateMineMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.ActionBarAPI;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.fanciful.FancyMessage;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockChange;
import net.minecraft.server.v1_8_R3.PacketPlayOutMultiBlockChange;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandAlias("privatemine|pmine|mine|mines")
public class PrivateMineCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(sender.getUniqueId());

        if (cache == null) {
            cache = pCore.getInstance().getPrivateMineHandler().createPrivateMine(sender.getUniqueId());

            pCore.getInstance().getPrivateMineHandler().updateCache(sender.getUniqueId(), cache);

            sender.sendMessage(CC.translate("&aCreating a new private mine..."));
            sender.teleport(cache.getSpawnLocation().clone().add(0, 3, 0));
            return;
        }

        new PrivateMineMenu().openMenu(sender);
    }

    @Subcommand("disband")
    @CommandCompletion("@players")
    @CommandPermission("op")
    public void disband(CommandSender sender, @Name("player") OfflinePlayer player) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        for (Player p : cache.getBounds().getWorld().getPlayers()) {
            if (!cache.getBounds().contains(p.getLocation())) continue;

            p.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
            p.sendMessage(CC.translate("&cThis private mine was disbanded, so you've been teleported away."));
        }

        pCore.getInstance().getPrivateMineHandler().deletePrivateMine(player.getUniqueId(), cache);
        for (Player other : cache.getPlayers()) {
            other.teleport(SettingsConfiguration.SPAWN_LOCATION.getLocation());
        }

    }

    @Subcommand("reset")
    @CommandCompletion("@players")
    public void reset(CommandSender sender, @Name("player") OfflinePlayer player) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        cache.reset();

    }

    @Subcommand("map")
    @CommandCompletion("@players")
    public void map(Player sender, @Name("player") OfflinePlayer player) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        List<Location> locations = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            locations.addAll(cache.getBounds().getFourCorners(i));
        }

        for (Location location : locations) {
            CraftPlayer craftPlayer = ((CraftPlayer) sender);
            if (craftPlayer.getHandle().playerConnection != null) {
                PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(((CraftWorld)location.getWorld()).getHandle(), new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
                packet.block = CraftMagicNumbers.getBlock(Material.EMERALD_BLOCK).fromLegacyData(0);
                craftPlayer.getHandle().playerConnection.sendPacket(packet);
            }
        }

    }

    @Subcommand("info")
    @CommandCompletion("@players")
    public void info(CommandSender sender, @Name("player") OfflinePlayer player) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        FancyMessage message = new FancyMessage(CC.GOLD + player.getName() + "'s Mine ");

        if (cache.getBounds().contains(((Player)sender).getLocation())) {
            sender.sendMessage(CC.translate("&aYou are currently in here!"));
        }

        String lowerCoords = cache.getBounds().getLowerNE().getBlockX() + " " + cache.getBounds().getLowerNE().getBlockY() + " " + cache.getBounds().getLowerNE().getBlockZ();
        String upperCoords = cache.getBounds().getUpperSW().getBlockX() + " " + cache.getBounds().getUpperSW().getBlockY() + " " + cache.getBounds().getUpperSW().getBlockZ();
        message.then(CC.translate("&a[Bounds A]")).tooltip(CC.translate("&7Coords: " + lowerCoords + " (Click to TP)")).command("/tppos " + lowerCoords);
        message.then(CC.translate(" &c[Bounds B]")).tooltip(CC.translate("&7Coords: " + upperCoords + " (Click to TP)")).command("/tppos " + upperCoords);

        String pitLowerCoords = cache.getMinePit().getLowerNE().getBlockX() + " " + cache.getMinePit().getLowerNE().getBlockY() + " " + cache.getMinePit().getLowerNE().getBlockZ();
        String pitUpperCoords = cache.getMinePit().getUpperSW().getBlockX() + " " + cache.getMinePit().getUpperSW().getBlockY() + " " + cache.getMinePit().getUpperSW().getBlockZ();
        message.then(CC.translate(" &a[Pit A]")).tooltip(CC.translate("&7Coords: " + pitLowerCoords + " (Click to TP)")).command("/tppos " + pitLowerCoords);
        message.then(CC.translate(" &c[Pit B]")).tooltip(CC.translate("&7Coords: " + pitUpperCoords + " (Click to TP)")).command("/tppos " + pitUpperCoords);

        String boxLowerCoords = cache.getMinePitBox().getLowerNE().getBlockX() + " " + cache.getMinePitBox().getLowerNE().getBlockY() + " " + cache.getMinePitBox().getLowerNE().getBlockZ();
        String boxUpperCoords = cache.getMinePitBox().getUpperSW().getBlockX() + " " + cache.getMinePitBox().getUpperSW().getBlockY() + " " + cache.getMinePitBox().getUpperSW().getBlockZ();
        message.then(CC.translate(" &a[Box A]")).tooltip(CC.translate("&7Coords: " + boxLowerCoords + " (Click to TP)")).command("/tppos " + boxLowerCoords);
        message.then(CC.translate(" &c[Box B]")).tooltip(CC.translate("&7Coords: " + boxUpperCoords + " (Click to TP)")).command("/tppos " + boxUpperCoords);

        String spawnCoords = cache.getSpawnLocation().getBlockX() + " " + cache.getSpawnLocation().getBlockY() + " " + cache.getSpawnLocation().getBlockZ();
        message.then(CC.translate(" &e[Spawn Location]")).tooltip(CC.translate("&7Coords: " + spawnCoords + " (Click to TP)")).command("/tppos " + spawnCoords);

        message.then(CC.translate(" &d[Reset]")).tooltip(CC.translate("&7Click to reset")).command("/pmine reset " + player.getName());

        message.send(sender);
    }

    @Subcommand("setlevel")
    @CommandCompletion("@players")
    @CommandPermission("op")
    public void reset(CommandSender sender, @Name("player") OfflinePlayer player, @Name("level") int level) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        try {
            MineUser user = pCore.getInstance().getMineUserHandler().getMineUserAsync(player.getUniqueId()).get();

            user.getPrivateMine().getRankInfo().setRank(cache, level);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cache.reset();

    }

    @Subcommand("setprogress")
    @CommandCompletion("@players")
    @CommandPermission("op")
    public void setprogress(CommandSender sender, @Name("player") OfflinePlayer player, @Name("progress") double progress) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        try {
            MineUser user = pCore.getInstance().getMineUserHandler().getMineUserAsync(player.getUniqueId()).get();

            user.getPrivateMine().getRankInfo().setProgress(progress);
            cache.increaseProgress(user.getUuid(), -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cache.reset();

    }

    @Subcommand("increaseprogress")
    @CommandCompletion("@players")
    @CommandPermission("op")
    public void increaseprogress(CommandSender sender, @Name("player") OfflinePlayer player, @Name("progress") double progress) {
        PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

        if (cache == null) {
            sender.sendMessage(CC.translate("&aNo mine exists..."));
            return;
        }

        cache.increaseProgress(player.getUniqueId(), progress);
        cache.reset();

    }

}
