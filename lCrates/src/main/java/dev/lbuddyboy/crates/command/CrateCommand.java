package dev.lbuddyboy.crates.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.crates.lCrates;
import dev.lbuddyboy.crates.menu.CratePreviewMenu;
import dev.lbuddyboy.crates.menu.OpenCrateMenu;
import dev.lbuddyboy.crates.model.Crate;
import dev.lbuddyboy.crates.model.CrateItem;
import dev.lbuddyboy.crates.util.CC;
import dev.lbuddyboy.crates.util.ItemBuilder;
import dev.lbuddyboy.crates.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("crate|cr|crates|lcrates|lcr")
public class CrateCommand extends BaseCommand {

    @Default
    public void def(Player sender) {
        menu(sender);
    }

    @Subcommand("create")
    @CommandPermission("lcrates.admin")
    public static void create(Player sender, @Name("name") String name) {
        if (lCrates.getInstance().getCrates().containsKey(name)) {
            sender.sendMessage(CC.translate("&cThat crate already exists."));
            return;
        }

        Crate crate = new Crate(name, "&e" + name, new ItemBuilder(Material.TRIPWIRE_HOOK).setName("&e" + name + " Key").create());

        crate.save();
        lCrates.getInstance().getCrates().put(name, crate);

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate has been created!"));
        lCrates.getInstance().getApi().registerCrate(crate);
    }

    @Subcommand("reload")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void reload(CommandSender sender, @Name("crate") @Optional Crate crate) {
        if (crate == null) {
            for (Crate rl : lCrates.getInstance().getCrates().values()) {
                rl.reload();
            }
            return;
        }

        crate.reload();
    }

    @Subcommand("menu")
    public static void menu(Player sender) {
        new OpenCrateMenu().openMenu(sender);
    }

    @Subcommand("preview")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void preview(Player sender, @Name("crate") Crate crate) {
        new CratePreviewMenu(crate, false).openMenu(sender);
    }

    @Subcommand("give|givekey|givekeys")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void giveKey(CommandSender sender, @Name("crate") Crate crate, @Name("player") OfflinePlayer player, @Name("amount") int amount) {
        if (lCrates.getInstance().isVirtualKeys()) {
            lCrates.getInstance().getApi().addKeys(player.getUniqueId(), crate, amount);
            return;
        }

        if (!player.isOnline()) {
            sender.sendMessage(CC.translate("&cThat player is not online."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            ItemUtils.tryFit(player.getPlayer(), crate.getOpenKey(), false);
        }
        player.getPlayer().sendMessage(CC.translate("&aYou have received " + "x" + amount + " of the " + crate.getDisplayName() + "&a key!"));
    }

    @Subcommand("take|takekey|takekeys")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void takeKey(CommandSender sender, @Name("crate") Crate crate, @Name("player") OfflinePlayer player, @Name("amount") int amount) {
        if (lCrates.getInstance().isVirtualKeys()) {
            lCrates.getInstance().getApi().removeKeys(player.getUniqueId(), crate, amount);
            return;
        }

        if (!player.isOnline()) {
            sender.sendMessage(CC.translate("&cThat player is not online."));
            return;
        }

        for (int i = 0; i < amount; i++) {
            ItemUtils.removeAmount(player.getPlayer().getInventory(), crate.getOpenKey(), amount);
        }
        player.getPlayer().sendMessage(CC.translate("&a" + "x" + amount + " of the " + crate.getDisplayName() + "&a keys have been taken from you!"));
    }

    @Subcommand("giveall|giveallkey")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void giveAllKey(Player sender, @Name("crate") Crate crate, @Name("amount") int amount) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (int i = 0; i < amount; i++) {
                ItemUtils.tryFit(player, crate.getOpenKey(), false);
            }
            player.sendMessage(CC.translate("&aYou have received " + "x" + amount + " of the " + crate.getDisplayName() + "&a key!"));
        }
    }

    @Subcommand("clearlocs")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void clearLocs(Player sender, @Name("crate") Crate crate) {
        crate.resetLocations();
        crate.save();
    }

    @Subcommand("givecrate")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void giveCrate(Player sender, @Name("crate") Crate crate) {
        sender.getInventory().addItem(crate.getCrate());
    }

    @Subcommand("createitem")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void createItem(Player sender, @Name("crate") Crate crate, @Name("id") String id) {
/*        if (sender.getInventory().getItemInMainHand() == null) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }*/

        if (crate.getCrateItems().size() > 54) {
            sender.sendMessage(CC.translate("&cYou have too many items in this crate, remove one!"));
            return;
        }

        if (crate.getCrateItems().containsKey(id)) {
            sender.sendMessage(CC.translate("&cYou cannot create an item with the same id."));
            return;
        }

        ItemStack stack = sender.getInventory().getItemInMainHand();
        if (stack.getType() == Material.AIR)
            stack = new ItemBuilder(Material.GOLD_INGOT).setName("&eDefault Item").create();

        CrateItem item = new CrateItem(crate, -1, id, stack, 50.0);
        crate.getItems().put(id, item);
        crate.save();
        crate.reload();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's item has just been created with your hand."));
    }

    @Subcommand("setitem")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void edit(Player sender, @Name("crate") Crate crate, @Name("config-id") CrateItem item) {
        if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        item.setItem(sender.getInventory().getItemInMainHand());
        item.setAmount(sender.getInventory().getItemInMainHand().getAmount());
        crate.save();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's item has just been updated to you hand."));
    }

    @Subcommand("command remove")
    @CommandCompletion("@loottables @itemIds @commands")
    @CommandPermission("loottables.admin")
    public static void removecommand(Player sender, @Name("loottable") Crate lootTable, @Name("config-id") CrateItem item, @Name("command") String command) {
        item.getCommands().remove(command);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate's " + item.getId() + " item now has the '" + command + "' command removed&a."));
    }

    @Subcommand("command add")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void addcommand(Player sender, @Name("loottable") Crate lootTable, @Name("config-id") CrateItem item, @Name("command") String command) {
        item.getCommands().add(command);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate's " + item.getId() + " item now has the '" + command + "' command&a."));
    }

    @Subcommand("command list")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void list(Player sender, @Name("loottable") Crate lootTable, @Name("config-id") CrateItem item) {
        sender.sendMessage(CC.translate("&a" + item.getId() + "'s Command"));

        for (String command : item.getCommands()) {
            sender.sendMessage(CC.translate("&a- " + command));
        }
    }

    @Subcommand("setcratematerial")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void setCrateMaterial(Player sender, @Name("crate") Crate crate) {
        if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }
        if (!sender.getInventory().getItemInMainHand().getType().isBlock()) {
            sender.sendMessage(CC.translate("&cYou need to have a block in your hand."));
            return;
        }

        crate.setCrateMaterial(sender.getInventory().getItemInMainHand().getType());
        crate.save();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's item has just been updated to you hand."));
    }

    @Subcommand("setmenuitem")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void edit(Player sender, @Name("crate") Crate crate) {
        if (!crate.isInCrateMenu()) {
            sender.sendMessage(CC.translate("&cThat crate is not currently in the crate menu. Toggle it off with /crate togglemenu if this is a mistake."));
            return;
        }

        if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        crate.getMenuSettings().setDisplayItem(sender.getInventory().getItemInMainHand());
        crate.save();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's item has just been updated to you hand."));
    }

    @Subcommand("deleteitem|delitem")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void remove(Player sender, @Name("crate") Crate crate, @Name("config-id") CrateItem item) {
        crate.getItems().get(item.getId()).setRemoved(true);
        crate.save();
        crate.reload();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's " + item.getId() + " item has been removed from the rewards."));
    }

    @Subcommand("delete")
    @CommandCompletion("@crates")
    @CommandPermission("lcrates.admin")
    public static void delete(Player sender, @Name("crate") Crate crate) {
        crate.delete();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate has been deleted."));
    }

    @Subcommand("setchance")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void chance(Player sender, @Name("crate") Crate crate, @Name("config-id") CrateItem item, @Name("chance") double chance) {
        item.setChance(chance);
        crate.save();
        crate.reload();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's chance has been set to " + chance + "."));
    }

    @Subcommand("setslot")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void slot(Player sender, @Name("crate") Crate crate, @Name("config-id") CrateItem item, @Name("slot") int slot) {
        if (slot <= 0) {
            sender.sendMessage(CC.translate("&cThat slot is too low."));
            return;
        }

        if (slot > 54) {
            sender.sendMessage(CC.translate("&cThat slot is too high."));
            return;
        }

        for (CrateItem i : crate.getCrateItems().values()) {
            if (i.getSlot() == slot) {
                sender.sendMessage(CC.translate("&aAn item already has that slot, swapping them..."));
                i.setSlot(item.getSlot());
            }
        }

        item.setSlot(slot);
        crate.save();
        crate.reload();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate's slot in the menu has been set to " + slot + "."));
    }

    @Subcommand("togglemenu")
    @CommandCompletion("@crates @itemIds")
    @CommandPermission("lcrates.admin")
    public static void togglemenu(CommandSender sender, @Name("crate") Crate crate) {
        crate.setInCrateMenu(!crate.isInCrateMenu());
        crate.save();

        sender.sendMessage(CC.translate("&aThe " + crate.getDisplayName() + "&a crate is now " + (crate.isInCrateMenu() ? "in the menu." : "not in the menu.")));
    }

}
