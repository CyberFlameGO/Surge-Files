package dev.lbuddyboy.pcore.essential.kit.editor;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.essential.kit.menu.KitCategorySelectorMenu;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

@AllArgsConstructor
@Getter
public enum EditorType {

    ARMOR_CONTENTS(20, "&aType 'confirm' to set the armor equipped as the kit armor contents. Type 'cancel' to stop this process.", new ItemBuilder(Material.DIAMOND_SWORD).setName("&5Armor Contents &7(Click)").create(), (p, kit, value) -> {
        if (!value.equalsIgnoreCase("confirm")) return;

        kit.setArmor(p.getInventory().getArmorContents());
    }),
    CONTENTS(21, "&aType 'confirm' to set the contents in your inventory as the kit contents. Type 'cancel' to stop this process.", new ItemBuilder(Material.CHEST).setName("&6Inventory Contents &7(Click)").create(), (p, kit, value) -> {
        if (!value.equalsIgnoreCase("confirm")) return;

        kit.setContents(p.getInventory().getContents());
    }),
    DISPLAY_ITEM(22, "&aType 'confirm' to set the item in your hand as the display material. Type 'cancel' to stop this process.", new ItemBuilder(Material.BEACON).setName("&eChange Display Material &7(Click)").create(), (p, kit, value) -> {
        if (!value.equalsIgnoreCase("confirm")) return;
        if (p.getItemInHand() == null) {
            p.sendMessage(CC.translate("&cYou do not have an item in your hand."));
            return;
        }

        kit.setData(p.getItemInHand().getData());
    }),
    AUTO_EQUIP(23, "&aType the description normal people with permission will see. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.DIAMOND_CHESTPLATE).setName("&dAuto Equip &7(Click)").create(), (p, kit, value) -> {
        try {
            kit.setAutoEquip(Boolean.parseBoolean(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide true/false."));
        }
    }),
    RENAME(24, "&aType what you would like the new name of this kit to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.NAME_TAG).setName("&dChange Name &7(Click)").create(), (p, kit, value) -> {
        kit.getFile().getFile().renameTo(new File(pCore.getInstance().getKitHandler().getKitsDirectory(), value + ".yml"));
        kit.setFile(new Config(pCore.getInstance(), value, pCore.getInstance().getKitHandler().getKitsDirectory()));
        kit.setName(value);
    }),
    COOLDOWN(25, "&aType what you would like the new cooldown to be (Ex: 1h, 2h). Type 'cancel' to stop this process.", new ItemBuilder(Material.WATCH).setName("&6Change Cooldown &7(Click)").create(), (p, kit, value) -> {
        kit.setCooldown(TimeUtils.parseTime(value) * 1000L);
    }),
    GLOWING(26, "&aType true if you want the display item to glow and false if not. Type 'cancel' to stop this process.", new ItemBuilder(Material.ENCHANTMENT_TABLE).setName("&dGlowing &7(Click)").create(), (p, kit, value) -> {
        try {
            kit.setGlowing(Boolean.parseBoolean(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide true/false."));
        }
    }),
    SLOT(29, "&aType the slot in the GUI you want this to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.IRON_INGOT).setName("&aSlot &7(Click)").create(), (p, kit, value) -> {
        try {
            kit.setSlot(Integer.parseInt(value));
        } catch (Exception ignored) {
            p.sendMessage(CC.translate("&cProcess failed... You need to provide an integer."));
        }
    }),
    CHANGE_DISPLAY(30, "&aType what you would like the new display name of this kit to be. Type 'cancel' to stop this process.", new ItemBuilder(Material.SIGN).setName("&dChange Display &7(Click)").create(), (p, kit, value) -> kit.setDisplayName(value)),
    DESCRIPTION_NORMAL(31, "&aType the description normal people with permission will see. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.BOOK).setName("&aNormal Description &7(Click)").setLore("&c&lRECOMMENDATION", "&cEdit in the config.yml", "&cthen /kit reload!").create(), (p, kit, value) -> {
        if (!value.contains("|")) {
            kit.setDescription(Collections.singletonList(value));
            return;
        }
        kit.setDescription(Arrays.asList(value.split("\\|")));
    }),
    DESCRIPTION_ON_COOLDOWN(32, "&aType the description people that are on cooldown will see. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.COMPASS).setName("&eOn Cooldown Description &7(Click)").setLore("&c&lRECOMMENDATION", "&cEdit in the config.yml", "&cthen /kit reload!").create(), (p, kit, value) -> {
        if (!value.contains("|")) {
            kit.setDescriptionOnCooldown(Collections.singletonList(value));
            return;
        }
        kit.setDescriptionOnCooldown(Arrays.asList(value.split("\\|")));
    }),
    DESCRIPTION_NO_PERM(33, "&aType the description normal people without permission will see. Use | to make multiple lines. Type 'cancel' to stop this process.", new ItemBuilder(Material.REDSTONE).setName("&cNo Permission Description &7(Click)").setLore("&c&lRECOMMENDATION", "&cEdit in the config.yml", "&cthen /kit reload!").create(), (p, kit, value) -> {
        if (!value.contains("|")) {
            kit.setDescriptionNoPermission(Collections.singletonList(value));
            return;
        }
        kit.setDescriptionNoPermission(Arrays.asList(value.split("\\|")));
    }),
    CATEGORY(34, "", new ItemBuilder(Material.PAINTING).setName("&d&lCATEGORY SELECTOR &7(Click)").create(), (p, kit, value) -> {
        new KitCategorySelectorMenu(kit).openMenu(p);
    }),
    DELETE(35, "&aType 'confirm' to delete this kit. Type 'cancel' to stop this process.", new ItemBuilder(Material.BARRIER).setName("&4&lDELETE &7(Click)").create(), (p, kit, value) -> {
        if (kit.getFile().getFile().exists()) kit.getFile().getFile().delete();
        pCore.getInstance().getKitHandler().getKits().remove(kit.getName());
        Tasks.delayed(p::closeInventory);
    });

    private final int slot;
    private final String prompt;
    private final ItemStack stack;
    private final Callback.TripleCallback<Player, Kit, String> edit;

}
