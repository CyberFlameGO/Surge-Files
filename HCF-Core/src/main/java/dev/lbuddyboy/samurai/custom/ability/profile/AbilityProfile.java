package dev.lbuddyboy.samurai.custom.ability.profile;

import lombok.Data;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/07/2021 / 2:07 AM
 * HCTeams / rip.orbit.hcteams.profile
 */

@Data
public class AbilityProfile {

	public static Map<UUID, AbilityProfile> profileMap = new HashMap<>();

	private final UUID uuid;
	private String lastHitName = "";
	private String lastDamagerName = "";
	private long lastDamagedMillis;
	private UUID eotwLastHitName;
	private long eotwLastDamaged = 0;
	private BukkitTask eotwTagTask;

	public AbilityProfile(UUID uuid) {
		this.uuid = uuid;

		AbilityProfile.profileMap.put(this.uuid, this);
	}

	public static AbilityProfile byUUID(UUID toSearch) {
		for (AbilityProfile value : profileMap.values()) {
			if (value.getUuid() == toSearch) {
				return value;
			}
		}
		return new AbilityProfile(toSearch);
	}

}
