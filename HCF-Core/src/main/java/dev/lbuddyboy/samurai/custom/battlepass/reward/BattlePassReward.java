package dev.lbuddyboy.samurai.custom.battlepass.reward;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.custom.battlepass.BattlePassProgress;
import dev.lbuddyboy.samurai.custom.battlepass.tier.Tier;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class BattlePassReward {

    private final UUID uuid = UUID.randomUUID();
    private final Tier tier;
    private List<String> text = new ArrayList<>();
    private List<String> commands = new ArrayList<>();

    public BattlePassReward addText(String... lines) {
        text.addAll(Arrays.asList(lines));
        return this;
    }

    public BattlePassReward addCommand(String command) {
        commands.add(command);
        return this;
    }

    public boolean isFreeReward() {
        return tier.getFreeReward() == this;
    }

    public boolean hasClaimed(BattlePassProgress progress) {
        return (isFreeReward() && progress.getClaimedRewardsFree().contains(tier)) || (!isFreeReward() && progress.getClaimedRewardsPremium().contains(tier));
    }

    public void execute(Player player) {
        for (String command : commands) {
            String processedCommand = command
                    .replace("{playerName}", player.getName())
                    .replace("{playerUuid}", player.getUniqueId().toString())
                    .replace("{tier}", String.valueOf(tier.getNumber()));

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BattlePassReward reward = (BattlePassReward) o;
        return uuid == reward.uuid && tier.getNumber() == reward.tier.getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(tier, text, commands);
    }

}
