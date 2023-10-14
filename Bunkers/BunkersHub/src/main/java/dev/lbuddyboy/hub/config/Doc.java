package dev.lbuddyboy.hub.config;

import dev.lbuddyboy.hub.util.ConfigFile;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:39 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.config
 */
public class Doc extends ConfigFile {

	public Doc(String configName) {
		super(configName);
	}

	@Override
	public YamlConfiguration getConfig() {
		return getDoc().gc();
	}

	@Override
	public List<Object> values() {
		return Collections.emptyList();
	}

}
