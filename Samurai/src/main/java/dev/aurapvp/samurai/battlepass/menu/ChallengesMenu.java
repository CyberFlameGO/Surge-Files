package dev.aurapvp.samurai.battlepass.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.battlepass.challenge.Challenge;
import dev.aurapvp.samurai.battlepass.challenge.category.ChallengeCategory;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
public class ChallengesMenu extends PagedMenu {

    private ChallengeCategory category;

    @Override
    public String getPageTitle(Player player) {
        return this.category.getDisplayName();
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Challenge challenge : this.category.getChallenges()) {
            buttons.add(new ChallengeButton(player, challenge));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return Collections.singletonList(new BackButton(5, new ChallengeSelectionMenu()));
    }

    @AllArgsConstructor
    public class ChallengeButton extends Button {

        private Player player;
        private Challenge challenge;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId(), true);
            return new ItemBuilder(this.challenge.getDisplayItem())
                    .formatLore(
                            "%xp%", this.challenge.getXp(),
                            "%progress%", pass.isComplete(challenge) ? "&a&lCOMPLETED" : pass.getProgress(this.challenge)
                    )
                    .create();
        }
    }

}
