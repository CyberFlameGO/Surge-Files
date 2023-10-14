package dev.lbuddyboy.samurai.team.menu.button;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.object.Callback;
import lombok.AllArgsConstructor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class BooleanButton extends Button {

    private boolean accept;
    private Callback<Boolean> callback;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (accept) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 20f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 20f, 0.1F);
        }

        player.closeInventory();

        callback.callback(accept);
    }

    @Override
    public String getName(Player player) {
        return accept ? "§aConfirm" : "§cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return accept ? (byte) 5 : (byte) 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEGACY_WOOL;
    }
}
