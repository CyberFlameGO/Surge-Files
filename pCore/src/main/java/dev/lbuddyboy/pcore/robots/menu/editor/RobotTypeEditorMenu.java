package dev.lbuddyboy.pcore.robots.menu.editor;

import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RobotTypeEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Robot Type Editor";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();



        return buttons;
    }

    @AllArgsConstructor
    public class RobotTypeButton extends Button {

        private int slot;
        private pRobot robot;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(robot.getType().getDisplayItem())
                    .setLore(CC.translate("&7Click to edit this robot."))
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();


        }
    }

}
