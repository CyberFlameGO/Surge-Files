package dev.aurapvp.samurai.essential.kit.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.aurapvp.samurai.essential.kit.Kit;
import dev.aurapvp.samurai.essential.kit.KitHandler;
import dev.aurapvp.samurai.essential.kit.menu.KitEditorMenu;
import dev.aurapvp.samurai.essential.kit.menu.KitMainMenu;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.Config;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

@CommandAlias("kit|gkit|kits")
public class KitCommand extends BaseCommand {

    private final KitHandler kitHandler;

    public KitCommand() {
        this.kitHandler = Samurai.getInstance().getKitHandler();
    }

    @Default
    @CommandCompletion("@kits")
    public void kit(CommandSender sender, @Name("kit") @Optional String kit) {
        if (!(sender instanceof Player)) {
            // help msg
            return;
        }

        if (kit != null) {
            return;
        }

        new KitMainMenu(null).openMenu((Player) sender);
    }

    @Subcommand("editor")
    @CommandPermission("samurai.kit.editor")
    public void editor(Player sender) {
        new KitMainMenu((kit) -> new KitEditorMenu(kit).openMenu(sender)).openMenu(sender);
    }

    @Subcommand("create")
    @CommandPermission("samurai.kit.create")
    public void create(Player sender, @Name("name") String name) {
        Kit kit = this.kitHandler.getKits().get(name);

        if (kit != null) {
            sender.sendMessage(CC.translate("&cA kit with that name already exists."));
            return;
        }

        kit = new Kit();
        kit.setFile(new Config(Samurai.getInstance(), name, Samurai.getInstance().getKitHandler().getKitsDirectory()));
        kit.setName(name);
        kit.setDisplayName(name);
        kit.setData(new MaterialData(Material.DIRT));
        kit.setSlot(1);
        kit.setAutoEquip(true);
        kit.setFakeItems(new ItemStack[0]);
        kit.setArmor(new ItemStack[4]);
        kit.setContents(new ItemStack[36]);
        kit.setCategory(Samurai.getInstance().getKitHandler().getCategories().values().stream().findFirst().get());
        kit.save();

        Samurai.getInstance().getKitHandler().getKits().put(name, kit);
        new KitEditorMenu(kit).openMenu(sender);
    }

    @Subcommand("delete")
    @CommandPermission("samurai.kit.delete")
    public void delete(Player sender, @Name("name") String name) {
        Kit kit = this.kitHandler.getKits().get(name);

        if (kit == null) {
            sender.sendMessage(CC.translate("&cA kit with that name does not exist."));
            return;
        }

        if (kit.getFile().getFile().exists()) kit.getFile().getFile().delete();
        Samurai.getInstance().getKitHandler().getKits().remove(kit.getName());
    }

}
