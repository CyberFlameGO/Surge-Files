package dev.lbuddyboy.samurai.util.loottable;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Data
public class LootTable {

    private String name, displayName;
    private Config config;
    private File folder;

    private int minimumRewards;
    private int maximumRewards;

    private final Map<String, LootTableItem> items = new HashMap<>();
    private final Map<Integer, ItemStack> fillers = new HashMap<>();

    public LootTable(String name, String displayName, boolean load) {
        this.name = name;
        this.folder = Samurai.getInstance().getDataFolder();
        this.displayName = displayName;
        this.config = new Config(Samurai.getInstance(), name, folder);

        if (load) {
            save();
        } else {
            reload();
        }
    }

    public LootTable(Config config) {
        this.config = config;

        reload();
    }

    public void reload() {
        this.fillers.clear();
        this.items.clear();

        this.name = config.getFileName().replaceAll(".yml", "");
        this.folder = Samurai.getInstance().getDataFolder();
        this.config = new Config(Samurai.getInstance(), this.name, this.folder);
        this.displayName = config.getString("display-name");
        this.minimumRewards = config.getInt("minimum-rewards");
        this.maximumRewards = config.getInt("maximum-rewards");

        if (this.config.contains("filler-items")) {
            for (String s : this.config.getStringList("filler-items")) {
                String[] parts = s.split(";");
                ItemBuilder builder = ItemBuilder.of(Material.getMaterial(parts[0]));
                int slot = Integer.parseInt(parts[1]);
                String name = parts[2];
                boolean glowing = Boolean.parseBoolean(parts[3]);
                boolean hideAll = Boolean.parseBoolean(parts[4]);

                builder.name(name);
                if (glowing) builder.enchant(Enchantment.DURABILITY, 1);
                if (hideAll) builder.flags(ItemFlag.values());

                fillers.put(slot, builder.build());
            }
        }

        ConfigurationSection section = this.config.getConfigurationSection("items");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                ConfigurationSection items = section.getConfigurationSection(key);
                if (items == null) continue;
                ItemStack stack = ItemUtils.itemStackArrayFromBase64(items.getString("item"))[0];

                if (items.contains("amount")) {
                    stack.setAmount(items.getInt("amount"));
                }

                LootTableItem item = new LootTableItem(
                        this,
                        items.getInt("slot"),
                        key,
                        stack,
                        items.getString("display-name"),
                        items.getDouble("chance"),
                        items.getStringList("commands"),
                        items.getBoolean("give-item", true)
                );

                this.items.put(key, item);
            }
        }
    }

    public void save() {
        Config config = this.config;

        config.set("items", null);
        config.set("display-name", this.displayName);

        for (LootTableItem item : this.items.values()) {
            if (item.isRemoved()) continue;

            config.set("items." + item.getId() + ".item", ItemUtils.itemStackArrayToBase64(Collections.singletonList(item.getItem()).toArray(new ItemStack[0])));
            config.set("items." + item.getId() + ".slot", item.getSlot());
            config.set("items." + item.getId() + ".chance", item.getChance());
            config.set("items." + item.getId() + ".amount", item.getAmount());
            config.set("items." + item.getId() + ".display-name", item.getDisplayName());
            config.set("items." + item.getId() + ".give-item", item.isGiveItem());
            config.set("items." + item.getId() + ".commands", item.getCommands());
        }

        config.save();
    }

    public void open(Player player) {
        open(player, this.minimumRewards, this.maximumRewards);
    }

    public List<LootTableItem> open(Player player, int min, int max) {
        int actualRewards = ThreadLocalRandom.current().nextInt(min, max);

        List<LootTableItem> rewards = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            List<LootTableItem> crateRewards = new ArrayList<>(this.items.values());

            Collections.shuffle(crateRewards);

            for (LootTableItem reward : crateRewards) {
                if (reward.getChance() < ThreadLocalRandom.current().nextDouble(100)) continue;

                rewards.add(reward);

                if (rewards.size() >= actualRewards) {
                    break;
                }
            }
            if (rewards.size() >= actualRewards) {
                break;
            }
        }

        rewards.forEach(reward -> {
            if (!reward.isGiveItem()) {
                reward.getCommands().forEach(command -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command
                            .replaceAll("%player%", player.getName())
                    );
                });
                return;
            }
            ItemUtils.tryFit(player, reward.getItem(), false);
        });

        return rewards;
    }

    public void delete() {
        this.fillers.clear();
        this.items.clear();
        if (this.config.getFile().exists()) this.config.getFile().delete();

        LootTableHandler.getLootTables().remove(this);
    }

}
