package dev.lbuddyboy.samurai.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.UUID;

@CommandAlias("orestats|ores")
public class OresCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void ores(Player sender, @Name("player") @Optional UUID player) {
        if (player == null) player = sender.getUniqueId();

        if (player.equals(sender.getUniqueId())) {
            sender.sendMessage(CC.translate("&6&lYour Ore Statistics"));
        } else {
            sender.sendMessage(CC.translate("&6&l" + UUIDUtils.name(player) + "'s Ore Statistics"));
        }

        Arrays.asList(
                "Diamonds Mined: " + ChatColor.AQUA + Samurai.getInstance().getDiamondMinedMap().getMined(player),
                "Emeralds Mined: " + ChatColor.GREEN + Samurai.getInstance().getEmeraldMinedMap().getMined(player),
                "Redstone Mined: " + ChatColor.RED + Samurai.getInstance().getRedstoneMinedMap().getMined(player),
                "Gold Mined: " + ChatColor.GOLD + Samurai.getInstance().getGoldMinedMap().getMined(player),
                "Iron Mined: " + ChatColor.GRAY + Samurai.getInstance().getIronMinedMap().getMined(player),
                "Lapis Mined: " + ChatColor.BLUE + Samurai.getInstance().getLapisMinedMap().getMined(player),
                "Coal Mined: " + ChatColor.DARK_GRAY + Samurai.getInstance().getCoalMinedMap().getMined(player)
        ).forEach(s -> sender.sendMessage(CC.translate("&7" + SymbolUtil.UNICODE_ARROW_RIGHT + " &f" + s)));
    }

}