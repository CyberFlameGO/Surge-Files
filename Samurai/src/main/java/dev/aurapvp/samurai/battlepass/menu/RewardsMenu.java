package dev.aurapvp.samurai.battlepass.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.battlepass.BattlePassReward;
import dev.aurapvp.samurai.battlepass.Tier;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.button.BackButton;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RewardsMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Rewards";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

        for (Tier tier : Samurai.getInstance().getBattlePassHandler().getTiers().values()) {
            buttons.add(new TierButton(pass, tier));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(5, new BattlePassMenu()));

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class TierButton extends Button {

        private BattlePass pass;
        private Tier tier;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            if (pass.getClaimedTiers().contains(this.tier.getName())) {
                return new ItemBuilder(Material.MINECART)
                        .setName("&6&lTier " + this.tier.getNumber() + " &7(&a&lCLAIMED&7)")
                        .create();
            }
            return new ItemBuilder(Material.CHEST_MINECART)
                    .setName("&6&lTier " + this.tier.getNumber())
                    .setLore(this.tier.getDescription(),
                            "%xp%", this.tier.getXpNeeded(),
                            "%player_xp%", this.pass.getExperience())
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.RIGHT || event.getClick() == ClickType.SHIFT_RIGHT) {
                new TierPreviewMenu(this.tier, true).openMenu(player);
                return;
            }

            if (this.pass.getExperience() < this.tier.getXpNeeded()) {
                player.sendMessage(CC.translate("&cYou do not have " + this.tier.getXpNeeded() + " battle pass experience."));
                return;
            }

            if (!pass.getClaimedTiers().contains(this.tier.getName())) {
                if (pass.isPremium(player)) {
                    for (BattlePassReward reward : this.tier.getBoughtRewards()) {
                        reward.execute(player);
                    }
                }
                for (BattlePassReward reward : this.tier.getFreeRewards()) {
                    reward.execute(player);
                }
                if (tier.getNumber() > pass.getTier()) pass.setTier(tier.getNumber());
                pass.getClaimedTiers().add(this.tier.getName());
            }
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
