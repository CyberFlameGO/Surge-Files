package dev.lbuddyboy.samurai.commands.menu.menu.buttons;

import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.Getter;
import dev.lbuddyboy.samurai.commands.menu.menu.LFFMenu;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
@Getter
public class LFFKitButton extends Button {

    private String kitName;
    private Material kitIcon;
    private LFFMenu parent;

    public LFFKitButton(String kitName, Material kitIcon, LFFMenu parent) {
        this.kitName = kitName;
        this.kitIcon = kitIcon;
        this.parent = parent;
    }
    
    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + kitName;
    }

    
    @Override
    public List<String> getDescription(Player player) {
        return Arrays.asList(ChatColor.WHITE + "Click to choose the", ChatColor.GRAY + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + ChatColor.GOLD + kitName + ChatColor.WHITE + " Class.");
    }

    
    @Override
    public Material getMaterial(Player player) {
        return kitIcon;
    }

    
    @Override
    public ItemStack getButtonItem(Player player) {
        ItemStack itemStack = new ItemBuilder(kitIcon).displayName(getName(player)).lore(getDescription(player)).build();
        if(parent.getSelected().contains(getKitName())) {
            itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        }
        ItemMeta meta = itemStack.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    
    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        boolean selected = parent.getSelected().contains(getKitName());
        if(selected) parent.getSelected().remove(getKitName());
        else parent.getSelected().add(getKitName());

        player.sendMessage(ChatColor.YELLOW + "You have " + (!selected ? ChatColor.GREEN + "now" : ChatColor.RED + "no longer") + ChatColor.YELLOW + " selected the " + ChatColor.LIGHT_PURPLE + kitName + ChatColor.YELLOW + " class.");
    }
}
