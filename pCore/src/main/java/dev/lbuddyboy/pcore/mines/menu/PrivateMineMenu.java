package dev.lbuddyboy.pcore.mines.menu;

import dev.drawethree.xprison.XPrison;
import dev.drawethree.xprison.api.enums.ReceiveCause;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ConversationBuilder;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.TimeUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivateMineMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Your Mine";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        MineUser user = pCore.getInstance().getMineUserHandler().getMineUser(player.getUniqueId());

        if (user.getPrivateMine() != null) {
            buttons.add(new ResetButton(player, user.getPrivateMine()));
            buttons.add(new InfoButton(user.getPrivateMine()));
        }

        buttons.add(new RanksButton());
        buttons.add(new TeleportButton(player));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    public static class RanksButton extends Button {

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.DIAMOND_PICKAXE)
                    .setName("&eMine Ranks")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            new MineRanksMenu().openMenu((Player) event.getWhoClicked());
        }
    }

    @AllArgsConstructor
    public class InfoButton extends Button {

        private PrivateMine mine;

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.SIGN)
                    .setName("&eMine Information")
                    .setLore(Arrays.asList(
                            " ",
                            " &eTaxed Tokens&7: &f" + mine.getTokensTaxed(),
                            " &eTax Percentage&7: &f" + (String.valueOf(mine.getTax()).replaceAll("0.", "")) + "0%",
                            " ",
                            " &7&oLeft click to collect the taxed tokens.",
                            " &7&oRight click to change the tax amount.",
                            " "
                    ))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getClick().isRightClick()) {
                player.beginConversation(new ConversationBuilder(player).closeableStringPrompt(CC.translate("&aType the new percent that should tax. (Ex: 50%)"), (ctx, response) -> {
                    if (!response.contains("%")) {
                        player.sendMessage(CC.translate("&cYou cannot provide this please put a valid percentange, Ex: 50%, 35%, 25%"));
                        return Prompt.END_OF_CONVERSATION;
                    }
                    char char1 = response.charAt(0);
                    char char2 = response.charAt(1);
                    String combined = "0." + char1 + char2;

                    mine.setTax(Double.parseDouble(combined));
                    player.sendMessage(CC.translate("&aYour mine's tax is now " + response));
                    PrivateMineMenu.this.openMenu(player);
                    return Prompt.END_OF_CONVERSATION;
                }).echo(false).build());
                return;
            }

            if (mine.getTokensTaxed() == 0) {
                player.sendMessage(CC.translate("&cThere are no tokens to collect..."));
                return;
            }

            mine.setTokensTaxed(0);
            XPrison.getInstance().getTokens().getApi().addTokens(player, mine.getTokensTaxed(), ReceiveCause.REDEEM);
        }
    }

    @AllArgsConstructor
    public static class TeleportButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.ENDER_PEARL)
                    .setName("&eTeleport")
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            PrivateMine cache = pCore.getInstance().getPrivateMineHandler().fetchCache(player.getUniqueId());

            Location location = cache.getSpawnLocation();

            player.teleport(location);
        }
    }

    @AllArgsConstructor
    public static class ResetButton extends Button {

        private Player player;
        private PrivateMine cache;

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WATCH)
                    .setName("&cMine Reset")
                    .setLore("&7&oYour mine reset in " + TimeUtils.formatIntoDetailedString((int) (this.cache.getNextResetMillis() / 1000)))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;


        }
    }

}
