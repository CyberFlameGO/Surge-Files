package dev.aurapvp.samurai.battlepass.challenge.category;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.battlepass.challenge.Challenge;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public class ChallengeCategory {

    private String name, displayName;
    private ItemStack displayItem;
    private boolean active;

    public List<Challenge> getChallenges() {
        return Samurai.getInstance().getBattlePassHandler().getChallenges().stream().filter(challenge -> challenge.getCategory() == this).collect(Collectors.toList());
    }

    public List<Challenge> getCompletedChallenges(Player player) {
        BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId(), true);
        return Samurai.getInstance().getBattlePassHandler().getChallenges().stream()
                .filter(challenge -> challenge.getCategory() == this && pass.isComplete(challenge))
                .collect(Collectors.toList());
    }

}
