package dev.lbuddyboy.samurai.map.kits.editor.menu;

import com.google.common.collect.ImmutableList;
import dev.lbuddyboy.samurai.map.kits.editor.button.EditKitMenu;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.map.kits.Kit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
final class KitEditButton extends Button {

    private final Optional<Kit> kitOpt;
    private final DefaultKit originalKit;

    @Override
    public String getName(Player player) {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Load/Edit";
    }

    @Override
    public List<String> getDescription(Player player) {
        return ImmutableList.of(
            "",
            ChatColor.GRAY + "Click to " + ChatColor.YELLOW + "edit" + ChatColor.GRAY + " this kit!"
        );
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.BOOK;
    }

    @Override
    public void clicked(Player player, int slot, ClickType clickType) {
        Kit resolvedKit;
        if (kitOpt.isPresent()) {
            resolvedKit = kitOpt.get();
        } else {
            resolvedKit = new Kit(originalKit);
            Samurai.getInstance().getMapHandler().getKitManager().trackUserKit(player.getUniqueId(), resolvedKit);
        }

        new EditKitMenu(resolvedKit).openMenu(player);
    }

}