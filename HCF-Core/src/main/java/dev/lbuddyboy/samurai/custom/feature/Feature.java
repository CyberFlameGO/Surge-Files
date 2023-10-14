package dev.lbuddyboy.samurai.custom.feature;

import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;

@AllArgsConstructor
@Getter
public enum Feature {

	OFFLINE_INVS("&4Offline Inventories", "Stores offline inventory data. Managed by /offlineinvsee"),
	TEAM_BREW("&dTeam Brew", "Allows players to access /team brew and runs a constant task checking them"),
	AIR_DROPS("&3Air Drops", "Drops a dispenser when placing an item."),
	DEATH_LIGHTNING("&cDeath Lightning", "Spawns lightning upon death."),
	POWER("&4Power", "/power selector."),
	SUPPLY_DROP("&dSupply Drop", "Adds supply crates that spawn randomly & are able to be spawned forcefully."),
	INVISIBLE_PARTICLES("&5Invisible Particles", "Make invisible particles able to be seen or not."),
	PEARL_STASIS("&5Pearl Stasis Fix", "Fixes pearl stasis chambers going infinitely (can cause lag occasionally)."),
	CLEAR_ITEM_TASK("&5Item & Mob Clear Task", "Despawns entities occassionally."),
	END_WORLD("&dEnd World", "Disable end warps and tp everyone in there out to spawn."),
	DISABLE_SHIELDS("&6Shields Disabled", "Disable shield usage."),
	DISABLE_BEDS("&6Beds Disabled", "Disable bed bomb usage."),
	ACTION_BAR_COOLDOWNS("&eActionBar Cooldowns", "Cooldowns on action bar."),
	MINIGAME("&dMini Games", "Disable /host and forcefully end any active game."),
	NETHER_WORLD("&eNether World", "Disable nether warps and tp everyone in there out to spawn."),
	TAB("&bTab", "Adds a tab with a lot of information."),
	NORMAL_NAMETAGS("&gNormal Nametags", "Adds colored nametags relating to teams etc."),
	PETS("&eInventory Pets", "Adds the inventory pets feature /pets."),
	PLAYTIME_REWARDS("&ePlayTime Rewards", "Adds playtime rewards and ability to get kits."),
	REDEEM("&dRedeem", "Adds the ability to redeem your favorite creator."),
	LUNAR_NAMETAGS("&5Lunar Nametags", "Adds the ability to see another line with a person team name above their head."),
	LUNAR_WAYPOINTS("&5Lunar Waypoints", "Adds the ability to see waypoints on lunar client."),
	LUNAR_TEAMS("&5Lunar Teammates", "Adds the ability to see lunar teammates."),
	BATTLE_PASS("&6Battle Pass", "Adds the ability to do challenges for rewards."),
	DECO_SHOP("&6Decoration Shop", "Manages the decoration category in /shop"),
	SELL_SHOP("&cSell Shop", "Manages the sell category in /shop"),
	BUY_SHOP("&aBuy Shop", "Manages the buy category in /shop"),
	WOOL_SHOP("&6Wool Shop", "Manages the wool category in /shop"),
	CONCRETE_SHOP("&6Concrete Shop", "Manages the concrete category in /shop"),
	CLAY_SHOP("&6Clay Shop", "Manages the clay category in /shop"),
	SHARD_SHOP("&6Shard Shop", "Manages the shard category in /shop"),
	GLASS_SHOP("&6Glass Shop", "Manages the glass category in /shop"),
	MAIN_SHOP("&6Main Shop", "Adds /shop & allows players to buy/sell items");

	private String display;
	private String description;

	public boolean isDisabled() {
		return Samurai.getInstance().getFeatureHandler().isDisabled(this);
	}

}
