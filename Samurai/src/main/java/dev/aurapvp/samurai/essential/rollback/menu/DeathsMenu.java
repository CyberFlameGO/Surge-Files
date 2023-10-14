package dev.aurapvp.samurai.essential.rollback.menu;

import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.essential.rollback.menu.sub.InventoryViewMenu;
import dev.aurapvp.samurai.util.CC;
import dev.aurapvp.samurai.util.ItemBuilder;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.paged.PagedMenu;
import lombok.AllArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class DeathsMenu extends PagedMenu {

    private OfflinePlayer player;
    private List<PlayerDeath> deaths;

    @Override
    public String getPageTitle(Player player) {
        return "Deaths (" + this.player.getName() + ")";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (PlayerDeath death : deaths) {
            buttons.add(new PlayerDeathButton(death));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class PlayerDeathButton extends Button {

        private PlayerDeath death;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            List<String> lore = new ArrayList<>();

            if (this.death.getKiller() == null) {
                lore = Arrays.asList(
                        CC.MENU_BAR,
                        "&8&o" + this.death.getDeathCause(),
                        CC.MENU_BAR,
                        "&eDied on &7" + this.death.getDiedTime(),
                        " ",
                        "&7Click to view the dead inventory",
                        CC.MENU_BAR
                );
            } else {
                OfflinePlayer killer = Bukkit.getOfflinePlayer(this.death.getKiller());

                lore = Arrays.asList(
                        CC.MENU_BAR,
                        "&8&o" + this.death.getDeathCause(),
                        CC.MENU_BAR,
                        "&eDied on &7" + this.death.getDiedTime(),
                        "&eDied by &7" + killer.getName(),
                        " ",
                        "&7Left click to view the dead inventory",
                        "&7Right click to view the killer inventory",
                        CC.MENU_BAR
                );
            }

            return new ItemBuilder(Material.LEGACY_SKULL_ITEM)
                    .setName(CC.WHITE + this.death.getId().toString())
                    .setLore(lore)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) throws Exception {
            if (!(event.getWhoClicked() instanceof Player)) return;
            Player player = (Player) event.getWhoClicked();

            if (this.death.getKiller() == null) {
                new InventoryViewMenu(this.death, deaths, this.death.getVictim(), this.death.getVictimArmor(), this.death.getVictimInventory()).openMenu(player);
                return;
            }

            if (event.getClick() == ClickType.LEFT) {
                new InventoryViewMenu(this.death, deaths, this.death.getKiller(), this.death.getVictimArmor(), this.death.getVictimInventory()).openMenu(player);
                return;
            }

            new InventoryViewMenu(this.death, deaths, this.death.getKiller(), this.death.getKillerArmor(), this.death.getKillerInventory()).openMenu(player);
        }
    }

}
