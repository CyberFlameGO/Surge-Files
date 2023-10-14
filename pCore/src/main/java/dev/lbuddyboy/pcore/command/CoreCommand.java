package dev.lbuddyboy.pcore.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.lbuddyboy.pcore.SettingsConfiguration;
import dev.lbuddyboy.pcore.command.menu.StackEditorMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.shop.ItemEditorType;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.shop.ShopItem;
import dev.lbuddyboy.pcore.shop.menu.editor.ItemsEditorMenu;
import dev.lbuddyboy.pcore.shop.menu.editor.ShopItemEditorMenu;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Prompt;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;

@CommandAlias("pcore")
@CommandPermission("op")
public class CoreCommand extends BaseCommand {

    @Default
    public void def(CommandSender sender) {

    }

    @Subcommand("edititem")
    public void edititem(Player sender) {
        if (sender.getItemInHand() == null) {
            sender.sendMessage(CC.translate("&cYou need an item in your hand to do this."));
            return;
        }

        new StackEditorMenu(sender.getItemInHand()).openMenu(sender);
    }

    @Subcommand("reload")
    public void reload(Player sender) {
        pCore.getInstance().getModules().forEach(IModule::save);
        pCore.getInstance().getModules().forEach(IModule::reload);

        sender.sendMessage(CC.translate("&aSuccessfully reloaded all handlers."));
    }

    @Subcommand("setspawn")
    public void setspawn(Player sender) {
        SettingsConfiguration.SPAWN_LOCATION.update(LocationUtils.serializeString(sender.getLocation()));
        sender.sendMessage(CC.translate("&aSpawn updated to your current location!"));
    }

    @AllArgsConstructor
    @Getter
    public enum StackEditorType {

        NAME(20, "&aType the new name of this item. Type 'cancel' to stop this process.", new ItemBuilder(Material.BOOKSHELF).setName("&eChange Lore &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setName(response).create());
        }),
        LORE(21, "&aType the description people will see for this. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.SIGN).setName("&eChange Name &7(Click)").create(), (p, stack, response) -> {

        }),
        CHANGE_MATERIAL(22, "&aType the new material you'd like this to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.SIGN).setName("&eChange Name &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            ItemStack itemStack = CompMaterial.fromString(response).toItem(stack.getAmount());

            if (itemStack == null || itemStack.getType() == Material.AIR) {
                p.sendMessage(CC.translate("&cCould not find an item under that name."));
                return;
            }

            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setMaterial(itemStack.getType()).setDurability(itemStack.getDurability()).create());
        }),
        DATA(23, "&aType the data for this item, (Ex: if it's stained glass and you enter 1 it'll be orange stained glass). Type 'cancel' to stop this process.", new ItemBuilder(Material.INK_SACK).setName("&eChange Data &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setDurability(Integer.parseInt(response)).create());
        });

        private final int slot;
        private final String prompt;
        private final ItemStack stack;
        private final Callback.TripleCallback<Player, ItemStack, String> edit;

    }

    @AllArgsConstructor
    @Getter
    public enum SkullEditorType {

        OWNER(23, "&aType the skull you'd like this to be (has to be an offline player). Type 'cancel' to stop this process.", new ItemBuilder(Material.SKULL_ITEM).setDurability(0).setName("&eSkull Owner - Name &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            if (p.getItemInHand().getType() != Material.SKULL_ITEM) {
                p.sendMessage(CC.translate("&cPlease have a skull in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setOwner(response).create());
        }),
        OWNER_UUID(24, "&aType the player uuid you'd like this to be (has to be an offline player uuid). Type 'cancel' to stop this process.", new ItemBuilder(Material.SKULL_ITEM).setDurability(1).setName("&eSkull UUID &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            if (p.getItemInHand().getType() != Material.SKULL_ITEM) {
                p.sendMessage(CC.translate("&cPlease have a skull in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setOwnerUUID(UUID.fromString(response)).create());
        }),
        OWNER_TEXTURE(25, "&aType the base64 texture you'd like this to be (has to be a base64). Type 'cancel' to stop this process.", new ItemBuilder(Material.SKULL_ITEM).setDurability(2).setName("&eSkull Base64 &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            if (p.getItemInHand().getType() != Material.SKULL_ITEM) {
                p.sendMessage(CC.translate("&cPlease have a skull in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setOwnerBase64(response).create());
        }),
        OWNER_LINK(26, "&aType the texture link you'd like this to be (has to be a minecraft https textures link). Type 'cancel' to stop this process.", new ItemBuilder(Material.SKULL_ITEM).setDurability(3).setName("&eSkull Texture Link &7(Click)").create(), (p, stack, response) -> {
            if (p.getItemInHand() == null) {
                p.sendMessage(CC.translate("&cPlease have an item in your hand."));
                return;
            }
            if (p.getItemInHand().getType() != Material.SKULL_ITEM) {
                p.sendMessage(CC.translate("&cPlease have a skull in your hand."));
                return;
            }
            p.setItemInHand(new ItemBuilder(p.getItemInHand()).setOwnerTextureLink(response).create());
        }),;

        private final int slot;
        private final String prompt;
        private final ItemStack stack;
        private final Callback.TripleCallback<Player, ItemStack, String> edit;

    }

}
