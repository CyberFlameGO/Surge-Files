package dev.lbuddyboy.pcore.robots.menu;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.api.enums.ReceiveCause;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.robots.upgrade.RobotUpgrade;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.NumberFormat;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RobotListMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (pRobot robot : pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId())) {
            buttons.add(new RobotButton(robot));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new CollectButton(player));

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getSize();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getButtonSlots();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().isAutoFill();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getNextSlot();
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getRobotHandler().getRobotListMenuSettings().getPreviousSlot();
    }

    @AllArgsConstructor
    private class RobotButton extends Button {

        private pRobot robot;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>();

            for (String s : pCore.getInstance().getRobotHandler().getConfig().getStringList("list-menu.buttons.robot-button.lore")) {
                if (!s.contains("%upgrades%")) {
                    lore.add(s);
                    continue;
                }

                lore.addAll(robot.getFormattedUpgrades());
            }

            return new ItemBuilder(this.robot.getType().getDisplayItem(), 1)
                    .setName(pCore.getInstance().getRobotHandler().getConfig().getString("list-menu.buttons.robot-button.name"),
                            "%robot-name%", robot.getType().getDisplayName()
                    )
                    .setLore(lore,
                            "%robot-name%", robot.getType().getDisplayName()
                    )
                    .create();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (pCore.getInstance().getRobotHandler().getRobot(robot.getId()) == null) {
                return;
            }

            new RobotMenu(robot, RobotListMenu.this).openMenu(player);
        }
    }

    @AllArgsConstructor
    private class CollectButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return pCore.getInstance().getRobotHandler().getConfig().getInt("list-menu.buttons.collect-button.slot");
        }

        @Override
        public ItemStack getItem() {
            long earnings = 0;

            for (pRobot robot : pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId())) {
                earnings += robot.getItemsProduced();
            }

            Material material = CompMaterial.fromString(pCore.getInstance().getRobotHandler().getConfig().getString("list-menu.buttons.collect-button.material")).getMaterial();
            int data = pCore.getInstance().getRobotHandler().getConfig().getInt("list-menu.buttons.collect-button.data");

            return new ItemBuilder(material, 1)
                    .setDurability(data)
                    .setName(pCore.getInstance().getRobotHandler().getConfig().getString("list-menu.buttons.collect-button.name"),
                            "%earnings%", NumberFormat.formatNumber(earnings)
                    )
                    .setLore(pCore.getInstance().getRobotHandler().getConfig().getStringList("list-menu.buttons.collect-button.lore"),
                            "%earnings%", NumberFormat.formatNumber(earnings)
                    )
                    .create();
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            long amount = 0;
            for (pRobot robot : pCore.getInstance().getRobotHandler().getRobots(player.getUniqueId())) {
                amount += robot.getItemsProduced();
                robot.setItemsProduced(0);
            }

            XPrison.getInstance().getTokens().getApi().addTokens(player, amount, ReceiveCause.REDEEM);
            player.sendMessage(CC.translate("&aYou have just collected &6" + EconomyType.COINS.getPrefix() + NumberFormat.formatNumber(amount) + " tokens!"));
        }
    }

}
