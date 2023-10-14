package dev.lbuddyboy.samurai.map.kits.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.map.kits.KitListener;
import dev.lbuddyboy.samurai.map.kits.editor.button.EditKitMenu;
import dev.lbuddyboy.samurai.map.kits.editor.menu.KitsMenu;
import dev.lbuddyboy.samurai.map.kits.editor.setup.KitEditorItemsMenu;
import dev.lbuddyboy.samurai.map.shards.menu.upgrades.ShardShopUpgradesMenu;
import dev.lbuddyboy.samurai.server.SpawnTagHandler;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.map.kits.Kit;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

@CommandAlias("kitadmin")
public class KitAdminCommand extends BaseCommand {

    @Subcommand("create")
    @CommandPermission("foxtrot.admin")
    public static void create(Player sender, @Name("name") @Single String name) {
        if (Samurai.getInstance().getMapHandler().getKitManager().getDefaultKit(name) != null) {
            sender.sendMessage(ChatColor.RED + "That kit already exists.");
            return;
        }

        Kit kit = Samurai.getInstance().getMapHandler().getKitManager().getOrCreateDefaultKit(name);
        kit.update(sender.getInventory());

        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        sender.sendMessage(ChatColor.YELLOW + "The " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " kit has been created from your inventory.");
    }

    @Subcommand("delete")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void delete(Player sender, @Name("kit") DefaultKit kit) {
        Samurai.getInstance().getMapHandler().getKitManager().deleteDefaultKit(kit);
        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been deleted.");
    }

    @Subcommand("menueditor")
    @CommandCompletion("@kits")
    public static void delmenueditorete(Player sender, @Name("kit") DefaultKit kit) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(sender.getLocation())) {
            sender.sendMessage(CC.translate("&cYou cannot edit your kits here..."));
            return;
        }

        Optional<Kit> kitOpt = Optional.ofNullable(Samurai.getInstance().getMapHandler().getKitManager().getUserKit(sender.getUniqueId(), kit));
        Kit resolvedKit;
        if (kitOpt.isPresent()) {
            resolvedKit = kit;
        } else {
            resolvedKit = new Kit(kit);
            Samurai.getInstance().getMapHandler().getKitManager().trackUserKit(sender.getUniqueId(), resolvedKit);
        }

        new EditKitMenu(resolvedKit).openMenu(sender);
    }

    @Subcommand("resetend")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void resetend(CommandSender sender) {
        Samurai.getInstance().getMapHandler().getKitManager().resetEnd();
    }

    @Subcommand("edit")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void execute(Player sender, @Name("kit") DefaultKit kit) {
        kit.update(sender.getInventory());
        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been edited and saved.");
    }

    @Subcommand("apply")
    @CommandCompletion("@kits")
    public static void apply(Player sender, @Name("kit") DefaultKit kit) {
        KitListener.attemptApplyKit(sender, kit);
    }

    @Subcommand("load")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void load(Player sender, @Name("kit") DefaultKit kit) {
        kit.apply(sender);

        sender.sendMessage(ChatColor.YELLOW + "Applied the " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + ".");
    }

    @Subcommand("setdesc")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void execute(Player player, @Name("kit") DefaultKit kit, @Name("description") String description) {
        kit.setDescription(description);
        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set description of " + kit.getName() + "!");
    }

    @Subcommand("editor")
    public static void execute(Player player) {
        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You can only open the Kit Editor while in Spawn!");
            return;
        }

        if (SpawnTagHandler.isTagged(player)) {
            player.sendMessage(ChatColor.RED + "You can't open the Kit Editor while spawn-tagged!");
            return;
        }

        new KitsMenu().openMenu(player);
    }

    @Subcommand("editoritems")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void editorItems(Player player, @Name("kit") DefaultKit kit) {
        new KitEditorItemsMenu(kit).openMenu(player);
    }

    @Subcommand("seticon")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void setIcon(Player player, @Name("kit") DefaultKit kit) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You have no item in your hand!");
            return;
        }

        kit.setIcon(player.getItemInHand());
        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set icon of " + kit.getName() + "!");
    }

    @Subcommand("setweight")
    @CommandCompletion("@kits")
    @CommandPermission("foxtrot.admin")
    public static void setWeight(Player player, @Name("kit") DefaultKit kit, @Name("weight") int order) {
        kit.setOrder(order);
        Samurai.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set order of " + kit.getName() + " to " + order + "!");
    }

    @Subcommand("upgrades")
    @CommandCompletion("@kits")
    public static void upgrades(Player player) {
        new ShardShopUpgradesMenu().openMenu(player);
    }

}
