package dev.lbuddyboy.samurai.map.shards.menu;

import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@AllArgsConstructor
public class GlassButton extends Button {

    private final int glassData;

    @Override
    public ItemStack getButtonItem(Player player) {
        return ItemBuilder.of(Material.LEGACY_STAINED_GLASS_PANE)
                .name(" ")
                .data((short) glassData)
                .build();
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