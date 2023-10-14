package dev.lbuddyboy.samurai.custom.daily.menu;

import dev.lbuddyboy.flash.util.bukkit.ItemBuilder;
import dev.lbuddyboy.flash.util.menu.Button;
import dev.lbuddyboy.flash.util.menu.GUISettings;
import dev.lbuddyboy.flash.util.menu.Menu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.daily.DailyReward;
import dev.lbuddyboy.samurai.custom.daily.DailyRewardsMap;
import dev.lbuddyboy.samurai.custom.pets.PetRarity;
import dev.lbuddyboy.samurai.custom.pets.menu.sub.PetRarityMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DailyMenu extends Menu {

    private static final GUISettings guiSettings = Samurai.getInstance().getDailyHandler().getGuiSettings();

    @Override
    public String getTitle(Player player) {
        return CC.translate(guiSettings.getTitle());
    }

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();

        buttons.add(new ClaimButton(player));
 
        return buttons;
    }

    @Override
    public boolean autoUpdate() {
        return true;
    }

    @AllArgsConstructor
    public class ClaimButton extends Button {

        private Player player;

        @Override
        public int getSlot() {
            return 5;
        }

        @Override
        public ItemStack getItem() {
            Material material = Material.getMaterial(Samurai.getInstance().getDailyHandler().getConfig().getString("menu-settings.button-format.material"));
            int data = Samurai.getInstance().getDailyHandler().getConfig().getInt("menu-settings.button-format.data");
            DailyRewardsMap map = Samurai.getInstance().getDailyHandler().getRewardsMap();

            return new ItemBuilder(material, 1)
                    .setName(Samurai.getInstance().getDailyHandler().getConfig().getString("menu-settings.button-format.name"))
                    .setLore(Samurai.getInstance().getDailyHandler().getConfig().getStringList("menu-settings.button-format.lore"),
                            "%reward-status%", map.isAvailable(player.getUniqueId()) ? "&a&lAVAILABLE" : TimeUtils.formatIntoHHMMSS((int) (map.getRemaining(player.getUniqueId()) / 1000))
                    )
                    .setDurability(data)
                    .create();
        }

        @Override
        public void action(InventoryClickEvent event) {
            if (!(event.getWhoClicked() instanceof Player)) return;
            DailyReward next = Samurai.getInstance().getDailyHandler().getNext(player.getUniqueId());
            DailyRewardsMap map = Samurai.getInstance().getDailyHandler().getRewardsMap();

            if (next == null) {
                player.sendMessage(CC.translate("&cYou have claimed all of your daily rewards."));
                return;
            }

            if (!map.isAvailable(player.getUniqueId())) {
                player.sendMessage(CC.translate("&cYou can claim your daily reward in " + TimeUtils.formatIntoHHMMSS((int) (map.getRemaining(player.getUniqueId()) / 1000))));
                return;
            }

            next.claim(player);
        }
    }

}
