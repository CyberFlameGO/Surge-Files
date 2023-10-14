package dev.lbuddyboy.samurai.team.allies;

import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 21/12/2021 / 10:39 PM
 * SteelHCF-main / com.steelpvp.hcf.faction.struct
 */

@AllArgsConstructor
@Getter
public enum AllySetting {

//	ALLY_TAKE_OUT_CONTAINED("&b&lAllies Take Items From Containers In Claim", Material.CHEST, 3, "You %status% take items from containers"),
	ALLY_INTERACT_CLAIM("&b&lAllies Interact With Claim", Material.OAK_FENCE_GATE, 2, "You %status% interact with all blocks"),
	ALLY_BREAK_CLAIM("&b&lAllies Break Blocks In Claim", Material.DIAMOND_PICKAXE, 1, "You %status% break blocks"),
	ALLY_BUILD_CLAIM("&b&lAllies Build In Claim", Material.STONE, 0, "You %status% build");

	private String display;
	private Material material;
	private int slot;
	private String context;

	public List<String> getLore(Team team) {
		List<String> list = new ArrayList<>();

		list.add(" ");
		list.add("&7Current Status: " + (team.getAllySettings().contains(this) ? "&aAllowed" : "&cDisallowed"));
		list.add(" ");
		if (team.getAllySettings().contains(this)) {
			list.add("&7Click to toggle this option off.");
		} else {
			list.add("&7Click to toggle this option on.");
		}
		list.add(" ");

		return CC.translate(list);
	}

}
