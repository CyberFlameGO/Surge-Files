package dev.lbuddyboy.pcore.coinflip.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.coinflip.CoinFlip;
import dev.lbuddyboy.pcore.coinflip.CoinFlipType;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class WagerMenu extends Menu {

    private final CoinFlip cf;

    @Override
    public String getTitle(Player player) {
        return cf.getEconomy().getPrefix() + "" + pCore.getInstance().getEconomyHandler().getEconomy().formatMoney(cf.getAmount()) + " * " + cf.getSide(player.getUniqueId()).getName();
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        if (cf.getSender().toString().equals(player.getUniqueId().toString())) {
            buttons.add(new CFPlayerButton(1, cf.getSender()));
            buttons.add(new CFButton(2, cf.getSide()));

            buttons.add(new CFButton(8, cf.getChallengerSide()));
            buttons.add(new CFPlayerButton(9, cf.getChallenger()));
        } else {
            buttons.add(new CFPlayerButton(1, cf.getChallenger()));
            buttons.add(new CFButton(2, cf.getChallengerSide()));

            buttons.add(new CFButton(8, cf.getSide()));
            buttons.add(new CFPlayerButton(9, cf.getSender()));
        }

        if (cf.getCountdown() > 0) {
            buttons.add(new CountdownButton(cf));
        } else {
            buttons.add(new RollButton(cf));
        }

        return buttons;
    }

    @AllArgsConstructor
    public static class CountdownButton extends Button {

        private CoinFlip cf;

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.STAINED_GLASS_PANE, Math.max(cf.getCountdown(), 1))
                    .setName("&a&l" + cf.getCountdown() + "...")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .create();
        }
    }

    @AllArgsConstructor
    public static class RollButton extends Button {

        private CoinFlip cf;

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            if (cf.getWinningSide() == CoinFlipType.HEADS) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .setName("&a&lHEADS")
                        .setDurability(3)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .create();
            }
            return new ItemBuilder(Material.RABBIT_FOOT)
                    .setName("&a&lTAILS")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .create();
        }
    }

    @AllArgsConstructor
    public static class CFButton extends Button {

        private int slot;
        private CoinFlipType side;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            if (side == CoinFlipType.HEADS) {
                return new ItemBuilder(Material.SKULL_ITEM)
                        .setName("&a&lHEADS")
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .setDurability(3)
                        .create();
            }
            return new ItemBuilder(Material.RABBIT_FOOT)
                    .setName("&a&lTAILS")
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .create();
        }
    }

    @AllArgsConstructor
    public static class CFPlayerButton extends Button {

        private int slot;
        private UUID player;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            String name = Bukkit.getOfflinePlayer(player).getName();
            return new ItemBuilder(Material.SKULL_ITEM)
                    .setName(CC.DARK_GREEN + name)
                    .setDurability(3)
                    .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                    .setOwner(name)
                    .create();
        }
    }

}
