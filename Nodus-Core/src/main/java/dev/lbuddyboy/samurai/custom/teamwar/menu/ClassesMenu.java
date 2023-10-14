package dev.lbuddyboy.samurai.custom.teamwar.menu;

import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarKit;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarPlayer;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class ClassesMenu extends Menu {

    private WarTeam team;
    private WarPlayer warPlayer;

    @Override
    public String getTitle(Player player) {
        return "Class Selection";
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (WarKit kit : WarKit.values()) {
            buttons.add(new ClassButton(++i, kit));
        }

        return buttons;
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class ClassButton extends Button {

        private int slot;
        private WarKit kit;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            if (warPlayer.getKit() == kit) {
                return new ItemBuilder(this.kit.getMaterial()).displayName(CC.GOLD + this.kit.getKitName() + " &a&l(SELECTED)").hideAttributes().enchant(Enchantment.DURABILITY, 1).build();
            }
            return new ItemBuilder(this.kit.getMaterial()).displayName(CC.GOLD + this.kit.getKitName()).build();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            warPlayer.setKit(kit);
        }

        @Override
        public boolean clickUpdate() {
            return true;
        }
    }

}
