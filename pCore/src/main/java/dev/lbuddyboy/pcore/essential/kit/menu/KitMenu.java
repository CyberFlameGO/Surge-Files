package dev.lbuddyboy.pcore.essential.kit.menu;

import dev.lbuddyboy.pcore.essential.kit.KitCategory;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.shop.ShopCategory;
import dev.lbuddyboy.pcore.util.Callback;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@AllArgsConstructor
public class KitMenu extends Menu {

    private final KitCategory category;
    private final Callback.SingleCallback<Kit> callable;
    private Menu previous;

    @Override
    public String getTitle(Player player) {
        return "Kits";
    }

    @Override
    public List<Button> getButtons(Player player) {
        return new ArrayList<Button>(){{
            for (Kit kit : pCore.getInstance().getKitHandler().getKits().values()) {
                if (kit.getCategory() == null) continue;
                if (category != kit.getCategory()) continue;

                add(new KitButton(player, kit, callable));
            }
        }};
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (player.hasMetadata("opening_other")) return;
        if (previous == null) return;

        Tasks.run(() -> previous.openMenu(player));
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @Override
    public int getSize(Player player) {
        return 27;
    }
}
