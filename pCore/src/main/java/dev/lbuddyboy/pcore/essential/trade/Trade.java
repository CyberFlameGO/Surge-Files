package dev.lbuddyboy.pcore.essential.trade;

import dev.lbuddyboy.pcore.essential.trade.inventory.TradeInventory;
import dev.lbuddyboy.pcore.essential.trade.task.TradeTask;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class Trade {

    private final UUID sender, target;
    private final Map<UUID, List<ItemStack>> items = new HashMap<>();
    private final Map<UUID, Inventory> inventories = new HashMap<>();
    private final List<UUID> accepted = new ArrayList<>();
    @Setter private int countdown = 5;
    @Setter private BukkitTask task;
    @Setter private boolean over;

    public Trade(UUID sender, UUID target) {
        this.sender = sender;
        this.target = target;
    }

    public void addItem(Player player, ItemStack stack) {
        List<ItemStack> stacks = items.getOrDefault(player.getUniqueId(), new ArrayList<>());

        stacks.add(stack);
        items.put(player.getUniqueId(), stacks);
    }

    public void removeItem(Player player, ItemStack stack) {
        List<ItemStack> stacks = items.getOrDefault(player.getUniqueId(), new ArrayList<>());

        stacks.remove(stack);
        items.put(player.getUniqueId(), stacks);
        player.getInventory().addItem(stack);
    }

    public void cancel() {
        this.accepted.clear();
        this.countdown = 5;
        this.task.cancel();
        this.task = null;
        TradeInventory.updateInventory(this);
    }

    public void accept(UUID uuid) {
        this.accepted.add(uuid);
        TradeInventory.updateInventory(this);
        if (this.accepted.size() >= 2) {
            commenceTrade();
        }
    }

    public void commenceTrade() {
        this.task = new TradeTask(this).runTaskTimer(pCore.getInstance(), 20, 20);
    }

    public void onClose() {
        if (this.task == null) {
            end();
            return;
        }
    }

    public void start() {
        Player sender = Bukkit.getPlayer(this.sender);
        Player target = Bukkit.getPlayer(this.target);

        TradeInventory.createInventory(sender, this);
        TradeInventory.createInventory(target, this);
        pCore.getInstance().getTradeHandler().getTrades().add(this);
    }

    public void end() {
        Player sender = Bukkit.getPlayer(this.sender);
        Player target = Bukkit.getPlayer(this.target);

        for (ItemStack stack : items.getOrDefault(other(sender), new ArrayList<>())) {
            ItemUtils.tryFit(sender, stack, false);
        }

        for (ItemStack stack : items.getOrDefault(other(target), new ArrayList<>())) {
            ItemUtils.tryFit(target, stack, false);
        }

        setOver(true);

        sender.closeInventory();
        target.closeInventory();
        pCore.getInstance().getTradeHandler().getTrades().remove(this);
    }

    public void refund() {
        Player sender = Bukkit.getPlayer(this.sender);
        Player target = Bukkit.getPlayer(this.target);

        for (ItemStack stack : items.getOrDefault(sender.getUniqueId(), new ArrayList<>())) {
            ItemUtils.tryFit(sender, stack, false);
        }

        for (ItemStack stack : items.getOrDefault(target.getUniqueId(), new ArrayList<>())) {
            ItemUtils.tryFit(target, stack, false);
        }

        setOver(true);

        sender.closeInventory();
        target.closeInventory();
        pCore.getInstance().getTradeHandler().getTrades().remove(this);
    }

    public UUID other(Player player) {
        return this.target == player.getUniqueId() ? this.sender : this.target;
    }

}
