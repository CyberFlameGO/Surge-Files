package dev.aurapvp.samurai.battlepass;

import lombok.Builder;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Builder
public class BattlePassReward {

    private String name;
    private ItemStack displayItem;
    private Tier tier;
    private List<String> commands = new ArrayList<>();

    public BattlePassReward addCommand(String... command) {
        commands.addAll(Arrays.asList(command));
        return this;
    }

    public boolean isFree() {
        return getTier().getFreeRewards().contains(this);
    }

    public boolean hasClaimed(BattlePass pass) {
        return pass.getClaimedTiers().contains(this.name);
    }

    public void execute(Player player) {
        for (String command : commands) {
            String processedCommand = command
                    .replace("%player%", player.getName())
                    .replace("%uuid%", player.getUniqueId().toString())
                    .replace("%tier%", String.valueOf(this.tier));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }

}
