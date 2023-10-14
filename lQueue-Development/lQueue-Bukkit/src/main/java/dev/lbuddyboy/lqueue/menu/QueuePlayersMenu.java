package dev.lbuddyboy.lqueue.menu;

import dev.lbuddyboy.lqueue.api.model.DistributionType;
import dev.lbuddyboy.lqueue.api.model.Queue;
import dev.lbuddyboy.lqueue.api.model.QueuePlayer;
import dev.lbuddyboy.lqueue.api.util.TimeUtils;
import dev.lbuddyboy.lqueue.lQueue;
import dev.lbuddyboy.lqueue.packet.QueueRemovePlayerPacket;
import dev.lbuddyboy.lqueue.util.CC;
import dev.lbuddyboy.lqueue.util.ItemBuilder;
import dev.lbuddyboy.lqueue.util.MojangUser;
import dev.lbuddyboy.lqueue.util.menu.Button;
import dev.lbuddyboy.lqueue.util.menu.button.BackButton;
import dev.lbuddyboy.lqueue.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class QueuePlayersMenu extends PagedMenu {

    private Queue queue;

    @Override
    public String getPageTitle(Player player) {
        return "Queue Players";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (QueuePlayer qp : queue.getQueuePlayers()) {
            buttons.add(new QueuePlayerButton(qp));
        }

        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return new ArrayList<Button>(){{
            add(new BackButton(5, new QueuesMenu()));
        }};
    }

    @AllArgsConstructor
    public class QueuePlayerButton extends Button {

        private QueuePlayer qp;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() throws IOException {
            return new ItemBuilder(Material.SKULL_ITEM)
                    .setDisplayName(new MojangUser(qp.getUuid()).getName())
                    .setLore(Arrays.asList(
                            CC.MENU_BAR,
                            "&eStatus&7: &f" + (qp.isOnline() ? "&aOnline" : "&cOffline &7(" + TimeUtils.formatIntoMMSS((int) (qp.getLeftAtExpiry() / 1000)) + ")"),
                            "&ePosition&7: &f" + queue.getPosition(qp) + "/" + queue.getQueuePlayers().size(),
                            "&ePriority&7: &f" + qp.getPriority(),
                            " ",
                            "&7Click to remove from the queue!",
                            CC.MENU_BAR
                    ))
                    .setData(3)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;

            queue.getQueuePlayers().removeIf(p -> p.getUniqueId().equals(qp.getUniqueId()));

            lQueue.getInstance().getPidginHandler().sendPacket(new QueueRemovePlayerPacket(queue.getName(), qp, DistributionType.GLOBAL));
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }

    }

}
