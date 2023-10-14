package dev.lbuddyboy.samurai.commands.menu.onlinestaff;

import dev.lbuddyboy.samurai.persist.maps.AFKMap;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.TimeUtils;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.persist.maps.PlaytimeMap;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/02/2022 / 12:05 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.commands.menu.onlinestaff
 */

@AllArgsConstructor
public class StaffButton extends Button {

	private Player online;

	@Override
	public String getName(Player player) {
		return null;
	}

	@Override
	public List<String> getDescription(Player player) {
		return null;
	}

	@Override
	public Material getMaterial(Player player) {
		return null;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) stack.getItemMeta();

		PlaytimeMap playtime = Samurai.getInstance().getPlaytimeMap();
		int playtimeTime = (int) playtime.getPlaytime(online.getUniqueId());

		AFKMap afk = Samurai.getInstance().getAfkMap();
		int afkTime = (int) afk.getPlaytime(online.getUniqueId());

		Player bukkitPlayer = Samurai.getInstance().getServer().getPlayer(online.getUniqueId());

		if (bukkitPlayer != null) {
			playtimeTime += playtime.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
			afkTime += afk.getCurrentSession(bukkitPlayer.getUniqueId()) / 1000;
		}

		if (player.isOp()) {
			meta.setLore(CC.translate(Arrays.asList(
					"&7&m----------------",
					"&gPlaytime: &f" + TimeUtils.formatIntoDetailedString(playtimeTime) ,
					"&gVanished: &f" + (online.hasMetadata("invisible") ? "Yes" : "No"),
					"&gModMode: &f" + (online.hasMetadata("modmode") ? "Yes" : "No"),
					"&gAFK Time: &f" + TimeUtils.formatIntoDetailedString(afkTime),
					"&7&m----------------",
					"&7Click to teleport",
					"&7&m----------------"
			)));
		} else {
			meta.setLore(CC.translate(Arrays.asList(
					"&7&m----------------",
					"&gPlaytime: &f" + TimeUtils.formatIntoDetailedString(playtimeTime) ,
					"&gVanished: &f" + (online.hasMetadata("invisible") ? "Yes" : "No"),
					"&gModMode: &f" + (online.hasMetadata("modmode") ? "Yes" : "No"),
					"&7&m----------------",
					"&7Click to teleport",
					"&7&m----------------"
			)));
		}

		meta.setOwningPlayer(online);
		meta.setDisplayName(CC.translate(online.getDisplayName()));

		stack.setItemMeta(meta);
		return stack;
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {
		player.teleport(online);
	}
}
