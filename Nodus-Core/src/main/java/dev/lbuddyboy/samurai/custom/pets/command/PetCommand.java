package dev.lbuddyboy.samurai.custom.pets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.samurai.custom.pets.IPet;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import dev.lbuddyboy.samurai.custom.pets.egg.EggImpl;
import dev.lbuddyboy.samurai.custom.pets.menu.PetsMenu;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("pet|pets|spets")
public class PetCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new PetsMenu().openMenu(sender);
    }

    @Subcommand("give")
    @CommandCompletion("@players @pets")
    @CommandPermission("pcore.command.pet")
    public void give(CommandSender sender, @Name("target") OnlinePlayer onlinePlayer, @Name("pet") String petName, @Name("level") int level) {
        IPet pet = Samurai.getInstance().getPetHandler().getPets().get(petName);
        Player player = onlinePlayer.getPlayer();

        if (pet == null) {
            return;
        }

        player.getInventory().addItem(pet.createPet(level));
    }

    @Subcommand("givecandy")
    @CommandCompletion("@players")
    @CommandPermission("pcore.command.pet")
    public void givecandy(CommandSender sender, @Name("target") OnlinePlayer onlinePlayer, @Name("amount") int amount) {
        Player player = onlinePlayer.getPlayer();

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(Samurai.getInstance().getPetHandler().getPetCandy());
        }
    }

    @Subcommand("giveegg")
    @CommandCompletion("@players @eggTypes @petRarities")
    @CommandPermission("pcore.command.pet")
    public void give(CommandSender sender, @Name("target") OnlinePlayer onlinePlayer, @Name("eggType") String eggType, @Name("pet") String petRarity, @Name("duration|blocks-walked") int value, @Name("amount") int amount) {
        PetRarity rarity = Samurai.getInstance().getPetHandler().getPetRarity(petRarity);
        EggImpl eggImpl = Samurai.getInstance().getPetHandler().getEggImpl(eggType);
        Player player = onlinePlayer.getPlayer();

        if (eggImpl == null) {
            player.sendMessage(CC.translate("&cThat's not a valid egg type."));
            return;
        }

        if (rarity == null) {
            player.sendMessage(CC.translate("&cThat's not a valid rarity."));
            return;
        }

        ItemStack stack = eggImpl.getItem(rarity, value);

        for (int i = 0; i < amount; i++) {
            player.getInventory().addItem(stack);
        }
    }

    @Subcommand("info")
    @CommandPermission("pcore.command.pet")
    public void info(Player sender) {
        ItemStack stack = sender.getItemInHand();
        NBTItem item = new NBTItem(stack);

        for (String key : item.getKeys()) {
            sender.sendMessage(key + ": " + item.getOrDefault(key, "none"));
        }
    }

}
