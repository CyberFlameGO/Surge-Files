package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("sand|sandbiome|desert|desertbiome")
public class SandBiomeCommand extends BaseCommand {

    @Default
    public static void SandBiomeCommand(Player sender) {
        sender.sendMessage(CC.translate("&6&lDesert Information"));
        sender.sendMessage(CC.translate(CC.GOLD + CC.UNICODE_ARROWS_RIGHT + " &fGeneral Location #1&7: &61800, 1800"));
        sender.sendMessage(CC.translate(CC.GOLD + CC.UNICODE_ARROWS_RIGHT + " &cNote: There are more deserts, this is our general location."));
    }

}