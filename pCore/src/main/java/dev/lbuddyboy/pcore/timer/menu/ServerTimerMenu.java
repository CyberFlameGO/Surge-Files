package dev.lbuddyboy.pcore.timer.menu;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.timer.menu.sub.ServerTimerTasksMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.TimeUtils;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ServerTimerMenu extends PagedMenu {
    @Override
    public String getPageTitle(Player player) {
        return "Server Timers (Editor)";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ServerTimer timer : pCore.getInstance().getTimerHandler().getServerTimers().values()) {
            buttons.add(new ServerTimerButton(timer));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class ServerTimerButton extends Button {

        private ServerTimer timer;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WATCH)
                    .setName(CC.GOLD + this.timer.getName())
                    .setLore(
                            CC.GRAY + CC.UNICODE_ARROW_RIGHT + " &eTime Left &7&o" + TimeUtils.formatLongIntoHHMMSS((int) this.timer.getExpiry() / 1000),
                            CC.GRAY + CC.UNICODE_ARROW_RIGHT + " &eDisplay Name " + this.timer.getDisplayName(),
                            CC.GRAY + CC.UNICODE_ARROW_RIGHT + " &eContext " + this.timer.getContext(),
                            " ",
                            "&7Click to view all the tasks of this timer."
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            new ServerTimerTasksMenu(timer).openMenu(player);
        }
    }

}
