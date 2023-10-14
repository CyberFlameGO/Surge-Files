package dev.lbuddyboy.pcore.battlepass;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class Tier {

    public Tier(String name) {
        this.name = name;
        this.config = new Config(pCore.getInstance(), name, pCore.getInstance().getBattlePassHandler().getTiersFolder());

        this.load();
    }

    public Tier(Config file) {
        this.name = file.getFileName().replaceAll(".yml", "");
        this.config = file;

        this.load();
    }

    private Config config;
    private String name;
    private int number, xpNeeded;
    private List<String> description;
    private List<BattlePassReward> freeRewards = new ArrayList<>(), boughtRewards = new ArrayList<>();

    public void load() {
        YamlConfiguration config = this.config;
        ConfigurationSection freeRewards = config.getConfigurationSection("free-rewards"), boughtRewards = config.getConfigurationSection("bought-rewards");

        this.number = config.getInt("number");
        this.xpNeeded = config.getInt("xp-needed");
        this.description = config.getStringList("description");
        if (freeRewards != null) {
            for (String key : freeRewards.getKeys(false)) {
                this.freeRewards.add(new BattlePassReward(
                        key,
                        ItemUtils.itemStackFromConfigSect(key + ".display-item", freeRewards),
                        this,
                        freeRewards.getStringList(key + ".commands")
                ));
            }
        }
        if (boughtRewards != null) {
            for (String key : boughtRewards.getKeys(false)) {
                this.boughtRewards.add(new BattlePassReward(
                        key,
                        ItemUtils.itemStackFromConfigSect(key + ".display-item", boughtRewards),
                        this,
                        boughtRewards.getStringList(key + ".commands")
                ));
            }
        }
    }

    public void save() {
        YamlConfiguration config = this.config;

        config.set("number", this.number);
        config.set("xp-needed", this.xpNeeded);
        config.set("description", this.description);

        for (BattlePassReward reward : this.freeRewards) {
            ItemUtils.itemStackToConfigSect(reward.getDisplayItem(), -1, "free-rewards." + reward.getName() + ".display-item", config);
            config.set("free-rewards." + reward.getName() + ".tier", reward.getTier().getNumber());
            config.set("free-rewards." + reward.getName() + ".commands", reward.getCommands());
        }

        for (BattlePassReward reward : this.boughtRewards) {
            ItemUtils.itemStackToConfigSect(reward.getDisplayItem(), -1, "bought-rewards." + reward.getName() + ".display-item", config);
            config.set("bought-rewards." + reward.getName() + ".tier", reward.getTier().getNumber());
            config.set("bought-rewards." + reward.getName() + ".commands", reward.getCommands());
        }

        this.config.save();
    }

}
