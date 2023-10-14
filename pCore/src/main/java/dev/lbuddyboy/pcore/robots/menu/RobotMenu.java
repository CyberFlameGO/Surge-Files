package dev.lbuddyboy.pcore.robots.menu;

import de.tr7zw.nbtapi.NBTItem;
import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.api.enums.ReceiveCause;
import dev.lbuddyboy.pcore.economy.EconomyType;
import dev.lbuddyboy.pcore.essential.locator.ItemLocation;
import dev.lbuddyboy.pcore.essential.locator.LocationType;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.util.*;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.impl.ConfirmMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
public class RobotMenu extends Menu {

    private pRobot robot;
    private Menu fromMenu;

    @Override
    public String getTitle(Player player) {
        return CC.translate(pCore.getInstance().getRobotHandler().getMainMenuSettings().getTitle());
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new UpgradesButton());
        buttons.add(new PickUpButton());
        buttons.add(new InfoButton());

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getRobotHandler().getMainMenuSettings().getSize();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getRobotHandler().getMainMenuSettings().getAutoFillItem();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getRobotHandler().getMainMenuSettings().isAutoFill();
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (player.hasMetadata("opening_other")) return;
        if (fromMenu == null) return;

        Tasks.run(() -> fromMenu.openMenu(player));
    }

    private class InfoButton extends Button {

        @Override
        public int getSlot() {
            return pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.information").getInt("slot");
        }

        @Override
        public ItemStack getItem() {
            ConfigurationSection section = pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.information");
            ItemBuilder builder = new ItemBuilder(ItemUtils.itemStackFromConfigSect("", section));
            List<String> lore = new ArrayList<>();

            for (String s : section.getStringList("lore")) {
                if (!s.contains("%upgrades%")) {
                    lore.add(s);
                    continue;
                }

                lore.addAll(robot.getFormattedUpgrades());
            }

            return builder
                    .setLore(lore
                            , "%next-produce%", TimeUtils.formatIntoDetailedString((int) (robot.getNextProduceMillis() / 1000))
                            , "%produced%", robot.getItemsProduced()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            XPrison.getInstance().getTokens().getApi().addTokens(player, robot.getItemsProduced(), ReceiveCause.REDEEM);
            player.sendMessage(CC.translate("&aYou have just collected &6" + EconomyType.COINS.getPrefix() + NumberFormat.formatNumber(robot.getItemsProduced()) + " tokens!"));
            robot.setItemsProduced(0);
        }
    }

    private class UpgradesButton extends Button {

        @Override
        public int getSlot() {
            return pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.upgrades").getInt("slot");
        }

        @Override
        public ItemStack getItem() {
            ConfigurationSection section = pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.upgrades");
            ItemBuilder builder = new ItemBuilder(ItemUtils.itemStackFromConfigSect("", section));
            List<String> lore = new ArrayList<>();

            for (String s : section.getStringList("lore")) {
                if (!s.contains("%upgrades%")) {
                    lore.add(s);
                    continue;
                }

                lore.addAll(robot.getFormattedUpgrades());
            }

            return builder
                    .setLore(lore
                            , "%next-produce%", TimeUtils.formatIntoDetailedString((int) (robot.getNextProduceMillis() / 1000))
                            , "%produced%", robot.getItemsProduced()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            new RobotUpgradeMenu(robot, fromMenu).openMenu((Player) event.getWhoClicked());
        }
    }

    private class PickUpButton extends Button {

        @Override
        public int getSlot() {
            return pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.pick-up").getInt("slot");
        }

        @Override
        public ItemStack getItem() {
            ConfigurationSection section = pCore.getInstance().getRobotHandler().getConfig().getConfigurationSection("robot-menu.buttons.pick-up");
            ItemBuilder builder = new ItemBuilder(ItemUtils.itemStackFromConfigSect("", section));
            List<String> lore = new ArrayList<>();

            for (String s : section.getStringList("lore")) {
                if (!s.contains("%upgrades%")) {
                    lore.add(s);
                    continue;
                }

                lore.addAll(robot.getFormattedUpgrades());
            }

            return builder
                    .setLore(lore
                            , "%next-produce%", TimeUtils.formatIntoDetailedString((int) (robot.getNextProduceMillis() / 1000))
                            , "%produced%", robot.getItemsProduced()
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            NBTItem item = robot.getType().getItem();

            boolean fits = ItemUtils.tryFitBool(player, item.getItem(), false);

            if (fits) {
                pCore.getInstance().getLocatorHandler().updateCache(item.getUUID("id"), new ItemLocation(null, player.getUniqueId(), LocationType.GROUND_ITEM));
            }

            robot.delete();
            player.closeInventory();
        }
    }

}
