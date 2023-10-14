package dev.lbuddyboy.samurai.team.logs.menu;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.util.UUIDUtils;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.logs.TeamLog;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.text.SimpleDateFormat;
import java.util.*;

@AllArgsConstructor
public class TeamLogsMenu extends PaginatedMenu {

    private Team team;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return team.getName() + "'s Logs";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        int i = 0;
        for (TeamLog log : team.getLogs()) {
            buttons.put(i++, new LogButton(log));
        }

        return buttons;
    }

    @AllArgsConstructor
    private static class LogButton extends Button {

        private TeamLog log;

        @Override
        public String getName(Player player) {
            SimpleDateFormat sdf = new SimpleDateFormat();
            sdf.setTimeZone(TimeZone.getTimeZone("EST"));
            if (log.isReviewed()) {
                return CC.translate("&g&l" + sdf.format(new Date(log.getExecutedAt())) + " &c&l(REVIEWED)");
            }
            return CC.translate("&g&l" + sdf.format(new Date(log.getExecutedAt())));
        }

        @Override
        public List<String> getDescription(Player player) {
            if (log.isReviewed()) {
                return CC.translate(Arrays.asList(
                        CC.GOLD + StringUtils.repeat("⎯", 35),
                        "&gAction&7: &f" + log.getAction(),
                        "&gCommand&7: &f" + log.getCommand(),
                        "&gExecutor&7: &f" + UUIDUtils.name(log.getExecutor()),
                        " ",
                        "&gReviewed By&7: &f" + UUIDUtils.name(log.getReviewedBy()),
                        CC.GOLD + StringUtils.repeat("⎯", 35)
                ));
            }
            return CC.translate(Arrays.asList(
                    CC.GOLD + StringUtils.repeat("⎯", 35),
                    "&gAction&7: &f" + log.getAction(),
                    "&gCommand&7: &f" + log.getCommand(),
                    "&gExecutor&7: &f" + UUIDUtils.name(log.getExecutor()),
                    CC.GOLD + StringUtils.repeat("⎯", 35)
            ));
        }

        @Override
        public Material getMaterial(Player player) {
            return Material.PAPER;
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType) {

        }
    }

}
