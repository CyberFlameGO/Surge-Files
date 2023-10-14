package dev.lbuddyboy.pcore.essential.kit.menu;

import dev.lbuddyboy.pcore.essential.kit.Kit;
import dev.lbuddyboy.pcore.util.Tasks;
import dev.lbuddyboy.pcore.util.menu.Button;
import dev.lbuddyboy.pcore.util.menu.Menu;
import dev.lbuddyboy.pcore.util.menu.button.BackButton;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KitPreviewMenu extends Menu {

    private Kit kit;
    private Menu previous;

    @Override
    public String getTitle(Player player) {
        return kit.getName() + " Preview";
    }

    @Override
    public boolean autoFill() {
        return true;
    }

    @Override
    public List<Button> getButtons(Player player) {
        return new ArrayList<Button>(){{
            add(new BackButton(9, new KitMenu(kit.getCategory(), null)));


            int top = 1;
            int below = 19;

            for (ItemStack item : kit.getArmor()) {
                add(Button.fromItem(item, top++));
            }

            for (ItemStack item : kit.getFakeItems()) {
                if (below > 54) continue;
                add(Button.fromItem(item, below++));
            }

            for (ItemStack item : kit.getContents()) {
                if (below > 54) continue;
                add(Button.fromItem(item, below++));
            }
        }};
    }

    @Override
    public void close(Player player) {
        super.close(player);
        if (player.hasMetadata("opening_menu")) return;
        if (previous == null) return;

        Tasks.run(() -> previous.openMenu(player));
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

}
