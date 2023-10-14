package dev.lbuddyboy.pcore.battlepass.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.BattlePass;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
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
        BattlePass pass = pCore.getInstance().getBattlePassHandler().loadBattlePass(player.getUniqueId());

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
                                "%experience%", pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(pass.getExperience()),
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
        if (config == null || pCore.getInstance().getBattlePassHandler().getBattlePassYML().isReloaded()) {
            config = pCore.getInstance().getBattlePassHandler().getBattlePassYML().gc();
        }
        return config;
    }

}
