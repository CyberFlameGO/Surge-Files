package dev.lbuddyboy.samurai.persist.maps.toggle;

import dev.lbuddyboy.samurai.persist.PersistMap;

import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 10:14 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.persist.maps.toggle
 */
public class ReclaimBCToggleMap extends PersistMap<Boolean> {

	public ReclaimBCToggleMap() {
		super("ReclaimBCToggle", "ReclaimBCToggle");
	}

	@Override
	public String getRedisValue(Boolean toggled) {
		return String.valueOf(toggled);
	}

	@Override
	public Object getMongoValue(Boolean toggled) {
		return String.valueOf(toggled);
	}

	@Override
	public Boolean getJavaObject(String str) {
		return Boolean.valueOf(str);
	}

	public void setToggled(UUID update, boolean toggled) {
		updateValueAsync(update, toggled);
	}

	public boolean isToggled(UUID check) {
		return (contains(check) ? getValue(check) : true);
	}

}
