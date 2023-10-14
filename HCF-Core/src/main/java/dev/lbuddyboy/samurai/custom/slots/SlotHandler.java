package dev.lbuddyboy.samurai.custom.slots;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.slots.command.SlotCommand;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.object.Config;
import dev.lbuddyboy.samurai.custom.slots.listener.SlotListener;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

@Getter
public class SlotHandler {

    private ItemStack item;
    private List<Integer> roll_slots;
    private Config config;
    private LootTable lootTable;
    private Map<UUID, BukkitTask> tasks;

    public SlotHandler() {
        reload();
        this.tasks = new HashMap<>();
        this.lootTable = new LootTable(this.config);
        roll_slots = this.config.getIntegerList("slot-tickets.roll-slots");
        LootTableHandler.getLootTables().add(this.lootTable);

        Samurai.getInstance().getPaperCommandManager().registerCommand(new SlotCommand());
        Samurai.getInstance().getServer().getPluginManager().registerEvents(new SlotListener(), Samurai.getInstance());
    }

    public void reload() {
        this.config = new Config(Samurai.getInstance(), "slot-bot");
        this.item = ItemUtils.itemStackFromConfigSect("slot-tickets.item", this.config);
    }

    public boolean isRolling(UUID uuid) {
        return this.tasks.containsKey(uuid);
    }

}
