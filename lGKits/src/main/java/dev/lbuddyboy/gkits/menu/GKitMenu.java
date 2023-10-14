package dev.lbuddyboy.gkits.menu;

import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.TimeUtils;
import dev.lbuddyboy.gkits.util.menu.Button;
import dev.lbuddyboy.gkits.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.gkits.object.User;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
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
public class GKitMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.translate(lGKits.getInstance().getConfig().getString("inventory.title"));
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (GKit gKit : lGKits.getInstance().getGKits().values()) {
            buttons.add(new KitButton(player, gKit));
        }

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return lGKits.getInstance().getConfig().getInt("inventory.size");
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public boolean autoFill() {
        return lGKits.getInstance().getConfig().getBoolean("inventory.auto-fill");
    }

    @AllArgsConstructor
    public class KitButton extends Button {

        private Player player;
        private GKit gKit;

        @Override
        public int getSlot() {
            return gKit.getSlot();
        }

        @Override
        public ItemStack getItem() {
            ItemStack itemStack = gKit.getDisplayItem().clone();

            User user = User.getByUuid(player.getUniqueId());

            List<String> replaced = new ArrayList<>();
            if (!player.hasPermission("gkitz." + gKit.getName())) {
                ItemMeta meta = itemStack.getItemMeta();
                for (String str : lGKits.getInstance().getConfig().getStringList("lore-formats.noperm")) {
                    replaced.add(str
                            .replaceAll("%description%", gKit.getDescription())
                            .replaceAll("%cooldown%", gKit.getFormattedCooldown()));
                }
                if (gKit.isGlowing()) {
                    itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                }
                meta.addItemFlags(ItemFlag.values());
                meta.setLore(CC.translate(replaced));
                itemStack.setItemMeta(meta);
                return itemStack;
            } else if (user.infoByGkit(gKit) != null) {
                ItemMeta meta = itemStack.getItemMeta();
                if (user.onCooldown(user.infoByGkit(gKit))) {
                    for (String str : lGKits.getInstance().getConfig().getStringList("lore-formats.oncooldown")) {
                        replaced.add(str
                                .replaceAll("%description%", gKit.getDescription())
                                .replaceAll("%cooldown%", gKit.getFormattedCooldown())
                                .replaceAll("%time-left%", "" + TimeUtils.formatLongIntoDetailedString((user.infoByGkit(gKit).getDuration() - System.currentTimeMillis()) / 1000L)));
                    }
                    meta.setLore(CC.translate(replaced));
                    meta.addItemFlags(ItemFlag.values());
                    itemStack.setItemMeta(meta);
                    if (gKit.isGlowing()) {
                        itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                    }
                    return itemStack;
                }
            }
            ItemMeta meta = itemStack.getItemMeta();
            for (String str : lGKits.getInstance().getConfig().getStringList("lore-formats.normal")) {
                replaced.add(str
                        .replaceAll("%description%", gKit.getDescription())
                        .replaceAll("%cooldown%", gKit.getFormattedCooldown()));
            }
            if (gKit.isGlowing()) {
                itemStack.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
            }
            meta.addItemFlags(ItemFlag.values());
            meta.setLore(CC.translate(replaced));
            itemStack.setItemMeta(meta);
            return itemStack;
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            User user = User.getByUuid(player.getUniqueId());

            if (event.getClick().isRightClick()) {
                GKitPreviewMenu g = new GKitPreviewMenu(gKit);
                g.openMenu(player);
                return;
            }

            if (!player.hasPermission("gkitz." + gKit.getName())) {
                event.setCancelled(true);
                return;
            }

            if (user.infoByGkit(gKit) != null) {
                if (user.onCooldown(user.infoByGkit(gKit))) {
                    event.setCancelled(true);
                    return;
                }
            }

            event.setCancelled(true);
            gKit.giveGkit(player, player.hasPermission("gkitz.bypass"));
        }
    }

}
