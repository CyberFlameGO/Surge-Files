package dev.lbuddyboy.pcore.command.menu;

import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.ItemBuilder;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
    public class ChangeColorMenu extends PagedMenu {

        private ItemStack stack;
        private Color selected;

        @Override
        public String getPageTitle(Player player) {
            return "Select Color";
        }

        @Override
        public List<Button> getPageButtons(Player player) {
            List<Button> buttons = new ArrayList<>();

            for (Color color : CC.dyeColors.values()) {
                buttons.add(new ColorButton(color));
            }

            return buttons;
        }

        @Override
        public List<Button> getGlobalButtons(Player player) {
            List<Button> buttons = new ArrayList<>();

            buttons.add(new ConfirmButton());

            return buttons;
        }

        public class ConfirmButton extends Button {

            @Override
            public int getSlot() {
                return 5;
            }

            @Override
            public ItemStack getItem() {
                return new ItemBuilder(Material.WOOL).setDurability(4).setName("&a&lCONFIRM NEW COLOR").create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                if (!(event.getWhoClicked() instanceof Player)) return;
                Player player = (Player) event.getWhoClicked();
                if (selected == null) {
                    player.sendMessage(CC.translate("&cYou do not have a color selected."));
                    return;
                }

                LeatherArmorMeta im = (LeatherArmorMeta) stack.getItemMeta();
                im.setColor(selected);
                stack.setItemMeta(im);
            }
        }

        @AllArgsConstructor
        public class ColorButton extends Button {

            private Color color;

            @Override
            public int getSlot() {
                return 5;
            }

            @Override
            public ItemStack getItem() {
                ItemBuilder builder = new ItemBuilder(Material.LEATHER_CHESTPLATE).setLeatherArmorColor(this.color).setName(CC.dyeColorsByColor.get(this.color));

                if (selected != null && selected == this.color) builder.addEnchant(Enchantment.DURABILITY, 10);

                return builder.create();
            }

            @Override
            public void action(InventoryClickEvent event) throws Exception {
                if (!(event.getWhoClicked() instanceof Player)) return;
                Player player = (Player) event.getWhoClicked();

                selected = this.color;
            }

            @Override
            public boolean clickUpdate() {
                return true;
            }
        }

    }