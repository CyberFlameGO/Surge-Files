package dev.lbuddyboy.pcore.essential.enderchest;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@Data
@AllArgsConstructor
public class EnderChestData {

    private UUID owner;
    private ItemStack[] contents;

}
