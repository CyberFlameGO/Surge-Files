package dev.lbuddyboy.hub.util;

import dev.lbuddyboy.hub.lHub;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:36 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.util
 */
public abstract class ConfigFile {

	@Getter private final YamlDoc doc;

	public abstract YamlConfiguration getConfig();
	public abstract List<Object> values();

	@SneakyThrows
	public ConfigFile(String configName) {
		doc = new YamlDoc(lHub.getInstance().getDataFolder(), configName + ".yml");
		doc.init();
	}

}
