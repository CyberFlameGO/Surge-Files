package dev.lbuddyboy.samurai.custom.slots;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.slots.task.TicketRollTask;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class SlotConstants {

    public final static String OPENED_META = "OPENED_SLOTS", OPENING_META = "OPENING_SLOTS", LOOT_META = "SLOTS_LOOT";
    public final static ItemStack ROLL_BUTTON = ItemBuilder.of(Material.LEVER).name(CC.translate("&a&lSPIN &7(Click)")).build();
    public final static ItemStack NONE = ItemBuilder.of(Material.BARRIER).name(CC.translate("&cNone")).setLore(CC.translate(Collections.singletonList("&7Click a slot ticket to roll!"))).build();

    public static int countTickets(Inventory inventory) {
        return (int) Arrays.stream(inventory.getContents()).filter(Objects::nonNull).filter(i -> i.isSimilar(Samurai.getInstance().getSlotHandler().getItem())).count();
    }
    public static void spin(Inventory inventory, Player player) {
        player.setMetadata(SlotConstants.OPENING_META, new FixedMetadataValue(Samurai.getInstance(), true));

        Samurai.getInstance().getSlotHandler().getTasks().put(player.getUniqueId(), new TicketRollTask(player, inventory, countTickets(inventory)).runTaskTimer(Samurai.getInstance(), 2, 2));
    }
}
