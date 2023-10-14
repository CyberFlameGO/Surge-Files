package dev.aurapvp.samurai.enchants.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.aurapvp.samurai.enchants.rarity.Rarity;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@CommandAlias("enchantitems")
public class EnchantItemsCommand extends BaseCommand {

    @Subcommand("givebook")
    @CommandPermission("samurai.enchants.admin")
    @CommandCompletion("@players @rarities")
    public void giveBook(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("rarity") String rarityName, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();
        Optional<Rarity> rarity = Samurai.getInstance().getEnchantHandler().getRarity(rarityName);

        if (!rarity.isPresent()) {
            sender.sendMessage(CC.translate("&cPlease provide a valid rarity."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(rarity.get().getOpenItem());
        }
    }

    @Subcommand("givetransmog")
    @CommandPermission("samurai.enchants.admin")
    @CommandCompletion("@players")
    public void giveBook(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(Samurai.getInstance().getEnchantHandler().getTRANSMOG_SCROLL());
        }
    }

    @Subcommand("givewhitescroll")
    @CommandPermission("samurai.enchants.admin")
    @CommandCompletion("@players")
    public void giveWS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(Samurai.getInstance().getEnchantHandler().getWHITE_SCROLL());
        }
    }

    @Subcommand("giveholywhitescroll")
    @CommandPermission("samurai.enchants.admin")
    @CommandCompletion("@players")
    public void giveHWS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(Samurai.getInstance().getEnchantHandler().getHOLY_WHITESCROLL());
        }
    }

    @Subcommand("giveblackscroll")
    @CommandPermission("samurai.enchants.admin")
    @CommandCompletion("@players")
    public void giveBS(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("amount") int amount, @Name("success") @co.aikar.commands.annotation.Optional @Default("-1") int success, @Name("destroy") @co.aikar.commands.annotation.Optional  @Default int destroy) {
        Player player = onlinePlayer.getPlayer();

        if (success <= -1 && destroy <= -1) {
            for (int i = 0; i < amount; i++) {
                player.getInventory().addItem(Samurai.getInstance().getEnchantHandler().getBlackScroll(ThreadLocalRandom.current().nextInt(1, 100), ThreadLocalRandom.current().nextInt(1, 100)));
            }
            return;
        }

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(Samurai.getInstance().getEnchantHandler().getBlackScroll(success <= -1 ? ThreadLocalRandom.current().nextInt(1, 100) : success,
                    destroy <= -1 ? ThreadLocalRandom.current().nextInt(1, 100) : destroy));

        }
    }

}
