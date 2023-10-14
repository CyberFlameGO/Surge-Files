package dev.lbuddyboy.samurai.map.game.arena.select;

import dev.lbuddyboy.samurai.util.Cuboid;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

@Data
@AllArgsConstructor
public class Selection {

    public static final ItemStack SELECTION_WAND = ItemBuilder.of(Material.GOLDEN_AXE).name("&aSelection Wand").build();
    public static final String SELECTION_METADATA_KEY = "selection";

    private Location loc1, loc2;

    private Selection() {}

    public Cuboid getCuboid() {
        if (!isComplete()) return null;

        return new Cuboid(loc1, loc2);
    }

    public boolean isComplete() {
        return loc1 != null && loc2 != null;
    }

    public static Selection getOrCreateSelection(Player player) {
        if (player.hasMetadata(SELECTION_METADATA_KEY)) {
            return (Selection) player.getMetadata(SELECTION_METADATA_KEY).get(0).value();
        }

        Selection selection = new Selection();
        player.setMetadata(SELECTION_METADATA_KEY, new FixedMetadataValue(Samurai.getInstance(), selection));

        return selection;
    }

}
