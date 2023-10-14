package dev.lbuddyboy.samurai.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 23/12/2021 / 2:38 PM
 * SteelHCF-main / com.steelpvp.hcf
 */

@AllArgsConstructor
@Getter
public enum FoxtrotConfiguration {

	SERVER_NAME("serverName",  "MineSurge"),
	ABILITY_EVENT_DISPLAY("ability-event.display-name",  "&6&lAbility Event"),
	ABILITY_EVENT_CONTEXT("ability-event.context",  "&6&l%timer%&6 will end in %time-left%"),
	ABILITY_EVENT_START_MESSAGE("ability-event.start-message", Arrays.asList(
			"&7&m----------------------",
			"&6&lAbility Event",
			"",
			"&fAll &eability item cooldowns&f will be",
			"&freduced by half, until the timer is gone!",
			"&7&m----------------------"
	)),
	SOTW_TIMER_DISPLAY("sotw.display-name",  "&6&lSOTW"),
	SOTW_TIMER_CONTEXT("sotw.context",  "&6&l%timer%&6 will end in %time-left%"),
	TRIPLE_KEYS_DISPLAY("triple-keys.display-name",  "&b&lTRIPLE KEYS"),
	TRIPLE_KEYS_CONTEXT("triple-keys.context",  "&b&l%timer%&b will end in %time-left%"),
	DOUBLE_AIRDROPS_DISPLAY("double-airdrops.display-name",  "&3&lDOUBLE AIRDROPS"),
	DOUBLE_AIRDROPS_CONTEXT("double-airdrops.context",  "&3&l%timer%&3 will end in %time-left%"),
	DOUBLE_COINS_DISPLAY("double-coins.display-name",  "&e&lDOUBLE COINS"),
	DOUBLE_COINS_CONTEXT("double-coins.context",  "&e&l%timer%&e will end in %time-left%"),
	SOTW_TIMER_START_MESSAGE("sotw.start-message", Arrays.asList(
			"&7&m----------------------",
			"&6&lSOTW Timer",
			"",
			"&fNo one will take damage unless",
			"&fafter running &e/sotw enable&f.",
			"&7&m----------------------"
	)),
	SOTW_TIMER_END_MESSAGE("sotw.end-message", Arrays.asList(
			"&7&m----------------------",
			"&6&lSOTW Timer",
			"",
			"&fSOTW Timer is now over. You can",
			"&fnow take damage and no long &e/spawn&f.",
			"&7&m----------------------"
	)),
	LUNAR_CLIENT_TEAM_VIEW("lunar.team-view",  true),
	LUNAR_CLIENT_NAMETAGS("lunar.nametags",  true),
	USE_TAB("use-top-tab",  true),

	FALL_DAMAGE_MULTIPLIER("fall-damage-multiplier", 1.30),
	BACK_IN_TIME_MULTIPLIER("back-in-time-multiplier", 1.20),
	HITS_ARCHER_DAMAGE_MULTIPLIER("archer-hits-damage-multiplier", 1.20);

	private String section;
	private Object defaultValue;

	public void update(Object value) {
		Samurai.getInstance().getConfig().set(section, value);
		Samurai.getInstance().saveConfig();
	}

	public boolean getBoolean() {
		return Samurai.getInstance().getConfig().getBoolean(section, (Boolean) defaultValue);
	}

	public String getString() {
		return Samurai.getInstance().getConfig().getString(section, (String) defaultValue);
	}

	public String getString(Player target) {
		return Samurai.getInstance().getConfig().getString(section, (String) defaultValue)
				.replaceAll("%target%", target.getName());
	}

	public String getString(Player sender, Player target) {
		return Samurai.getInstance().getConfig().getString(section, (String) defaultValue)
				.replaceAll("%sender%", sender.getName())
				.replaceAll("%target%", target.getName());
	}

	public List<String> getStringList() {
		return Samurai.getInstance().getConfig().getStringList(section);
	}

	public int getInt() {
		return Samurai.getInstance().getConfig().getInt(section, (int) defaultValue);
	}
	public double getDouble() {
		return Samurai.getInstance().getConfig().getDouble(section, (double) defaultValue);
	}

}
