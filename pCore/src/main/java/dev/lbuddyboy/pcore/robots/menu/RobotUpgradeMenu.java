package dev.lbuddyboy.pcore.robots.menu;

import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.pets.menu.PetsMenu;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.NumberFormat;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Getter
public class RobotUpgradeMenu extends PagedMenu {

    private pRobot robot;
    private Menu fromMenu;

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Map.Entry<RobotUpgrade, Integer> entry : this.robot.getUpgrades().entrySet()) {
            buttons.add(new UpgradeButton(entry.getKey()));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new BackButton(pCore.getInstance().getRobotHandler().getConfig().getInt("upgrade-menu.back-button.slot"), new RobotMenu(robot, fromMenu)));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getRobotHandler().getUpgradeMenuSettings().getPreviousSlot();
    }

    @AllArgsConstructor
    private class UpgradeButton extends Button {

        private RobotUpgrade upgrade;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>();

            for (String s : pCore.getInstance().getRobotHandler().getConfig().getStringList("upgrade-menu.buttons.upgrade-button.lore")) {
                if (!s.contains("%description%")) {
                    lore.add(s);
                    continue;
                }
                lore.addAll(upgrade.getDescription());
            }

            double cost = robot.getNextUpgradeCost(this.upgrade);

            return new ItemBuilder(this.upgrade.getMaterial().toItem(), 1)
                    .setName(pCore.getInstance().getRobotHandler().getConfig().getString("upgrade-menu.buttons.upgrade-button.name"),
                            "%upgrade-name%", upgrade.getDisplayName(),
                            "%upgrade-level%", robot.getUpgradeLevel(upgrade),
                            "%upgrade-cost%", robot.getUpgradeLevel(this.upgrade) == this.upgrade.getMaxLevel() ? "&a&lMAXED" : NumberFormat.formatNumber(cost),
                            "%upgrade-max%", upgrade.getMaxLevel()
                    )
                    .setLore(lore,
                            "%upgrade-name%", upgrade.getDisplayName(),
                            "%upgrade-level%", robot.getUpgradeLevel(upgrade),
                            "%upgrade-cost%", robot.getUpgradeLevel(this.upgrade) == this.upgrade.getMaxLevel() ? "&a&lMAXED" : NumberFormat.formatNumber(cost),
                            "%upgrade-max%", upgrade.getMaxLevel()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (!EconomyType.COINS.hasAmount(player.getUniqueId(), robot.getNextUpgradeCost(this.upgrade))) {
                player.sendMessage(CC.translate("&cInsufficient tokens..."));
                player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                return;
            }

            int futureLevel = robot.getUpgradeLevel(this.upgrade) + 1;
            if (futureLevel > upgrade.getMaxLevel()) {
                player.sendMessage(CC.translate("&cThis robot upgrade is already maxed."));
                return;
            }

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            player.sendMessage(CC.translate("&aYour '" + upgrade.getId() + "' robot upgrade is now level " + futureLevel + "."));
            robot.getUpgrades().put(this.upgrade, futureLevel);
            robot.save();
        }
    }

}
