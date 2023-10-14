package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.menu.button.MakeLeaderButton;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class SelectNewLeaderMenu extends Menu {

    @NonNull @Getter Team team;

    @Override
    public String getTitle(Player player) {
        return "Leader for " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.addAll(team.getMembers());

        Collections.sort(uuids, Comparator.comparing(u -> UUIDUtils.name(u).toLowerCase()));

        for (UUID u : uuids) {
            buttons.put(index, new MakeLeaderButton(u, team));
            index++;
        }

        if (buttons.size() <= 9) {
            buttons.put(9, new BackButton(new TeamManageMenu(team)));
        } else if (buttons.size() <= 18) {
            buttons.put(18, new BackButton(new TeamManageMenu(team)));
        } else if (buttons.size() <= 27) {
            buttons.put(27, new BackButton(new TeamManageMenu(team)));
        }

        return buttons;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

}
