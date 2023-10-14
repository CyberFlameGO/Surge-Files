package dev.lbuddyboy.samurai.map.kits.editor.menu;

import dev.lbuddyboy.samurai.map.kits.editor.button.EditKitMenu;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.map.kits.DefaultKit;
import dev.lbuddyboy.samurai.map.kits.Kit;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;

public final class KitsMenu extends Menu {

    public KitsMenu() {
        setAutoUpdate(true);
    }

    @Override
    public void onOpen(Player player) {
        if (!Samurai.getInstance().getMapHandler().getKitManager().awaitingRestore(player)) {
            Samurai.getInstance().getMapHandler().getKitManager().saveState(player);
        }

        InventoryUtils.resetInventoryNow(player);
    }

    @Override
    public void onClose(Player player) {
        InventoryUtils.resetInventoryNow(player);

        Bukkit.getServer().getScheduler().runTaskLater(Samurai.getInstance(), () -> {
            Menu menu = Menu.currentlyOpenedMenus.get(player.getName());
            boolean stillEditing = menu instanceof KitsMenu || menu instanceof EditKitMenu;

            if (!stillEditing) {
                Samurai.getInstance().getMapHandler().getKitManager().restoreState(player);
            }
        }, 2L);
    }

    @Override
    public String getTitle(Player var1) {
        return "Edit Kits";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int index = 0;
        List<DefaultKit> kits = Samurai.getInstance().getMapHandler().getKitManager().getDefaultKits();
        kits.sort(Comparator.comparingInt(DefaultKit::getOrder));

        for (DefaultKit originalKit : kits) {
            Optional<Kit> kitOpt = Optional.ofNullable(Samurai.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit));

            int column = index;

            buttons.put(getSlot(column, 1), new KitIconButton(kitOpt, originalKit));
            buttons.put(getSlot(column, 3), new KitEditButton(kitOpt, originalKit));

            if (kitOpt.isPresent()) {
                buttons.put(getSlot(column, 4), new KitRenameButton(kitOpt.get()));
                buttons.put(getSlot(column, 5), new KitDeleteButton(kitOpt.get()));
            } else {
                buttons.put(getSlot(column, 4), Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));
                buttons.put(getSlot(column, 5), Button.placeholder(Material.GRAY_STAINED_GLASS_PANE, ""));
            }

            index += 1;

            if (index > 9) break;
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }
}