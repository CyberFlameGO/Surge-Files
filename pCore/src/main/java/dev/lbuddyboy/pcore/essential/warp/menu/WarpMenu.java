package dev.lbuddyboy.pcore.essential.warp.menu;

import dev.lbuddyboy.pcore.essential.warp.Warp;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.util.CC;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.paged.PagedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpMenu extends PagedMenu {

    @Override
    public String getPageTitle(Player player) {
        return CC.translate(pCore.getInstance().getWarpHandler().getGuiSettings().getTitle());
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (Warp warp : pCore.getInstance().getWarpHandler().getWarps().values()) {
            buttons.add(new Button() {
                @Override
                public int getSlot() {
                    return 0;
                }

                @Override
                public ItemStack getItem() {
                    return warp.getDisplayItem();
                }

                @Override
                public void action(InventoryClickEvent event) throws Exception {
                    warp.startWarping(player);
                    player.closeInventory();
                }
            });
        }

        return buttons.stream().filter(b -> b.getItem() != null && b.getItem().getType() != Material.AIR).collect(Collectors.toList());
    }

    @Override
    public int getPreviousButtonSlot() {
        return pCore.getInstance().getWarpHandler().getGuiSettings().getPreviousSlot();
    }

    @Override
    public int getNextPageButtonSlot() {
        return pCore.getInstance().getWarpHandler().getGuiSettings().getNextSlot();
    }

    @Override
    public int[] getButtonSlots() {
        return pCore.getInstance().getWarpHandler().getGuiSettings().getButtonSlots();
    }

    @Override
    public boolean autoFill() {
        return pCore.getInstance().getWarpHandler().getGuiSettings().isAutoFill();
    }

    @Override
    public ItemStack autoFillItem() {
        return pCore.getInstance().getWarpHandler().getGuiSettings().getAutoFillItem();
    }

    @Override
    public int getSize(Player player) {
        return pCore.getInstance().getWarpHandler().getGuiSettings().getSize();
    }
}
