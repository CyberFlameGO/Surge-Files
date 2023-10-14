package dev.lbuddyboy.samurai.team.menu;

import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.allies.AllySetting;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/12/2021 / 10:38 PM
 * SteelHCF-main / com.steelpvp.hcf.faction.menu
 */

@AllArgsConstructor
public class FactionSettingsMenu extends Menu {

	private Team team;

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aTeam Settings");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (AllySetting setting : AllySetting.values()) {
			buttons.put(setting.getSlot(), new Button() {
				@Override
				public String getName(Player var1) {
					return CC.translate(setting.getDisplay());
				}

				@Override
				public List<String> getDescription(Player var1) {
					return setting.getLore(team);
				}

				@Override
				public Material getMaterial(Player var1) {
					return setting.getMaterial();
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {
					if (team.getAllySettings().contains(setting)) {
						team.disableAllySetting(player.getUniqueId(), setting);
						team.sendMessage(CC.translate("&a&lFACTION SETTING CHANGE &7- " + setting.getDisplay() + " &7-> &cDisallowed"));
						team.sendMessage(CC.translate("&7" + setting.getContext() + " in your allied factions claim.").replaceAll("%status%", "cannot"));
						for (Team allied : team.getAllies().stream().map(objectId -> Samurai.getInstance().getTeamHandler().getTeam(objectId)).toList()) {
							allied.sendMessage(CC.translate("&a&lFACTION SETTING CHANGE &7- " + setting.getDisplay() + " &7-> &cDisallowed"));
							allied.sendMessage(CC.translate("&7" + setting.getContext() + " in your allied factions claim.").replaceAll("%status%", "cannot"));
						}
					} else {
						team.enableAllySetting(player.getUniqueId(), setting);
						team.sendMessage(CC.translate("&a&lFACTION SETTING CHANGE &7- " + setting.getDisplay() + " &7-> &aAllowed"));
						team.sendMessage(CC.translate("&7" + setting.getContext() + " in your allied factions claim.").replaceAll("%status%", "can"));
						for (Team allied : team.getAllies().stream().map(objectId -> Samurai.getInstance().getTeamHandler().getTeam(objectId)).toList()) {
							allied.sendMessage(CC.translate("&a&lFACTION SETTING CHANGE &7- " + setting.getDisplay() + " &7-> &aAllowed"));
							allied.sendMessage(CC.translate("&7" + setting.getContext() + " in your allied factions claim.").replaceAll("%status%", "can"));
						}
					}
				}
			});
		}

		return buttons;
	}

	@Override
	public boolean isUpdateAfterClick() {
		return true;
	}

	@Override
	public boolean isAutoUpdate() {
		return true;
	}
}
