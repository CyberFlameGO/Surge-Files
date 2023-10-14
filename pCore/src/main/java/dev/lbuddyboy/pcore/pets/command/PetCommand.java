package dev.lbuddyboy.pcore.pets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.IPet;
import dev.lbuddyboy.pcore.pets.PetRarity;
import dev.lbuddyboy.pcore.pets.egg.EggImpl;
import dev.lbuddyboy.pcore.pets.menu.PetsMenu;
import dev.lbuddyboy.pcore.util.CC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("pet|pets|spets")
@CommandPermission("pcore.command.pet")
public class PetCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        new PetsMenu().openMenu(sender);
    }

    @Subcommand("give")
    @CommandCompletion("@players @pets")
    public void give(CommandSender sender, @Name("target") OnlinePlayer onlinePlayer, @Name("pet") String petName, @Name("level") int level) {
        IPet pet = pCore.getInstance().getPetHandler().getPets().get(petName);
        Player player = onlinePlayer.getPlayer();

        if (pet == null) {
            return;
        }

        player.getInventory().addItem(pet.createPet(level));
    }

    @Subcommand("giveegg")
    @CommandCompletion("@players @eggTypes @petRarities")
    public void give(CommandSender sender, @Name("target") OnlinePlayer onlinePlayer, @Name("eggType") String eggType, @Name("pet") String petRarity, @Name("duration|blocks-walked") int value) {
        PetRarity rarity = pCore.getInstance().getPetHandler().getPetRarity(petRarity);
        EggImpl eggImpl = pCore.getInstance().getPetHandler().getEggImpl(eggType);
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

        player.getInventory().addItem(stack);

    }

    @Subcommand("info")
    public void info(Player sender) {
        ItemStack stack = sender.getItemInHand();
        NBTItem item = new NBTItem(stack);

        for (String key : item.getKeys()) {
            sender.sendMessage(key + ": " + item.getOrDefault(key, "none"));
        }
    }

}
