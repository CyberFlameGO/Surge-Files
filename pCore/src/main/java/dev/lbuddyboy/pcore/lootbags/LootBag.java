package dev.lbuddyboy.pcore.lootbags;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.RewardItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LootBag {

    private Config config;
    private String name, displayName;
    private ItemStack displayItem;
    private List<RewardItem> rewards = new ArrayList<RewardItem>() {{
        add(RewardItem.DEFAULT_ITEM);
    }};
    private int amountPerRoll = 3;

    public LootBag(Config config) {
        this.config = config;
        this.name = config.getFileName().replaceAll(".yml", "");
        this.displayName = config.getString("display-name");
        this.amountPerRoll = config.getInt("amountPerRoll");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
        this.rewards = new ArrayList<RewardItem>() {{
            for (String idStr : config.getConfigurationSection("rewards").getKeys(false)) {
                int id = Integer.parseInt(idStr);

                add(new RewardItem(config, "rewards", id));
            }
        }};
    }

    public void save() {
        config.set("display-name", this.displayName);
        config.set("amountPerRoll", this.amountPerRoll);
        ItemUtils.itemStackToConfigSect(this.displayItem, -1, "display-item", this.config);
        rewards.forEach(reward -> reward.save(config, "rewards"));

        config.save();
    }

    public void roll(Player player) {
        int completed = 0;

        while (completed < this.amountPerRoll) {
            RewardItem item = this.rewards.get(ThreadLocalRandom.current().nextInt(0, this.rewards.size()));

            if (item.getChance() < ThreadLocalRandom.current().nextDouble(100.0)) continue;

            item.reward(player);
            completed++;
        }
    }

    public void rename(String name) {
        this.config.getFile().renameTo(new File(this.config.getFile().getParent(), name + ".yml"));
        this.config = new Config(pCore.getInstance(), name + ".yml", pCore.getInstance().getLootBagHandler().getLootBagsDirectory());
        this.name = name;
    }

}
