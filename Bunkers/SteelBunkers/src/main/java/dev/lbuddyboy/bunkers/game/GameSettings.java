package dev.lbuddyboy.bunkers.game;

import dev.lbuddyboy.bunkers.Bunkers;
import dev.lbuddyboy.bunkers.util.LocationSerializer;
import dev.lbuddyboy.bunkers.util.YamlDoc;
import lombok.Data;
import dev.lbuddyboy.bunkers.util.Cuboid;
import org.bukkit.Location;

import java.io.IOException;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/03/2022 / 7:34 PM
 * SteelBunkers / com.steelpvp.bunkers.game
 */

@Data
public class GameSettings {

	private final YamlDoc config;

	private Location spawnLocation;
	private Cuboid bounds;
	private Cuboid kothCuboid;
	private String kothName;

	public GameSettings() {
		this.config = new YamlDoc(Bunkers.getInstance().getDataFolder(), "game-settings.yml");

		this.load();
	}

	private void load() {
		if (this.config.gc().contains("kothLoc-1")) {
			this.kothCuboid = new Cuboid(LocationSerializer.deserializeString(this.config.gc().getString("kothLoc-1")), LocationSerializer.deserializeString(this.config.gc().getString("kothLoc-2")));
		}
		if (this.config.gc().contains("mapLoc-1")) {
			this.bounds = new Cuboid(LocationSerializer.deserializeString(this.config.gc().getString("mapLoc-1")), LocationSerializer.deserializeString(this.config.gc().getString("mapLoc-2")));
		}
		this.kothName = this.config.gc().getString("koth-name");
	}

	public void save() {
		if (this.kothCuboid != null) {
			this.config.gc().set("kothLoc-1", LocationSerializer.serializeString(this.kothCuboid.getUpperSW()));
			this.config.gc().set("kothLoc-2", LocationSerializer.serializeString(this.kothCuboid.getLowerNE()));
		}
		if (this.bounds != null) {
			this.config.gc().set("mapLoc-1", LocationSerializer.serializeString(this.bounds.getUpperSW()));
			this.config.gc().set("mapLoc-2", LocationSerializer.serializeString(this.bounds.getLowerNE()));
		}
		this.config.gc().set("koth-name", this.kothName);
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
