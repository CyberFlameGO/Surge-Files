package dev.aurapvp.samurai.battlepass.menu;

import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.ItemUtils;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BattlePassMenu extends Menu {

    private static FileConfiguration config;

    @Override
    public String getTitle(Player player) {
        return CC.translate(getConfig().getString("main-menu.title"));
    }

    @Override
    public boolean autoFill() {
        return getConfig().getBoolean("main-menu.auto-fill.enabled");
    }

    @Override
    public ItemStack autoFillItem() {
        return ItemUtils.itemStackFromConfigSect("display-item", getConfig().getConfigurationSection("main-menu.auto-fill"));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        BattlePass pass = Samurai.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

        buttons.add(new RewardsButton());
        buttons.add(new ChallengesButton());
        buttons.add(new Button() {
            @Override
            public int getSlot() {
                return getConfig().getInt("main-menu.buttons.progress.display-item.slot");
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(ItemUtils.itemStackFromConfigSect("progress.display-item", getConfig().getConfigurationSection("main-menu.buttons")))
                        .formatLore(
                                "%experience%", Samurai.getInstance().getEconomyHandler().getEconomy().formatMoney(pass.getExperience()),
                                "%tier%", pass.getTier(),
                                "%pass-type%", (pass.isPremium(player) ? "Premium" : "Free")
                                )
                        .create();
            }
        });

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return getConfig().getInt("main-menu.size");
    }

    public class RewardsButton extends Button {

        @Override
        public int getSlot() {
            return getConfig().getInt("main-menu.buttons.tiers.display-item.slot");
        }

        @Override
        public ItemStack getItem() {
            return ItemUtils.itemStackFromConfigSect("tiers.display-item", getConfig().getConfigurationSection("main-menu.buttons"));
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            Player player = (Player) event.getWhoClicked();
            new RewardsMenu().openMenu(player);
        }
    }

    public class ChallengesButton extends Button {

        @Override
        public int getSlot() {
            return getConfig().getInt("main-menu.buttons.challenges.display-item.slot");
        }

        @Override
        public ItemStack getItem() {
            return ItemUtils.itemStackFromConfigSect("challenges.display-item", getConfig().getConfigurationSection("main-menu.buttons"));
        }
        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            Player player = (Player) event.getWhoClicked();
            new ChallengeSelectionMenu().openMenu(player);
        }
    }

    public FileConfiguration getConfig() {
        if (config == null || Samurai.getInstance().getBattlePassHandler().getBattlePassYML().isReloaded()) {
            config = Samurai.getInstance().getBattlePassHandler().getBattlePassYML().gc();
        }
        return config;
    }

}
