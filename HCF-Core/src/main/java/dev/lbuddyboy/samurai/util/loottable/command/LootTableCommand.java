package dev.lbuddyboy.samurai.util.loottable.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import de.tr7zw.nbtapi.NBTItem;
import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import dev.lbuddyboy.samurai.util.loottable.editor.menu.EditLootTableMenu;
import dev.lbuddyboy.samurai.util.loottable.editor.menu.LootTablesMenu;
import dev.lbuddyboy.samurai.util.loottable.menu.LootTablePreviewMenu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@CommandAlias("loottable|ltable|lt")
public class LootTableCommand extends BaseCommand {

    @Subcommand("reload")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void reload(CommandSender sender, @Name("loottable") @Optional LootTable crate) {
        if (crate == null) {
            for (LootTable rl : LootTableHandler.getLootTables()) {
                rl.reload();
            }
            return;
        }

        crate.reload();
    }
    
    @Subcommand("preview")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void preview(Player sender, @Name("loottable") LootTable crate) {
        new LootTablePreviewMenu(crate, (Menu) null).openMenu(sender);
    }

    @Subcommand("testroll")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void testroll(Player sender, @Name("loottable") LootTable crate) {
        crate.open(sender);
    }

    @Subcommand("getitems")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void getitems(Player sender, @Name("loottable") LootTable crate) {
        sender.getInventory().setContents(crate.getItems().values()
                .stream()
                .map(item -> {
                    dev.lbuddyboy.samurai.util.ItemBuilder builder = dev.lbuddyboy.samurai.util.ItemBuilder.copyOf(item.getItem())
                            .nbtString("id", item.getId())
                            .nbtDouble("chance", item.getChance());

                    if (!item.getCommands().isEmpty()) {
                        builder.nbtStringList("commands", item.getCommands());
                    }

                    return builder.build();
                })
                .toArray(ItemStack[]::new));
    }

    @Subcommand("setitems")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void setitems(Player sender, @Name("loottable") LootTable crate) {
        crate.getItems().clear();

        for (ItemStack stack : sender.getInventory()) {
            if (stack == null || stack.getType() == Material.AIR || stack.getAmount() <= 0) continue;

            NBTItem item = new NBTItem(stack);
            String id = item.hasTag("id") ? item.getString("id") : String.valueOf(crate.getItems().size());
            double chance = item.hasTag("chance") ? item.getDouble("chance") : 50D;
            List<String> commands = item.hasTag("commands") ? item.getStringList("commands") : new ArrayList<>();

            if (item.hasTag("id")) item.removeKey("id");
            if (item.hasTag("chance")) item.removeKey("chance");
            if (item.hasTag("commands")) item.removeKey("commands");

            crate.getItems().put(
                    id,
                    new LootTableItem(crate, crate.getItems().size() + 1, id, item.getItem(), ItemUtils.getName(stack), chance, commands, !commands.isEmpty())
            );
        }

        crate.save();

    }

    @Subcommand("createitem")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void createItem(Player sender, @Name("loottable") LootTable lootTable, @Name("id") String id, @Name("useHand") @Optional Boolean useHand) {
        if (useHand == null) useHand = true;

        if (useHand) {
            if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
                sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
                return;
            }
        }

        if (lootTable.getItems().size() > 54) {
            sender.sendMessage(CC.translate("&cYou have too many items in this loottable, remove one!"));
            return;
        }

        if (lootTable.getItems().containsKey(id)) {
            sender.sendMessage(CC.translate("&cYou cannot create an item with the same id."));
            return;
        }

        ItemStack stack = new ItemBuilder(Material.GOLD_INGOT).setName("&eDefault Item").create();
        if (useHand)
            stack = sender.getInventory().getItemInMainHand();

        LootTableItem item = new LootTableItem(lootTable, -1, id, stack, ItemUtils.getName(stack), 50.0, new ArrayList<>(), true);
        lootTable.getItems().put(id, item);
        lootTable.save();
        lootTable.reload();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's item has just been created" + (useHand ? " with your hand" : "") + "."));
    }

    @Subcommand("setitem")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void setitem(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item) {
        if (sender.getInventory().getItemInMainHand().getType() == Material.AIR) {
            sender.sendMessage(CC.translate("&cYou need to have an item in your hand."));
            return;
        }

        item.setItem(sender.getInventory().getItemInMainHand());
        item.setAmount(sender.getInventory().getItemInMainHand().getAmount());
        item.setDisplayName(ItemUtils.getName(item.getItem()));
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's item has just been updated to you hand."));
    }

    @Subcommand("editor")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void editor(Player sender, @Name("loottable") @Optional LootTable crate) {
        if (crate == null) {
            new LootTablesMenu().openMenu(sender);
            return;
        }

        new EditLootTableMenu(crate).openMenu(sender);
    }

    @Subcommand("setdisplayname")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void edit(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item, @Name("display-name") String displayName) {

        item.setDisplayName(displayName);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's display name has just been updated to " + displayName + "&a."));
    }

    @Subcommand("togglegiveitem")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void togglegiveitem(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item) {

        item.setGiveItem(!item.isGiveItem());
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's " + item.getId() + " item will " + (item.isGiveItem() ? "now give the item" : "not give the item") + "&a."));
    }

    @Subcommand("command remove")
    @CommandCompletion("@loottables @itemIds @commands")
    @CommandPermission("loottables.admin")
    public static void removecommand(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item, @Name("command") String command) {
        item.getCommands().remove(command);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's " + item.getId() + " item now has the '" + command + "' command removed&a."));
    }

    @Subcommand("command add")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void addcommand(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item, @Name("command") String command) {
        item.getCommands().add(command);
        lootTable.save();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a loottables's " + item.getId() + " item now has the '" + command + "' command&a."));
    }

    @Subcommand("command list")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void list(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item) {
        sender.sendMessage(CC.translate("&a" + item.getId() + "'s Command"));

        for (String command : item.getCommands()) {
            sender.sendMessage(CC.translate("&a- " + command));
        }
    }

    @Subcommand("deleteitem|delitem")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void deleteitem(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item) {
        lootTable.getItems().get(item.getId()).setRemoved(true);
        lootTable.save();
        lootTable.reload();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate's " + item.getId() + " item has been removed from the rewards."));
    }

    @Subcommand("delete")
    @CommandCompletion("@loottables")
    @CommandPermission("loottables.admin")
    public static void delete(Player sender, @Name("loottable") LootTable lootTable) {
        lootTable.delete();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate has been deleted."));
    }

    @Subcommand("setchance")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void chance(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item, @Name("chance") double chance) {
        item.setChance(chance);
        lootTable.save();
        lootTable.reload();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate's chance has been set to " + chance + "."));
    }

    @Subcommand("setslot")
    @CommandCompletion("@loottables @itemIds")
    @CommandPermission("loottables.admin")
    public static void slot(Player sender, @Name("loottable") LootTable lootTable, @Name("config-id") LootTableItem item, @Name("slot") int slot) {
        if (slot <= 0) {
            sender.sendMessage(CC.translate("&cThat slot is too low."));
            return;
        }

        if (slot > 54) {
            sender.sendMessage(CC.translate("&cThat slot is too high."));
            return;
        }

        for (LootTableItem i : lootTable.getItems().values()) {
            if (i.getSlot() == slot) {
                sender.sendMessage(CC.translate("&aAn item already has that slot."));
                return;
            }
        }

        item.setSlot(slot);
        lootTable.save();
        lootTable.reload();

        sender.sendMessage(CC.translate("&aThe " + lootTable.getDisplayName() + "&a crate's slot in the menu has been set to " + slot + "."));
    }

}
