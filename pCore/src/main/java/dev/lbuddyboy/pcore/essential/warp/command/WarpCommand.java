package dev.lbuddyboy.pcore.essential.warp.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.HelpEntry;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.essential.warp.Warp;
import dev.lbuddyboy.pcore.essential.warp.menu.WarpMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.impl.TeleportTimer;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Config;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("warp|warps")
public class WarpCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("pcore.command.warp.create")
    public void create(Player sender, @Name("name") @Single String name) {
        if (pCore.getInstance().getWarpHandler().getWarps().containsKey(name)) {
            return;
        }

        Warp warp = new Warp(name, sender.getLocation());

        warp.save();

        sender.sendMessage(CC.translate("&aYou have created a new warp!"));
    }

    @Default
    @CommandCompletion("@warps")
    public void warp(Player sender, @Name("warp") @Default("null") Warp warp) {

        if (warp == null) {
            new WarpMenu().openMenu(sender);
            return;
        }

        if (!sender.hasPermission(warp.getPermission())) {
            sender.sendMessage(CC.translate("&cYou do not have permission to use this warp."));
            return;
        }

        warp.startWarping(sender);
    }

    @Subcommand("setlocation")
    @CommandPermission("pcore.command.warp.setlocation")
    @CommandCompletion("@warps")
    public void setlocation(Player sender, @Name("warp") Warp warp) {
        warp.setLocation(sender.getLocation());
        warp.save();
        sender.sendMessage(CC.translate("&aYou have set the location of the " + warp.getName() + " warp to your current location."));
    }

    @Subcommand("setpermission")
    @CommandPermission("pcore.command.warp.setpermission")
    @CommandCompletion("@warps")
    public void setpermission(Player sender, @Name("warp") Warp warp, @Name("permission") @Single String permission) {

        warp.setPermission(permission);
        warp.save();
        sender.sendMessage(CC.translate("&aYou have set the permission of the " + warp.getName() + " warp to '" + permission + "'."));
    }

    @Subcommand("setwarmup")
    @CommandPermission("pcore.command.warp.setwarmup")
    @CommandCompletion("@warps")
    public void setwarmup(Player sender, @Name("warp") Warp warp, @Name("seconds") int seconds) {
        warp.setWarmup(seconds * 1000L);
        warp.save();
        sender.sendMessage(CC.translate("&aYou have set the teleport warmup of the " + warp.getName() + " warp to " + seconds + " seconds."));
    }

    @Subcommand("setname")
    @CommandPermission("pcore.command.warp.setname")
    @CommandCompletion("@warps")
    public void setname(Player sender, @Name("warp") Warp warp, @Name("name") String name) {
        String oldName = warp.getName();
        warp.setName(name);
        warp.save();
        sender.sendMessage(CC.translate("&aYou have set the name of the " + oldName + " warp to '" + name + "'."));
    }

    @Subcommand("setdisplaymaterial")
    @CommandPermission("pcore.command.warp.setdisplaymaterial")
    @CommandCompletion("@warps")
    public void setdisplaymaterial(Player sender, @Name("warp") Warp warp) {

        if (sender.getItemInHand() == null || sender.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need an item in your hand to do this."));
            return;
        }

        warp.getDisplayItem().setType(sender.getItemInHand().getType());
        warp.save();

        sender.sendMessage(CC.translate("&aYou have set the display material for the " + warp.getName() + " warp to " + warp.getDisplayItem().getType().name() + "."));
    }

    @Subcommand("delete")
    @CommandPermission("pcore.command.warp.delete")
    @CommandCompletion("@warps")
    public void delete(Player sender, @Name("warp") Warp warp) {
        warp.delete();
        sender.sendMessage(CC.translate("&aYou have deleted the " + warp.getName() + " warp."));
    }

    @HelpCommand
    public void help(CommandSender sender, CommandHelp help){
        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&9&lWarp Help"));
        sender.sendMessage(CC.CHAT_BAR);
        for (HelpEntry entry : help.getHelpEntries()) {
            if (entry.getDescription().isEmpty()) {
                sender.sendMessage(CC.translate("&c/" + entry.getCommand()));
            } else {
                sender.sendMessage(CC.translate("&c/" + entry.getCommand() + " - " + entry.getDescription()));
            }
        }
        sender.sendMessage(CC.CHAT_BAR);
    }

}
