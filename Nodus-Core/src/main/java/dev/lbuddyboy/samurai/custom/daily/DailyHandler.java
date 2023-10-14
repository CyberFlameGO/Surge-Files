package dev.lbuddyboy.samurai.custom.daily;

import dev.lbuddyboy.flash.util.menu.GUISettings;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.daily.command.DailyCommand;
import dev.lbuddyboy.samurai.persist.maps.statistics.BaseToggleMap;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;

@Getter
public class DailyHandler {

    private final Map<String, DailyReward> rewards;
    private final DailyRewardsMap rewardsMap;
    private Config config;
    private GUISettings guiSettings;

    public DailyHandler() {
        this.rewards = new HashMap<>();
        (this.rewardsMap = new DailyRewardsMap()).loadFromRedis();

        Samurai.getInstance().getPaperCommandManager().getCommandCompletions().registerCompletion("rewards", (c) -> rewards.keySet());
        Samurai.getInstance().getPaperCommandManager().registerCommand(new DailyCommand());
        reload();
    }

    public void reload() {
        this.rewards.clear();
        this.config = new Config(Samurai.getInstance(), "daily-rewards");
        this.guiSettings = new GUISettings(this.config);

        ConfigurationSection section = this.config.getConfigurationSection("daily-rewards");
        for (String key : section.getKeys(false)) {
            int weight = section.getInt(key + ".weight");
            String name = section.getString(key + ".name");
            String displayName = section.getString(key + ".display-name");
            List<String> commands = section.getStringList(key + ".commands");

            this.rewards.put(name, new DailyReward(key, name, displayName, commands, weight));
        }
        List<DailyReward> dailyRewards = new ArrayList<>(this.rewards.values());
        dailyRewards.sort(Comparator.comparingInt(DailyReward::getWeight));

    }

    public DailyReward getLastClaimed(UUID uuid) {
        DailyReward lastClaimed = null;
        for (DailyReward reward : this.rewards.values()) {
            if (lastClaimed == null){
                if (reward.isActive(uuid)) {
                    lastClaimed = reward;
                }
                continue;
            }
            if (lastClaimed.getWeight() < reward.getWeight()) lastClaimed = reward;
        }
        return lastClaimed;
    }

    public DailyReward getNext(UUID uuid) {
        DailyReward reward = getLastClaimed(uuid);
        List<DailyReward> rewards = getRewardsSorted();

        if (reward == null) return rewards.get(0);
        int index = rewards.indexOf(reward);
        if (rewards.size() <= index + 1) return null;

        return rewards.get(rewards.indexOf(reward) + 1);
    }

    public List<DailyReward> getRewardsSorted() {
        List<DailyReward> dailyRewards = new ArrayList<>(this.rewards.values());
        dailyRewards.sort(Comparator.comparingInt(DailyReward::getWeight));
        return dailyRewards;
    }

}
