package dev.lbuddyboy.samurai.util.menu.buttons;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.Callback;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BooleanButton extends Button {
    private final boolean confirm;
    private final Callback<Boolean> callback;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (this.confirm) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 20.0f, 0.1f);
        } else {
            player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 20.0f, 0.1f);
        }
        player.closeInventory();
        this.callback.callback(this.confirm);
    }

    @Override
    public String getName(Player player) {
        return this.confirm ? "\u00a7aConfirm" : "\u00a7cCancel";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return this.confirm ? (byte) 5 : 14;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.LEGACY_WOOL;
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        if (confirm) {
            return CC.getCustomHead(CC.translate("&aConfirm"), 1, "8926c1f2c3c14d086c40cfc235fe938694f4a51067ada4726b486ea1c87b03e2");
        } else {
            return CC.getCustomHead(CC.translate("&cCancel"), 1, "6b2c2678e93640988e685ac89347efa4524119c9d8f7278ce0181bc3e4fb2b09");
        }
    }

    @ConstructorProperties(value={"confirm", "callback"})
    public BooleanButton(boolean confirm, Callback<Boolean> callback) {
        this.confirm=confirm;
        this.callback=callback;
    }
}

