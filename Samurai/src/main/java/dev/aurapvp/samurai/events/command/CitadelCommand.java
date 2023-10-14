package dev.aurapvp.samurai.events.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.events.koth.KoTH;
import dev.aurapvp.samurai.listener.SelectionListener;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Cuboid;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("citadel")
public class CitadelCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("samurai.command.citadel")
    public static void create(CommandSender sender, @Name("name") @Single String name) {
        if (Samurai.getInstance().getEventHandler().getEvents().containsKey(name)) {
            sender.sendMessage(CC.translate("&cThere's already an event under that name."));
            return;
        }

        KoTH koTH = new KoTH(name);

        sender.sendMessage(CC.translate("&aSuccessfully created the " + name + " Citadel."));
        Samurai.getInstance().getEventHandler().getEvents().put(name, koTH);
        koTH.save();
    }

    @Subcommand("delete")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void delete(CommandSender sender, @Name("name") @Single KoTH koTH) {
        if (koTH.isActive()) {
            koTH.end();
        }
        koTH.delete();

        sender.sendMessage(CC.translate("&aSuccessfully deleted the " + koTH.getName() + " Citadel."));
    }

    @Subcommand("capzonewand")
    @CommandPermission("samurai.command.citadel")
    public static void capzonewand(Player sender) {
        sender.getInventory().addItem(SelectionListener.CLAIM_WAND);
    }

    @Subcommand("setcapzone")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void setcapzone(Player sender, @Name("koth") KoTH koTH) {
        Location pos1 = SelectionListener.getPos1().get(sender);
        Location pos2 = SelectionListener.getPos2().get(sender);

        if (pos1 == null) {
            sender.sendMessage(CC.translate("&cPlease select a first position by doing /citadel capzonewand and left clicking a block."));
            return;
        }

        if (pos2 == null) {
            sender.sendMessage(CC.translate("&cPlease select a first position by doing /citadel capzonewand and right clicking a block."));
            return;
        }

        SelectionListener.getPos1().remove(sender);
        SelectionListener.getPos2().remove(sender);
        koTH.setCapZone(new Cuboid(pos1, pos2));
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the capzone of the " + koTH.getName() + " Citadel!"));
    }

    @Subcommand("setcenter")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void setcenter(Player sender, @Name("koth") @Single KoTH koTH) {
        koTH.setCenter(sender.getLocation());
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the default cap seconds of the " + koTH.getName() + " Citadel to your location!"));
    }

    @Subcommand("setcapseconds")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void setcapzone(Player sender, @Name("koth") @Single KoTH koTH, @Name("seconds") int seconds) {
        koTH.setDefaultCapSeconds(seconds);
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully set the default cap seconds of the " + koTH.getName() + " Citadel to " + seconds + "!"));
    }

    @Subcommand("start")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void start(CommandSender sender, @Name("koth") @Single KoTH koTH) {
        koTH.start();
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully started the " + koTH.getName() + " Citadel!"));
    }

    @Subcommand("stop")
    @CommandPermission("samurai.command.citadel")
    @CommandCompletion("@citadels")
    public static void stop(CommandSender sender, @Name("koth") @Single KoTH koTH) {
        koTH.end();
        koTH.save();
        sender.sendMessage(CC.translate("&aSuccessfully started the " + koTH.getName() + " Citadel!"));
    }

}
