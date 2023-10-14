package dev.lbuddyboy.samurai.custom.teamwar.menu;

import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.paged.PagedMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarPlayer;
import dev.lbuddyboy.samurai.custom.teamwar.model.WarTeam;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class MembersMenu extends PagedMenu {

    private WarTeam team;

    @Override
    public String getPageTitle(Player player) {
        return "Members";
    }

    @Override
    public List<Button> getPageButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        for (WarPlayer warPlayer : this.team.getMembers().values()) {
            buttons.add(new PlayerButton(warPlayer));
        }

        return buttons;
    }

    @AllArgsConstructor
    public class PlayerButton extends Button {

        private WarPlayer player;

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public ItemStack getItem() {
            return new ItemBuilder(Material.PLAYER_HEAD)
                    .displayName("&6&l" + FrozenUUIDCache.name(this.player.getUuid()))
                    .skullOwner(UUIDUtils.name(player.getUuid()))
                    .lore(
                            CC.GOLD + CC.UNICODE_ARROWS_RIGHT + " &fCurrent Class&7: &e" + this.player.getKit().getKitName(),
                            " ",
                            " &7Click to choose a different class for this player."
                    )
                    .build();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            new ClassesMenu(team, this.player).openMenu(player);
        }
    }

}
