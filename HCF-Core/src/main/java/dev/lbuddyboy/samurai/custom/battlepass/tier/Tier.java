package dev.lbuddyboy.samurai.custom.battlepass.tier;

import dev.lbuddyboy.samurai.custom.battlepass.reward.BattlePassReward;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;

@RequiredArgsConstructor
@Getter
public class Tier {

    private final int number;
    private final int requiredExperience;
    private BattlePassReward premiumReward;
    private BattlePassReward freeReward;

    public Tier newReward(boolean premium, Consumer<BattlePassReward> consumer) {
        if (premium) {
            premiumReward = new BattlePassReward(this);
            consumer.accept(premiumReward);
        } else {
            freeReward = new BattlePassReward(this);
            consumer.accept(freeReward);
        }
        return this;
    }

}
