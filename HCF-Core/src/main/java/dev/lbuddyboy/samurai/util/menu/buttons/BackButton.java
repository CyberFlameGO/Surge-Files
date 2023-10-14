package dev.lbuddyboy.samurai.util.menu.buttons;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class BackButton extends Button {
    private final Menu back;

    @Override
    public Material getMaterial(Player player) {
        return null;
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public String getName(Player player) {
        return "\u00a7cGo back";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public ItemStack getButtonItem(Player player) {
        return CC.getCustomHead(CC.translate("&g⟵"), 1, "93971124be89ac7dc9c929fe9b6efa7a07ce37ce1da2df691bf8663467477c7");
    }

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        Button.playNeutral(player);
        this.back.openMenu(player);
    }

    @ConstructorProperties(value={"back"})
    public BackButton(Menu back) {
        this.back=back;
    }
}

