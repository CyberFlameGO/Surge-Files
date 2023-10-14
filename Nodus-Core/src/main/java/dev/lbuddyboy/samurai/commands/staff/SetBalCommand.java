package dev.lbuddyboy.samurai.commands.staff;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.economy.FrozenEconomyHandler;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("setbal")
@CommandPermission("foxtrot.setbal")
public class SetBalCommand extends BaseCommand {

    @Default
    @CommandCompletion("@players")
    public static void setBal(CommandSender sender, @Name("player") UUID player, @Name("amount") float amount) {
        if (amount > 10000 && sender instanceof Player && !sender.isOp()) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        if (Float.isNaN(amount)) {
            sender.sendMessage("§cWhy are you trying to do that?");
            return;
        }


        if (amount > 250000 && sender instanceof Player) {
            sender.sendMessage("§cYou cannot set a balance this high. This action has been logged.");
            return;
        }

        Player targetPlayer = Samurai.getInstance().getServer().getPlayer(player);
        FrozenEconomyHandler.deposit(player, amount);

        if (sender != targetPlayer) {
            sender.sendMessage("§fBalance for §6" + player + "§f set to §6$" + amount);
        }

        if (sender instanceof Player && (targetPlayer != null)) {
            String targetDisplayName = ((Player) sender).getDisplayName();
            targetPlayer.sendMessage("§fYour balance has been set to §6$" + amount + "§f by §6" + targetDisplayName);
        } else if (targetPlayer != null) {
            targetPlayer.sendMessage("§fYour balance has been set to §6$" + amount + "§f by §4CONSOLE§f.");
        }
    }

}