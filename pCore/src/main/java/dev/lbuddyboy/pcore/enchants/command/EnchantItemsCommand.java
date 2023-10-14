package dev.lbuddyboy.pcore.enchants.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.enchants.rarity.Rarity;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("enchantitems")
public class EnchantItemsCommand extends BaseCommand {

    @Subcommand("givebook")
    @CommandPermission("pcore.enchants.admin")
    @CommandCompletion("@players @rarities")
    public void giveBook(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("rarity") String rarityName, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();
        Optional<Rarity> rarity = pCore.getInstance().getEnchantHandler().getRarity(rarityName);

        if (!rarity.isPresent()) {
            sender.sendMessage(CC.translate("&cPlease provide a valid rarity."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(rarity.get().getOpenItem());
        }
    }

    @Subcommand("givetransmog")
    @CommandPermission("pcore.enchants.admin")
    @CommandCompletion("@players")
    public void giveBook(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(pCore.getInstance().getEnchantHandler().getTRANSMOG_SCROLL());
        }
    }

    @Subcommand("givewhitescroll")
    @CommandPermission("pcore.enchants.admin")
    @CommandCompletion("@players")
    public void giveWS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(pCore.getInstance().getEnchantHandler().getWHITE_SCROLL());
        }
    }

    @Subcommand("giveholywhitescroll")
    @CommandPermission("pcore.enchants.admin")
    @CommandCompletion("@players")
    public void giveHWS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(pCore.getInstance().getEnchantHandler().getHOLY_WHITESCROLL());
        }
    }

    @Subcommand("giveblackscroll")
    @CommandPermission("pcore.enchants.admin")
    @CommandCompletion("@players")
    public void giveBS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount, @Name("success") @co.aikar.commands.annotation.Optional @Default("-1") int success, @Name("destroy") @co.aikar.commands.annotation.Optional  @Default int destroy) {
        Player player = onlinePlayer.getPlayer();

        if (success <= -1 && destroy <= -1) {
            for (int i = 0; i < amount; i++) {
                player.getInventory().addItem(pCore.getInstance().getEnchantHandler().getBlackScroll(ThreadLocalRandom.current().nextInt(1, 100), ThreadLocalRandom.current().nextInt(1, 100)));
            }
            return;
        }

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(pCore.getInstance().getEnchantHandler().getBlackScroll(success <= -1 ? ThreadLocalRandom.current().nextInt(1, 100) : success,
                    destroy <= -1 ? ThreadLocalRandom.current().nextInt(1, 100) : destroy));

        }
    }

}
