package dev.lbuddyboy.communicate;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.ChatColor;

import java.text.DecimalFormat;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:09 PM
 * SteelBunkers / com.steelpvp.bunkers.team
 */

@Data
@AllArgsConstructor
public class Team {

	private final ChatColor color;
	private final int maxDTR;
	private double dtr;

	public String getDisplay() {
		return this.color + ChatColor.BOLD.toString() + getName();
	}

	public String getName() {
		if (color == ChatColor.RED) {
			return "ʀᴇᴅ";
		}
		if (color == ChatColor.YELLOW) {
			return "ʏᴇʟʟᴏᴡ";
		}
		if (color == ChatColor.GREEN) {
			return "ɢʀᴇᴇɴ";
		}
		if (color == ChatColor.BLUE) {
			return "ʙʟᴜᴇ";
		}

		return "ᴄᴇɴᴛʀᴀʟ";
	}

	public String getDTRFormatted() {
		return new DecimalFormat("0.0").format(this.dtr);
	}

	public boolean isRaidable() {
		return this.dtr < 0;
	}

}
