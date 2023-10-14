package dev.lbuddyboy.gkits.enchants.object;

import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 13/11/2021 / 1:48 PM
 * LBuddyBoy Development / me.lbuddyboy.gkits.enchants.object
 */

@AllArgsConstructor
@Getter
public enum Rarity {

	COMMON(lGKits.getInstance().getConfig().getString("rarities.common.name"),
			CC.translate(lGKits.getInstance().getConfig().getString("rarities.common.display-name")),
			ChatColor.valueOf(lGKits.getInstance().getConfig().getString("rarities.common.color")),
			100),

	RARE(lGKits.getInstance().getConfig().getString("rarities.rare.name"),
			CC.translate(lGKits.getInstance().getConfig().getString("rarities.rare.display-name")),
			ChatColor.valueOf(lGKits.getInstance().getConfig().getString("rarities.rare.color")),
			500
			),
	LEGENDARY(lGKits.getInstance().getConfig().getString("rarities.legendary.name"),
			CC.translate(lGKits.getInstance().getConfig().getString("rarities.legendary.display-name")),
			ChatColor.valueOf(lGKits.getInstance().getConfig().getString("rarities.legendary.color")), 1000);

	private String name;
	private String displayName;
	private ChatColor color;
	private int weight;

}
