package dev.lbuddyboy.samurai.server.timer.menu;

import dev.lbuddyboy.flash.util.bukkit.CC;
import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.server.timer.ServerTimer;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.server.timer.menu.sub.ServerTimerTasksMenu;
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

        for (ServerTimer timer : Samurai.getInstance().getTimerHandler().getServerTimers().values()) {
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
            return new ItemBuilder(Material.CLOCK)
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
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            new ServerTimerTasksMenu(timer).openMenu(player);
        }
    }

}
