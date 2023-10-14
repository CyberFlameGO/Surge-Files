package dev.lbuddyboy.samurai.custom.slots.task;

import dev.lbuddyboy.samurai.custom.slots.SlotConstants;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class SpinTask extends BukkitRunnable {

    private final Player player;
    private final ItemBuilder builder;
    private final InventoryClickEvent event;

    private int ticks = 0;

    @Override
    public void run() {

        if (ticks == 4) ticks = 0;
        ticks++;

        if (!player.hasMetadata(SlotConstants.OPENING_META)) {
            cancel();
            builder.name("&a&lFINISHED");
        } else {
            builder.name("&a&lSPINNING" + (ticks == 1 ? "." : ticks == 2 ? ".." : ticks == 3 ? "..." : ""));
            player.playSound(player.getLocation(), Sound.ENTITY_CHICKEN_EGG, 2.0f, 2.0f);
        }

        event.setCurrentItem(builder.build());
    }
}

