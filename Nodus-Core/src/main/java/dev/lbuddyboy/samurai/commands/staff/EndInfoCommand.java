package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.listener.EndListener;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

@CommandAlias("endinfo")
@CommandPermission("foxtrot.default")
public class EndInfoCommand extends BaseCommand {

    @Default
    public static void endinfo(CommandSender sender) {
        Location endExit = EndListener.getEndExit();
        Location creepers = EndListener.getCreepers();
        sender.sendMessage(CC.translate("&5&lEnd Information"));
        if (endExit != null) {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &4End Exit: &f" + endExit.getBlockX() + ", " + endExit.getBlockZ()));
        } else {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &4End Exit: &cNot Set"));
        }
        if (creepers != null) {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &aCreeper Spawners: &f" + creepers.getBlockX() + ", " + creepers.getBlockZ()));
        } else {
            sender.sendMessage(CC.translate("&7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &aCreeper Spawners: &cNot Set"));
        }
    }

}