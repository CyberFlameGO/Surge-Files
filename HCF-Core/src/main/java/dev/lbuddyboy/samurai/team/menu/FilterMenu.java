package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.maps.FilterModeMap;
import dev.lbuddyboy.samurai.team.TeamFilter;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class FilterMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return "Filter Menu";
    }

    @Override
    public Map<Integer, Button> getButtons(Player var1) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(4, new FilterButton());

        return buttons;
    }

    public class FilterButton extends Button {

        @Override
        public String getName(Player var1) {
            return CC.translate("&6Team List Filters");
        }

        @Override
        public List<String> getDescription(Player player) {
            List<String> lore = new ArrayList<>();
            FilterModeMap map = Samurai.getInstance().getFilterModeMap();
            TeamFilter activeFilter = map.getFilter(player.getUniqueId());

            for (TeamFilter filter : TeamFilter.values()) {
                if (activeFilter == filter) {
                    lore.add(" &7" + CC.UNICODE_ARROWS_RIGHT + " &e" + filter.lore);
                    continue;
                }
                lore.add(" &e" + filter.lore);
            }

            return CC.translate(lore);
        }

        @Override
        public Material getMaterial(Player var1) {
            return Material.RECOVERY_COMPASS;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {
            FilterModeMap map = Samurai.getInstance().getFilterModeMap();
            TeamFilter activeFilter = map.getFilter(player.getUniqueId());
            List<TeamFilter> filters = Arrays.stream(TeamFilter.values()).toList();
            int index = filters.indexOf(activeFilter);
            if (index == filters.size() - 1) index = -1;

            map.setFilter(player.getUniqueId(), filters.get(index + 1));
        }
    }

}
