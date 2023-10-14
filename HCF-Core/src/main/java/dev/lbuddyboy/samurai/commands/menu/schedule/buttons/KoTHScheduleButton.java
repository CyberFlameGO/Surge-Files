package dev.lbuddyboy.samurai.commands.menu.schedule.buttons;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.Event;
import dev.lbuddyboy.samurai.events.EventScheduledTime;
import dev.lbuddyboy.samurai.events.EventType;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/02/2022 / 12:29 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.menu.schedule.buttons
 */
public class KoTHScheduleButton extends Button {

	public static final DateFormat KOTH_DATE_FORMAT = new SimpleDateFormat("EEE h:mm a");

	@Override
	public String getName(Player player) {
		return CC.translate("&gKoTH Schedule");
	}

	@Override
	public List<String> getDescription(Player player) {

		List<String> lore = new ArrayList<>();

		lore.add(" ");

		int sent = 0;
		Date now = new Date();

		for (Map.Entry<EventScheduledTime, String> entry : Samurai.getInstance().getEventHandler().getEventSchedule().entrySet()) {
			Event resolved = Samurai.getInstance().getEventHandler().getEvent(entry.getValue());

			if (resolved == null || resolved.isHidden() || !entry.getKey().toDate().after(now) || resolved.getType() != EventType.KOTH) {
				continue;
			}

			if (sent > 5) {
				break;
			}

			sent++;
			lore.add(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + entry.getValue() + ChatColor.GOLD + " can be captured at " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(entry.getKey().toDate()) + ChatColor.GOLD + ".");
		}

		if (sent == 0) {
			lore.add(ChatColor.GOLD + "[King of the Hill] " + ChatColor.RED + "KOTH Schedule: " + ChatColor.YELLOW + "Undefined");
		} else {
			lore.add(" ");
			lore.add(ChatColor.GOLD + "[King of the Hill] " + ChatColor.YELLOW + "It is currently " + ChatColor.BLUE + KOTH_DATE_FORMAT.format(new Date()) + ChatColor.GOLD + ".");
		}

		lore.add(" ");

		return CC.translate(lore);
	}

	@Override
	public Material getMaterial(Player player) {
		return Material.PLAYER_HEAD;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		return CC.getCustomHead(getName(player), 1, "b866a77b472c72f5131191c6d521ed6a31f7e5620e772f1944c42c11a90ea6dd", getDescription(player));
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {

	}
}

