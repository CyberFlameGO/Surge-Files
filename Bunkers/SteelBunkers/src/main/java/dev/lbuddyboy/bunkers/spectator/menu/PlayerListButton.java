package dev.lbuddyboy.bunkers.spectator.menu;

import dev.lbuddyboy.bunkers.util.bukkit.ItemBuilder;
import dev.lbuddyboy.bunkers.util.menu.Button;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 20/03/2022 / 8:52 PM
 * SteelBunkers / com.steelpvp.bunkers.spectator.menu
 */

@AllArgsConstructor
public class PlayerListButton extends Button {

    private Player player;
    private int slot;

    @Override
    public int getSlot() {
        return 0;
    }

    @Override
    public ItemStack getItem() {
        return new ItemBuilder(Material.PLAYER_HEAD)
                .setName("&g" + this.player.getName() + (this.player.hasMetadata("spectator") ? " &7(Spectator)" : ""))
                .setLore(
                        " ",
                        "&7&oClick to teleport",
                        " "
                )
                .setOwner(this.player.getName())
                .create();
    }

    @Override
    public void action(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();

        player.teleport(this.player);
    }
}
