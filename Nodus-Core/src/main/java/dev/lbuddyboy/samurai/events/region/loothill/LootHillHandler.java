package dev.lbuddyboy.samurai.events.region.loothill;

import dev.lbuddyboy.samurai.events.region.loothill.listeners.HillListener;
import dev.lbuddyboy.samurai.util.FileHelper;
import dev.lbuddyboy.samurai.util.loottable.LootTable;
import dev.lbuddyboy.samurai.util.loottable.LootTableHandler;
import dev.lbuddyboy.samurai.util.object.Config;
import lombok.Data;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 15/03/2022 / 10:56 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.events.region.abilityhill
 */

@Data
public class LootHillHandler {

	@Getter private final static String teamName = "LootHill";
	private final Config config;
	private final LootTable lootTable;

	private LootHill lootHill;
	private long lastReset;
	private static File file;

	public LootHillHandler() {
		this.lastReset = System.currentTimeMillis();
		this.config = new Config(Samurai.getInstance(), "loot-hill");
		this.lootTable = new LootTable(this.config);
		LootTableHandler.getLootTables().add(this.lootTable);

		Samurai.getInstance().getServer().getPluginManager().registerEvents(new HillListener(), Samurai.getInstance());

		try {
			file = new File(Samurai.getInstance().getDataFolder(), "loot-hill.json");

			if (!file.exists()) {
				if (file.createNewFile()) {
					Samurai.getInstance().getLogger().warning("Created a new Loot Hill json file.");
				}
			} else {
				lootHill = Samurai.GSON.fromJson(FileHelper.readFile(file), LootHill.class);
				Samurai.getInstance().getLogger().info("Successfully loaded the Loot Hill from file");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		int secs = (20 * 60) * 30;

		Samurai.getInstance().getServer().getScheduler().runTaskTimer(Samurai.getInstance(), () -> {
			lootHill.reset();

			// Broadcast the reset
			Bukkit.broadcastMessage(CC.translate("&x&f&b&9&e&0&9[Loot Hill]") + ChatColor.GREEN + " All loot hill blocks have been reset!");
			lastReset = System.currentTimeMillis();
		}, 10, secs);

	}

	public long getResetTime() {
		long timeSinceLast = (System.currentTimeMillis() - lastReset) / 1000;

		return (60 * 30) - timeSinceLast;
	}

	public void save() {
		FileHelper.writeFile(file, Samurai.GSON.toJson(this.lootHill));
	}

	public boolean hasAbilityHill() {
		return lootHill != null;
	}

	public static Claim getClaim() {
		return Samurai.getInstance().getTeamHandler().getTeam(teamName).getClaims().get(0); // null if no glowmtn is set!
	}

}
