package dev.lbuddyboy.samurai.commands.menu.menu.buttons;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.commands.LFFCommand;
import dev.lbuddyboy.samurai.commands.menu.menu.LFFMenu;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class LFFCompleteButton extends Button {

    private LFFMenu lffMenu;


    @Override
    public String getName(Player player) {
        return ChatColor.GREEN + "Click to Broadcast";
    }


    @Override
    public List<String> getDescription(Player player) {
        List<String> lore = new ArrayList<>(Collections.singletonList(""));
        if(lffMenu.getSelected().isEmpty()) lore.add(ChatColor.RED + "You don't have a class selected!");
        else {
            lore.add(ChatColor.GOLD + "Selected Classes:");
            lffMenu.getSelected().forEach(lffKitButton -> lore.add(ChatColor.GRAY.toString() + ChatColor.BOLD + " " + SymbolUtil.UNICODE_ARROW_RIGHT + " " + ChatColor.WHITE + lffKitButton));
        }
        return lore;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.EMERALD;
    }


    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        if(lffMenu.getSelected().isEmpty()) {
            player.sendMessage(ChatColor.RED + "You don't have a class selected!");
            return;
        }

        player.closeInventory();
        LFFCommand.cooldown.applyCooldown(player, 30);

        Bukkit.getOnlinePlayers().forEach(aPlayer -> {
            aPlayer.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 35));
            aPlayer.sendMessage(CC.translate(player.getDisplayName() + ChatColor.WHITE + " is " + "&glooking for a faction" + ChatColor.WHITE + "!"));
            aPlayer.sendMessage(CC.translate(" &7" + SymbolUtil.UNICODE_ARROW_RIGHT + " " + "&gClasses&7: &f" + StringUtils.join(lffMenu.getSelected(),  ", ")));
            aPlayer.sendMessage(ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("⎯", 35));
        });


    }
}
