package dev.lbuddyboy.pcore.events.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.pcore.command.SelectionWandCommand;
import dev.lbuddyboy.pcore.events.koth.KoTH;
import dev.lbuddyboy.pcore.listener.SelectionListener;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("koth")
public class KoTHCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("Samurai.command.koth")
    public static void create(CommandSender sender, @Name("name") @Single String name) {
        if (pCore.getInstance().getEventHandler().getEvents().containsKey(name)) {
            sender.sendMessage(CC.translate("&cThere's already an event under that name."));
            return;
        }

        KoTH koTH = new KoTH(name);

        sender.sendMessage(CC.translate("&aSuccessfully created the " + name + " KoTH."));
        pCore.getInstance().getEventHandler().getEvents().put(name, koTH);
        koTH.save();
    }

    @Subcommand("delete")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void delete(CommandSender sender, @Name("name") @Single KoTH koTH) {
        if (koTH.isActive()) {
            koTH.end();
        }
        koTH.delete();

        sender.sendMessage(CC.translate("&aSuccessfully deleted the " + koTH.getName() + " KoTH."));
    }

    @Subcommand("capzonewand")
    @CommandPermission("Samurai.command.koth")
    public static void capzonewand(Player sender) {
        sender.getInventory().addItem(SelectionListener.CLAIM_WAND);
    }

    @Subcommand("setcapzone")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void setcapzone(Player sender, @Name("koth") KoTH koTH) {
        Location pos1 = SelectionListener.getPos1().get(sender);
        Location pos2 = SelectionListener.getPos2().get(sender);

        if (pos1 == null) {
            sender.sendMessage(CC.translate("&cPlease select a first position by doing /koth capzonewand and left clicking a block."));
            return;
        }

        if (pos2 == null) {
            sender.sendMessage(CC.translate("&cPlease select a first position by doing /koth capzonewand and right clicking a block."));
            return;
        }

        SelectionListener.getPos1().remove(sender);
        SelectionListener.getPos2().remove(sender);
        koTH.setCapZone(new Cuboid(pos1, pos2));
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the capzone of the " + koTH.getName() + " KoTH!"));
    }

    @Subcommand("setcenter")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void setcenter(Player sender, @Name("koth") @Single KoTH koTH) {
        koTH.setCenter(sender.getLocation());
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the default cap seconds of the " + koTH.getName() + " KoTH to your location!"));
    }

    @Subcommand("setcapseconds")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void setcapzone(Player sender, @Name("koth") @Single KoTH koTH, @Name("seconds") int seconds) {
        koTH.setDefaultCapSeconds(seconds);
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the default cap seconds of the " + koTH.getName() + " KoTH to " + seconds + "!"));
    }

    @Subcommand("start")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void start(CommandSender sender, @Name("koth") @Single KoTH koTH) {
        koTH.start();
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully started the " + koTH.getName() + " KoTH!"));
    }

    @Subcommand("stop")
    @CommandPermission("Samurai.command.koth")
    @CommandCompletion("@koths")
    public static void stop(CommandSender sender, @Name("koth") @Single KoTH koTH) {
        koTH.end();
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully started the " + koTH.getName() + " KoTH!"));
    }
}
