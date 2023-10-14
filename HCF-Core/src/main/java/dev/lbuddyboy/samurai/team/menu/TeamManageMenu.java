package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.team.menu.button.*;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class TeamManageMenu extends Menu {

    private Team team;


    @Override
	public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 27; i++) {
            if (i == 18) {
                buttons.put(i, new OpenMuteMenuButton(team));
            }
            if (i == 26) {
                buttons.put(i, new RollbackButton(team));
            }
            if (i == 9) {
                buttons.put(i, new RenameButton(team));
            } else if (i == 10) {
                buttons.put(i, new OpenDTRMenuButton(team));
            } else if (i == 12) {
                buttons.put(i, new OpenPromoteMenuButton(team));
            } else if (i == 13) {
                buttons.put(i, new OpenKickMenuButton(team));
            } else if (i == 14) {
                buttons.put(i, new OpenDemoteMenuButton(team));
            } else if (i == 16) {
                buttons.put(i, new OpenLeaderMenuButton(team));
            } else if (i == 17) {
                buttons.put(i, new DisbandTeamButton(team));
            }
        }

        return buttons;
    }

    @Override
    public int size(Map<Integer, Button> buttons) {
        return 36;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
	public String getTitle(Player player) {
        return "Manage " + team.getName();
    }
}
