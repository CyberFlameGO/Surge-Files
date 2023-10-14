package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.menu.button.KickPlayerButton;
import org.bukkit.entity.Player;

import java.util.*;

@RequiredArgsConstructor
public class KickPlayersMenu extends Menu {

    @NonNull @Getter Team team;

    @Override
    public String getTitle(Player player) {
        return "Players in " + team.getName();
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();
        int index = 0;

        ArrayList<UUID> uuids = new ArrayList<>();
        uuids.addAll(team.getMembers());

        Collections.sort(uuids, (u1, u2) -> UUIDUtils.name(u1).toLowerCase().compareTo(UUIDUtils.name(u2).toLowerCase()));

        for (UUID uuid : uuids) {
            buttons.put(index, new KickPlayerButton(uuid, team));
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
