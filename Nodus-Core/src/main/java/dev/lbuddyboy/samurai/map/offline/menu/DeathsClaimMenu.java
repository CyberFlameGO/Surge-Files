package dev.lbuddyboy.samurai.map.offline.menu;

import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeathsClaimMenu extends PaginatedMenu {

    private final UUID uuid;
    private final ItemStack[] stacks;

    public DeathsClaimMenu(UUID uuid) {
        this.uuid = uuid;
        this.stacks = Samurai.getInstance().getOfflineHandler().getDeathsClaim().getOrDefault(uuid, new ItemStack[0]);
    }

    @Override
    public String getPrePaginatedTitle(Player var1) {
        return "Death Claim";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (ItemStack content : this.stacks) {
            buttons.put(i, new DeathsClaimButton(content, i++));
        }

        return buttons;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 45;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 54;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @AllArgsConstructor
    public class DeathsClaimButton extends Button {

        private ItemStack stack;
        private int i;

        @Override
        public String getName(Player var1) {
            return null;
        }

        @Override
        public List<String> getDescription(Player var1) {
            return null;
        }

        @Override
        public Material getMaterial(Player var1) {
            return null;
        }

        @Override
        public ItemStack getButtonItem(Player player) {
            return this.stack;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            ItemUtils.tryFit(player, stacks[i], false);
            stacks[i] = null;
            Samurai.getInstance().getOfflineHandler().getDeathsClaim().put(uuid, stacks);
        }

    }

}
