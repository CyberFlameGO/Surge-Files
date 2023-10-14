package dev.lbuddyboy.samurai.custom.slots.task;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.slots.SlotConstants;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTableItem;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TicketRollTask extends BukkitRunnable {

    private final Player player;
    private final Inventory inventory;
    private final int tickets;

    private final List<LootTableItem> rewards = new ArrayList<>();
    private int ticks = 30;

    @Override
    public void run() {

        List<LootTableItem> items = new ArrayList<>(Samurai.getInstance().getSlotHandler().getLootTable().getItems().values());

        if (!player.isOnline()) {
            cancel();
            player.removeMetadata(SlotConstants.OPENING_META, Samurai.getInstance());
            player.getInventory().addItem(ItemBuilder.copyOf(Samurai.getInstance().getSlotHandler().getItem()).amount(tickets).build());
            return;
        }

        if (ticks <= 0) {
            List<Integer> whitelisted = new ArrayList<>(Collections.singleton(Samurai.getInstance().getSlotHandler().getConfig().getInt("slot-tickets.start-roll-slot") - 1));
            for (int i = 0; i < tickets; i++) {
                whitelisted.add(Samurai.getInstance().getSlotHandler().getRoll_slots().get(i) - 1);
            }
            for (int i = 0; i < inventory.getSize(); i++) {
                if (whitelisted.contains(i)) continue;

                inventory.setItem(i, null);
            }

            cancel();
            player.removeMetadata(SlotConstants.OPENING_META, Samurai.getInstance());
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2.0f, 2.0f);

            rewards.forEach(reward -> {
                if (!reward.isGiveItem()) {
                    reward.getCommands().forEach(command -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                                .replaceAll("%player%", player.getName())
                        );
                    });
                    System.out.println("should've done a command");
                    return;
                }
                ItemUtils.tryFit(player, reward.getItem(), false);
            });

            Bukkit.broadcastMessage(CC.translate(Samurai.getInstance().getSlotHandler().getConfig().getString("slot-tickets.reward-message")
                    .replaceAll("%rewards%", StringUtils.join(rewards.stream().map(LootTableItem::getDisplayName).collect(Collectors.toList()), "&7, "))
                    .replaceAll("%player%", player.getName())));

            Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
                if (player.getOpenInventory().getTopInventory() == inventory) {
                    player.closeInventory();
                }
                Samurai.getInstance().getSlotHandler().getTasks().remove(player.getUniqueId());
            }, 20 * 3);
            return;
        }

        rewards.clear();
        ticks--;

        for (int i = 0; i < tickets; i++) {
            boolean finished = false;
            while (!finished) {
                Collections.shuffle(items);
                for (LootTableItem reward : items) {
                    if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                    inventory.setItem(Samurai.getInstance().getSlotHandler().getRoll_slots().get(i) - 10, reward.getItem());
                    break;
                }
                Collections.shuffle(items);
                for (LootTableItem reward : items) {
                    if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                    inventory.setItem(Samurai.getInstance().getSlotHandler().getRoll_slots().get(i) + 8, reward.getItem());
                    break;
                }
                Collections.shuffle(items);
                for (LootTableItem reward : items) {
                    if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                    inventory.setItem(Samurai.getInstance().getSlotHandler().getRoll_slots().get(i) - 1, reward.getItem());
                    rewards.add(reward);
                    finished = true;
                    break;
                }
            }
        }
    }
}
