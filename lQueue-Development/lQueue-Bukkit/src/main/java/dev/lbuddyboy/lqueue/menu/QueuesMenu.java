package dev.lbuddyboy.lqueue.menu;

import dev.lbuddyboy.flash.handler.SpoofHandler;
import dev.lbuddyboy.lqueue.api.lQueueAPI;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.util.TimeUtils;
import dev.lbuddyboy.lqueue.command.QueueToggleCommand;
import dev.lbuddyboy.lqueue.util.CC;
import dev.lbuddyboy.lqueue.util.ItemBuilder;
import dev.lbuddyboy.lqueue.util.menu.Button;
import dev.lbuddyboy.lqueue.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueuesMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return "Queue Servers";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Queue queue : lQueueAPI.getQueues()) {
            buttons.add(new QueueButton(queue));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class QueueButton extends Button {

        private Queue queue;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setDisplayName("&6&l" + queue.getName())
                    .setData(queue.isOffline() ? 14 : queue.isWhitelisted() ? 4 : 5)
                    .setLore(Arrays.asList(
                            CC.MENU_BAR,
                            "&eStatus&7: &r" + queue.status(),
                            "&ePaused&7: &r" + (queue.isPaused() ? "&aYes" : "&cNo"),
                            "&eQueued&7: &r" + SpoofHandler.getSpoofedCount(queue.getQueuePlayers().size()),
                            "&eOnline&7: &f" + SpoofHandler.getSpoofedCount(queue.getOnlinePlayers()) + "/" + queue.getMaxPlayers(),
                            (queue.isOffline() ?
                                    "&eDowntime&7: &f" + TimeUtils.formatLongIntoHHMMSS((System.currentTimeMillis() - queue.getStoppedAt()) / 1000) :
                                    "&eUptime&7: &f" + TimeUtils.formatLongIntoHHMMSS((System.currentTimeMillis() - queue.getStartedAt()) / 1000)
                            ),
                            "",
                            "&7Left Click to view the queue players!",
                            "&7Right Click to toggle the queue status on/off!",
                            CC.MENU_BAR
                    ))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.RIGHT) {
                new QueueToggleCommand().toggleQueue(player, queue.getName());
                return;
            }

            new QueuePlayersMenu(queue).openMenu(player);
        }
    }

}
