package dev.lbuddyboy.samurai.team.menu.button;

import dev.lbuddyboy.samurai.team.commands.staff.ForceDisbandCommand;
import dev.lbuddyboy.samurai.team.menu.ConfirmMenu;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DisbandTeamButton extends Button {

    private Team team;

    @Override
    public void clicked(Player player, int i, ClickType clickType) {
        if (!player.hasPermission("foxtrot.manage.forcedisband")) {
            player.sendMessage(CC.translate("&cNo permission."));
            return;
        }
        new ConfirmMenu("Disband?", (b) -> {
            if (b) {
                ForceDisbandCommand.forceDisband(player, team);
            }
        }).openMenu(player);
    }

    @Override
    public String getName(Player player) {
        return "§c§lDisband Team";
    }

    @Override
    public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    @Override
    public byte getDamageValue(Player player) {
        return 0;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.TNT;
    }
}
