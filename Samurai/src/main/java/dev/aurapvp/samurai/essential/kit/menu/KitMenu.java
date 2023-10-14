package dev.aurapvp.samurai.essential.kit.menu;

import dev.aurapvp.samurai.essential.kit.Kit;
import dev.aurapvp.samurai.essential.kit.KitCategory;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.util.Callback;
import dev.aurapvp.samurai.util.Tasks;
import dev.aurapvp.samurai.util.menu.Button;
import dev.aurapvp.samurai.util.menu.Menu;
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
            for (Kit kit : Samurai.getInstance().getKitHandler().getKits().values()) {
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
