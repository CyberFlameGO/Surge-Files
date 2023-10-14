package dev.lbuddyboy.bunkers.game.ore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/03/2022 / 11:57 PM
 * SteelBunkers / com.steelpvp.bunkers.game.ore
 */

@AllArgsConstructor
@Getter
public class OreBreak {

	private Material material;
	private Location location;
	private long brokeAt;

}
