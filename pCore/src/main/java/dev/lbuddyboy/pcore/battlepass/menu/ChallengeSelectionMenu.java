package dev.lbuddyboy.pcore.battlepass.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.challenge.Challenge;
import dev.lbuddyboy.pcore.battlepass.challenge.category.ChallengeCategory;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeSelectionMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Challenge Categories";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ChallengeCategory category : pCore.getInstance().getBattlePassHandler().getChallengeCategories()) {
            buttons.add(new CategoryButton(player, category));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return Collections.singletonList(new BackButton(5, new BattlePassMenu()));
    }

    @AllArgsConstructor
    public class CategoryButton extends Button {

        private Player player;
        private ChallengeCategory category;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            AtomicInteger possibleXp = new AtomicInteger();

            this.category.getChallenges().stream().map(Challenge::getXp).forEach(possibleXp::addAndGet);

            return new ItemBuilder(this.category.getDisplayItem())
                    .formatLore(
                            "%" + this.category.getName() + "-challenges%", this.category.getChallenges().size(),
                            "%" + this.category.getName() + "-progress%", this.category.getCompletedChallenges(this.player).size(),
                            "%" + this.category.getName() + "-possible-xp%", possibleXp.get()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!this.category.isActive()) {
                player.sendMessage(CC.translate("&cThis week's challenges aren't out yet!"));
                return;
            }
            new ChallengesMenu(category).openMenu(this.player);
        }
    }

}
