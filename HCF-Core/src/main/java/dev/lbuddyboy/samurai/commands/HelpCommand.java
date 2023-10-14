package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.commands.menu.help.MainHelpMenu;
import org.bukkit.entity.Player;

@CommandAlias("help")
public class HelpCommand extends BaseCommand {

    @Default
    public static void help(Player sender) {
        if (true) {
            new MainHelpMenu().openMenu(sender);
            return;
        }
//        String sharp = "Sharpness " + Enchantment.DAMAGE_ALL.getMaxLevel();
//        String prot = "Protection " + Enchantment.PROTECTION_ENVIRONMENTAL.getMaxLevel();
//        String bow = "Power " + Enchantment.ARROW_DAMAGE.getMaxLevel();
//
//        String serverName = Foxtrot.getInstance().getServerHandler().getServerName();
//        String serverWebsite = Foxtrot.getInstance().getServerHandler().getNetworkWebsite();
//
//        sender.sendMessage(new String[] {
//
//                "§8§m-------------------------------------------",
//                "§cHelpful Commands:",
//                "§7/report <player> <reason> §f- Report rule breakers.",
//                "§7/request <message> §f- Request staff assistance.",
//                "§7/settings §f- Customize your gameplay.",
//                "§7/tgc §f- Toggle chat visibility.",
//                "§7/tpm §f- Toggle private messaging.",
//
//                "",
//                "§cUseful Links:",
//                "§7Teamspeak §f- ts.valorhcf.net",
//                "§7Discord §f- valorhcf.net/discord",
//                "§7Store §f- store.valorhcf.net",
//                "§8§m-------------------------------------------",
//
//        });
    }

}
