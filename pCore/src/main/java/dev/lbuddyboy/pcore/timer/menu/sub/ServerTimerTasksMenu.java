package dev.lbuddyboy.pcore.timer.menu.sub;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.timer.ScheduledTask;
import dev.lbuddyboy.pcore.timer.ServerTimer;
import dev.lbuddyboy.pcore.timer.menu.ServerTimerMenu;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ServerTimerTasksMenu extends PagedMenu {

    private ServerTimer timer;

    @Override
    public String getPageTitle(Player player) {
        return timer.getName() + "'s Tasks";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (ScheduledTask task : timer.getTasks()) {
            buttons.add(new TaskButton(task));
        }

        return buttons;
    }

    @Override
    public List<Button> getGlobalButtons(Player player) {
        return new ArrayList<Button>(){{
            add(new TaskCreateButton());
            add(new BackButton(6, new ServerTimerMenu()));
        }};
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    public class TaskCreateButton extends Button {

        @Override
        public int getSlot() {
            return 4;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.WOOL)
                    .setName("&a&lCREATE A NEW TASK &7(CLICK)")
                    .setDurability(5)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            timer.getTasks().add(ScheduledTask.DEFAULT_TASK());
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

    @AllArgsConstructor
    public class TaskButton extends Button {

        private ScheduledTask task;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(task.getTask() == ScheduledTask.Task.COMMAND ? Material.COMMAND : Material.PAPER)
                    .setName("&a" + task.getTask().name())
                    .setLore(
                            CC.UNICODE_ARROW_RIGHT + " &eValue &7&o" + ChatColor.stripColor(task.getValue()),
                            CC.UNICODE_ARROW_RIGHT + " &eExecutes with &7&o" + (task.getTimeLeft() / 1000) + "s left.",
                            " ",
                            "&7Left click to edit the task type",
                            "&7Middle click to edit when the task executes",
                            "&7Right click to edit the value"
                    )
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (event.getClick() == ClickType.MIDDLE) {
                player.closeInventory();
                player.setMetadata("server_timer_editor_time", new FixedMetadataValue(pCore.getInstance(), task));
                player.setMetadata("server_timer_editor", new FixedMetadataValue(pCore.getInstance(), timer));
                player.sendMessage(CC.translate("&aPlease select a new task execute time you would like this to be. (seconds) Type 'cancel' to stop this process."));
                return;
            }

            if (event.getClick() == ClickType.RIGHT) {
                player.closeInventory();
                player.setMetadata("server_timer_editor_value", new FixedMetadataValue(pCore.getInstance(), task));
                player.setMetadata("server_timer_editor", new FixedMetadataValue(pCore.getInstance(), timer));
                player.sendMessage(CC.translate("&aPlease select a new task value you would like this to be. (If using command type do not use a slash!) Type 'cancel' to stop this process."));
                return;
            }

            player.closeInventory();
            player.setMetadata("server_timer_editor_action", new FixedMetadataValue(pCore.getInstance(), task));
            player.setMetadata("server_timer_editor", new FixedMetadataValue(pCore.getInstance(), timer));
            player.sendMessage(CC.translate("&aPlease select a new task type you would like this to be. (COMMAND or BROADCAST) Type 'cancel' to stop this process."));
        }

    }

}
