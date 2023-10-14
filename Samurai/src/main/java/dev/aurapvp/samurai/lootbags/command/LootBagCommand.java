package dev.aurapvp.samurai.lootbags.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.lootbags.LootBag;
import dev.aurapvp.samurai.lootbags.menu.LootBagEditorMenu;
import dev.aurapvp.samurai.lootbags.menu.LootBagRewardsMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Config;
import dev.aurapvp.samurai.util.ItemUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("lootbag")
public class LootBagCommand extends BaseCommand {

    @Subcommand("give")
    @CommandCompletion("@players @lootbags")
    @CommandPermission("samurai.command.lootbag.give")
    public void give(CommandSender sender, @Name("player") OnlinePlayer onlinePlayer, @Name("lootbag") String lootBagName, @Name("amount") int amount) {
        LootBag lootBag = Samurai.getInstance().getLootBagHandler().getLootBags().getOrDefault(lootBagName, null);
        Player player = onlinePlayer.getPlayer();

        if (lootBag == null) {
            sender.sendMessage(CC.translate("&cA lootbag under that name does not exist"));
            return;
        }

        ItemStack item = lootBag.getDisplayItem().clone();

        item.setAmount(amount);

        ItemUtils.tryFit(player, item, false);
        player.sendMessage(CC.translate("&aYou have been given " + amount + "x " + lootBag.getDisplayName() + "&a."));
        sender.sendMessage(CC.translate("&aYou have given " + amount + "x " + lootBag.getDisplayName() + "&a to " + player.getName() + "."));
    }

    @Subcommand("create")
    @CommandPermission("samurai.command.lootbag.create")
    public void create(Player sender, @Name("name") String name) {

        if (sender.getItemInHand() == null || sender.getItemInHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need an item in your hand for the display item."));
            return;
        }

        if (Samurai.getInstance().getLootBagHandler().getLootBags().containsKey(name)) {
            sender.sendMessage(CC.translate("&cA lootbag under that name already exists!"));
            return;
        }

        LootBag lootBag = new LootBag();

        lootBag.setConfig(new Config(Samurai.getInstance(), name, Samurai.getInstance().getLootBagHandler().getLootBagsDirectory()));
        lootBag.setName(name);
        lootBag.setDisplayName(name);
        lootBag.setDisplayItem(sender.getItemInHand());

        lootBag.save();
        Samurai.getInstance().getLootBagHandler().getLootBags().put(name, lootBag);

        sender.sendMessage(CC.translate("&aCreated a new lootbag under the name '" + name + "'."));
    }

    @Subcommand("setdisplayname")
    @CommandPermission("samurai.command.lootbag.setdisplayname")
    @CommandCompletion("@lootbags")
    public void setdisplayname(Player sender, @Name("lootbag") String lootBagName, @Name("name") String name) {
        LootBag lootBag = Samurai.getInstance().getLootBagHandler().getLootBags().getOrDefault(lootBagName, null);

        if (lootBag == null) {
            sender.sendMessage(CC.translate("&cA lootbag under that name does not exist"));
            return;
        }

        lootBag.setDisplayName(name);
        lootBag.save();

        sender.sendMessage(CC.translate("&aSet the display name of the " + lootBagName + " lootbag to '" + name + "'."));
    }

    @Subcommand("setdisplayitem")
    @CommandPermission("samurai.command.lootbag.setdisplayitem")
    @CommandCompletion("@lootbags")
    public void setdisplayitem(Player sender, @Name("lootbag") String lootBagName) {
        LootBag lootBag = Samurai.getInstance().getLootBagHandler().getLootBags().getOrDefault(lootBagName, null);

        if (sender.getItemInHand() == null) {
            sender.sendMessage(CC.translate("&cYou need an item in your hand for the display item."));
            return;
        }

        if (lootBag == null) {
            sender.sendMessage(CC.translate("&cA lootbag under that name does not exist"));
            return;
        }

        lootBag.setDisplayItem(sender.getItemInHand());
        lootBag.save();

        sender.sendMessage(CC.translate("&aUpdated the display item for the " + lootBagName + " lootbag!"));
    }

    @Subcommand("rewards")
    @CommandCompletion("@lootbags")
    public void rewards(Player sender, @Name("lootbag") String lootBagName) {
        LootBag lootBag = Samurai.getInstance().getLootBagHandler().getLootBags().getOrDefault(lootBagName, null);

        if (lootBag == null) {
            sender.sendMessage(CC.translate("&cA lootbag under that name does not exist"));
            return;
        }

        new LootBagRewardsMenu(lootBag).openMenu(sender);
    }

    @Subcommand("editor")
    public void editor(Player sender) {
        new LootBagEditorMenu().openMenu(sender);
    }

}
