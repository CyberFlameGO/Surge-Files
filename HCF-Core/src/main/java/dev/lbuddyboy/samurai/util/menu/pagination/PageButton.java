//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package dev.lbuddyboy.samurai.util.menu.pagination;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public class PageButton extends Button {
    private final int mod;
    private final PaginatedMenu menu;

    public void clicked(Player player, int i, ClickType clickType) {
        if (clickType == ClickType.RIGHT) {
            (new ViewAllPagesMenu(this.menu)).openMenu(player);
            playNeutral(player);
        } else if (this.hasNext(player)) {
            this.menu.modPage(player, this.mod);
            Button.playNeutral(player);
        } else {
            Button.playFail(player);
        }

    }

    private boolean hasNext(Player player) {
        int pg = this.menu.getPage() + this.mod;
        return pg > 0 && this.menu.getPages(player) >= pg;
    }

    public String getName(Player player) {
        if (!this.hasNext(player)) {
            return this.mod > 0 ? "§7Last page" : "§7First page";
        } else {
            return this.mod > 0 ? "§a⟶" : "§c⟵";
        }
    }

    public List<String> getDescription(Player player) {
        return new ArrayList();
    }

    public byte getDamageValue(Player player) {
        return (byte)(this.hasNext(player) ? 11 : 7);
    }

    public Material getMaterial(Player player) {
        return null;
    }

    public ItemStack getButtonItem(Player player) {
        if (!this.hasNext(player)) {
            return this.mod > 0 ? CC.getCustomHead(CC.translate("&gLast Page"), 1, "7472d245b2a8ab25bd4b9d32601d4aba2c53181ad2bde62c8ed71f8cae99543") : CC.getCustomHead(CC.translate("&gFirst Page"), 1, "b4137232ab1911994481a72996b84bced8fa92821e4f763f13a23c8703cea");
        } else {
            return this.mod > 0 ? CC.getCustomHead(CC.translate("&g⟶"), 1, "2671c4c04337c38a5c7f31a5c751f991e96c03df730cdbee99320655c19d") : CC.getCustomHead(CC.translate("&g⟵"), 1, "93971124be89ac7dc9c929fe9b6efa7a07ce37ce1da2df691bf8663467477c7");
        }
    }

    @ConstructorProperties({"mod", "menu"})
    public PageButton(int mod, PaginatedMenu menu) {
        this.mod = mod;
        this.menu = menu;
    }
}
