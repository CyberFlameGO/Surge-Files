package dev.lbuddyboy.gkits.menu;

import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:05 PM
 * GKits / me.lbuddyboy.gkits.menu
 */
public class ArmorSetsMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate("&6&lArmor Sets");
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        int i = 0;
        for (ArmorSet set : lGKits.getInstance().getArmorSets()) {

            buttons.add(new SetButton(set, ++i));
        }

        return buttons;
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @AllArgsConstructor
    public class SetButton extends Button {

        private ArmorSet set;
        private int slot;

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public ItemStack getItem() {
            ItemStack itemStack = new ItemStack(set.getDisplayMaterial());
            ItemMeta meta = itemStack.getItemMeta();

            meta.addItemFlags(ItemFlag.values());
            meta.setDisplayName(CC.translate(set.getColor() + "&l" + set.getDisplayName() + " SET"));
            meta.setLore(CC.translate(set.getDescription()));
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            if (event.getClick() == ClickType.RIGHT && player.hasPermission("gkitz.bypass")) {
                set.reward(player);
                for (ArmorSet set : lGKits.getInstance().getArmorSets()) {
                    if (set.hasOn(player)) {
                        if (!set.hasSetOn(player)) set.deactivate(player);
                    }
                    if (!set.hasSetOn(player)) continue;

                    set.activate(player);
                }
                return;
            }
            event.setCancelled(true);
        }
    }

}
