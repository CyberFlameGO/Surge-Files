package dev.lbuddyboy.samurai.team.menu.button;

import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class DTRButton extends Button {
    
    private Team team;
    private boolean increase;
    private boolean freezeDTR;
    private boolean takeOffDTR;
    private boolean maxDTR;

    @Override
	public void clicked(Player player, int i, ClickType clickType) {
        if (!increase && (team.getDTR() - 1) <= 0 && !player.hasPermission("hcf.command.faction.argument.setraidable")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to set teams as raidable. This has been logged.");
            Command.broadcastCommandMessage(player, "Tried to set a team raidable, but doesn't have permission.");
            return;
        }

        if (increase) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 20f, 0.1f);
            team.setDTR(team.getDTR() + 1);
        } else {
            if (freezeDTR) {
                team.setDTRCooldown(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(30L));
            } else {
                if (takeOffDTR) {
                    team.setDTRCooldown(System.currentTimeMillis());
                } else {
                    if (maxDTR) {
                        team.setDTR(team.getMaxDTR());
                    } else {
                        team.setDTR(team.getDTR() - 1);
                    }
                }
            }
            player.playSound(player.getLocation(), Sound.BLOCK_GRAVEL_BREAK, 20f, 0.1F);
        }
        player.chat("/f i " + team.getName());

    }

    
    @Override
	public String getName(Player player) {
        if (maxDTR) {
            return CC.translate("&aSet to Max DTR");
        }
        if (takeOffDTR) {
            return CC.translate("&aTake off DTR Freeze");
        }
        return increase ? CC.translate("&aIncrease by 1.0") : (freezeDTR) ? CC.translate("&cPut on DTR freeze.") : CC.translate("&cDecrease by 1.0");
    }

    
    @Override
	public List<String> getDescription(Player player) {
        return new ArrayList<>();
    }

    
    @Override
	public byte getDamageValue(Player player) {
        if (takeOffDTR || maxDTR) {
            return 0;
        }
        return increase ? (byte) 5 : (freezeDTR) ? (byte) 0 : (byte) 14;
    }

    
    @Override
	public Material getMaterial(Player player) {
        if (maxDTR) {
            return Material.DIAMOND_BLOCK;
        }
        if (freezeDTR) {
            return Material.ICE;
        }
        if (takeOffDTR) {
            return Material.PACKED_ICE;
        }
        return Material.LEGACY_WOOL;
    }
}