package dev.lbuddyboy.samurai.commands.menu.schedule.buttons;

import dev.lbuddyboy.samurai.custom.schedule.ScheduledTime;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.TimeUtils;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.SymbolUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 12:29 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.menu.schedule.buttons
 */
public class EventScheduleButton extends Button {

	@Override
	public String getName(Player player) {
		return CC.translate("&gEvents Schedule");
	}

	@Override
	public List<String> getDescription(Player player) {
		
		List<String> lore = new ArrayList<>();

		lore.add(CC.translate("&7"));

		if (Samurai.getInstance().getScheduleHandler().getScheduledTimes().isEmpty()) {
			lore.add(CC.translate("&cThere is nothing scheduled at the moment."));
			lore.add(CC.translate("&7"));
		} else {
			for (Map.Entry<String, ScheduledTime> entry : Samurai.getInstance().getScheduleHandler().getScheduledTimes().entrySet()) {
				String[] args = entry.getKey().split(":");
				lore.add(CC.translate("&g" + args[0]));
				if (player.isOp()) {
					lore.add(CC.translate(" &7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gCommand&f: &c" + entry.getValue().getCommand()));
				}
				lore.add(CC.translate(" &7 " + SymbolUtil.UNICODE_ARROW_RIGHT + " &gTime Left&f: &c" + TimeUtils.formatLongIntoDetailedString((entry.getValue().getTimeLeft()) / 1000)));
				lore.add(CC.translate("&f"));
			}
		}
		
		return CC.translate(lore);
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PLAYER_HEAD;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		return CC.getCustomHead(getName(player), 1, "7406e45318e9a4a6bfe132f202fe3ceac15d11eaedbef1eb06a376db433090a8", getDescription(player));
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {

	}
}
