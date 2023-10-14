package dev.lbuddyboy.samurai.team.roster.menu;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.util.CC;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RosterMenu extends Menu {

    public static Map<Player, String> rosterAdd = new HashMap<>();

    public Team team;

    @Override
    public String getTitle(Player player) {
        return "Your Roster";
    }

    @Override
    public ItemStack autoFillItem() {
        return new ItemBuilder(Material.YELLOW_STAINED_GLASS_PANE).create();
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ViewMembersButton(team));
        buttons.add(new ViewCaptainsButton(team));
        buttons.add(new ViewCoLeadersButton(team));

        return buttons;
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }

    @AllArgsConstructor
    private static class ViewMembersButton extends Button {

        public Team team;

        @Override
        public int getSlot() {
            return 12;
        }

        @Override
        public ItemStack getItem() {
            ItemStack stack = new ItemBuilder(Material.LEATHER_HELMET).setName(CC.translate("&6Member Roster &7(Click)")).create();
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            stack.setItemMeta(meta);

            return stack;
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getMembers());
            menu.context = "Members";
            menu.color = ChatColor.GOLD;
            menu.team = team;

            menu.openMenu(player);
        }
    }

    @AllArgsConstructor
    private static class ViewCaptainsButton extends Button {

        public Team team;

        @Override
        public int getSlot() {
            return 14;
        }

        @Override
        public ItemStack getItem() {
            ItemStack stack = new ItemBuilder(Material.IRON_HELMET).setName(CC.translate("&3Captain Roster &7(Click)")).create();
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            stack.setItemMeta(meta);

            return stack;
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getCaptains());
            menu.context = "Captains";
            menu.color = ChatColor.DARK_AQUA;
            menu.team = team;

            menu.openMenu(player);
        }
    }

    @AllArgsConstructor
    private static class ViewCoLeadersButton extends Button {

        public Team team;

        @Override
        public int getSlot() {
            return 16;
        }

        @Override
        public ItemStack getItem() {
            ItemStack stack = new ItemBuilder(Material.DIAMOND_HELMET).setName(CC.translate("&bCo-Leader Roster &7(Click)")).create();
            ItemMeta meta = stack.getItemMeta();
            meta.addItemFlags(ItemFlag.values());
            stack.setItemMeta(meta);

            return stack;
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            RosterRoleMenu menu = new RosterRoleMenu(team.getRoster().getColeaders());
            menu.context = "Co-Leaders";
            menu.color = ChatColor.AQUA;
            menu.team = team;

            menu.openMenu(player);
        }
    }

}
