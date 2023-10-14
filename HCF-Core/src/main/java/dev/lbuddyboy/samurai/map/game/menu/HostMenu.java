package dev.lbuddyboy.samurai.map.game.menu;

import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.map.game.Game;
import dev.lbuddyboy.samurai.map.game.GameType;
import dev.lbuddyboy.samurai.util.Formats;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HostMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Host an Event";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        for (GameType gameType : GameType.values()) {
            buttons.put(buttons.size(), new GameTypeButton(gameType));
        }

        return buttons;
    }

    @AllArgsConstructor
    private class GameTypeButton extends Button {
        private final GameType gameType;

        @Override
        public String getName(Player player) {
            return ChatColor.GOLD.toString() + ChatColor.BOLD + gameType.getDisplayName();
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> description = new ArrayList<>();
            description.add("");
            description.addAll(Formats.renderLines(ChatColor.GRAY.toString(), gameType.getDescription()));
            description.add("");

            if (gameType.isDisabled()) {
                description.add(ChatColor.RED.toString() + ChatColor.BOLD + "DISABLED");
                description.add(ChatColor.RED + "This event is currently disabled by an admin!");
            } else if (gameType.canHost(player)) {
                description.add(ChatColor.GREEN + "Click to host this event!");
            } else {
                description.add(ChatColor.RED.toString() + "NO PERMISSION");
                description.add(ChatColor.RED + "Purchase SURGE rank to gain");
                description.add(ChatColor.RED + "access & host this event!");
                description.add("");
                description.add(ChatColor.WHITE + "store.minesurge.org");
            }

            return description;
        }

        @Override
        public Material getMaterial(Player player) {
            return gameType.getIcon().getType();
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            ItemStack stack = super.getButtonItem(player);
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            stack.setItemMeta(meta);
            return stack;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            if (clickType.isLeftClick()) {
                if (Feature.MINIGAME.isDisabled()) {
                    player.sendMessage(ChatColor.RED + "Mini games are currently disabled!");
                    return;
                }
                if (gameType.isDisabled()) {
                    player.sendMessage(ChatColor.RED + "That event is temporarily disabled!");
                    return;
                }
                if (!gameType.canHost(player)) {
                    player.sendMessage(ChatColor.RED + "You don't have permission to host " + gameType.getDisplayName() + " events.");
                    return;
                }

                if (!Samurai.getInstance().getMapHandler().getGameHandler().canStartGame(player, gameType)) {
                    return;
                }

                try {
                    player.closeInventory();

                    Game game = Samurai.getInstance().getMapHandler().getGameHandler().startGame(player, gameType);
                    game.addPlayer(player);
                    game.setGemRequiredToJoin(false);

                    player.sendMessage(ChatColor.GREEN + "Started " + gameType.getDisplayName() + "! You can use /game forcestart to forcefully start the event.");
                } catch (IllegalStateException e) {
                    player.sendMessage(ChatColor.RED.toString() + e.getMessage());
                }
            }
        }
    }

}
