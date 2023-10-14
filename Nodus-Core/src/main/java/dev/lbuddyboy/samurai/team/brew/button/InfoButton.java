package dev.lbuddyboy.samurai.team.brew.button;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/08/2021 / 10:21 PM
 * HCTeams / dev.lbuddyboy.samurai.team.brew.button
 */
public class InfoButton extends Button {

    @Override
    public ItemStack getButtonItem(Player player) {
        return new ItemBuilder(Material.BOOK)
                .displayName(CC.translate("&6Information"))
                .lore(CC.translate(Arrays.asList(
                        "",
                        "&7When adding to any of the brewing",
                        "&7materials your team's auto brewing",
                        "&7time will reset and if you have the",
                        "&7required materials for each potion",
                        "&7it will add to the total potions",
                        "&7that your team has brewed.",
                        ""
                )))
                .build();
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {

    }

    @Override
    public String getName(Player player) {
        return null;
    }

    @Override
    public List<String> getDescription(Player player) {
        return null;
    }

    @Override
    public Material getMaterial(Player player) {
        return null;
    }
}
