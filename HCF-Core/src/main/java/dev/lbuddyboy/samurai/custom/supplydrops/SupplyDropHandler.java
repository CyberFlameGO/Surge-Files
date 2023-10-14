package dev.lbuddyboy.samurai.custom.supplydrops;

import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.supplydrops.listener.SupplyCrateListener;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import dev.lbuddyboy.samurai.util.discord.DiscordLogger;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.object.Reward;
import dev.lbuddyboy.samurai.util.object.YamlDoc;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 10:18 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops
 */

@Data
public class SupplyDropHandler {

	public static String PREFIX = CC.translate("&x&c&a&3&2&d&f&l[SUPPLY CRATE]");
	public static final Comparator<Location> LOWEST_Y_COMPARATOR = Comparator.comparingInt(Location::getBlockY);

	private final YamlDoc config;
	private final List<Reward> rewards;

	private List<ItemStack> lootTable;
	private SupplyCrate activeSupplyCrate;
	private ItemStack supplyDropSummonerItem;

	public SupplyDropHandler() {
		this.lootTable = new ArrayList<>();
		this.rewards = new ArrayList<>();
		this.config = new YamlDoc(Samurai.getInstance().getDataFolder(), "supplydrops-loot.yml");

		lootTable.addAll(ItemStackSerializer.deserializeConfig(getConfig().getStringList("loot")));

		Samurai.getInstance().getServer().getPluginManager().registerEvents(new SupplyCrateListener(), Samurai.getInstance());

		this.rewards.addAll(Arrays.asList(
			new Reward("&g&lx3 Aura Keys", true).addCommand("crates give Aura {playerName} 3"),
			new Reward("&e&lx3 Surge Keys", true).addCommand("crate give Surge {playerName} 3"),
			new Reward("&7&lx3 Ability Packages", true).addCommand("ability givepp {playerName} 3"),
			new Reward("&c&lx15 Lives", true).addCommand("pvp addlives {playerName} 15"),
			new Reward("&6&lx10 Team Points", true).addCommand("f addpoints {playerName} 10 found in supply crates")
		));

		this.supplyDropSummonerItem = new ItemBuilder(Material.getMaterial(getConfig().getString("summoner.material")))
				.displayName(CC.translate(getConfig().getString("summoner.name")))
				.lore(CC.translate(getConfig().getStringList("summoner.lore")))
				.build();
	}

	public void disable() {
		if (this.activeSupplyCrate != null) {
			this.activeSupplyCrate.processLowestStage(false);
			this.activeSupplyCrate.despawn(false);
		}
	}

	public void saveLoot(List<ItemStack> stacks) {
		this.lootTable = stacks.stream().filter(s -> s != null && !s.getType().equals(Material.AIR) || s != null).collect(Collectors.toList());

		getConfig().set("loot", null);

		getConfig().set("loot", ItemStackSerializer.serializeList(lootTable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(ItemStack stack) {
		this.lootTable.add(stack);

		getConfig().set("loot", ItemStackSerializer.serializeList(lootTable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(List<ItemStack> stacks) {
		this.lootTable = stacks.stream().filter(s -> s != null && !s.getType().equals(Material.AIR) || s != null).collect(Collectors.toList());

		getConfig().set("loot", ItemStackSerializer.serializeList(lootTable));
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void buildCrate(Location to) {

		if (this.activeSupplyCrate != null) return;

		Map<Location, Material> materialMap = new HashMap<>();

		SupplyCrate supplyCrate = new SupplyCrate(to, materialMap);

		supplyCrate.setSpawnLocation(to);
		Location endLoc = supplyCrate.getEndLocation();

		Location centerLocation = supplyCrate.getCenterLocation();

		if (endLoc != null) {

			Block centerBlock = centerLocation.getBlock();

			Bukkit.broadcastMessage(CC.translate(PREFIX + " &eA supply crate has just appeared in the sky at &d" + endLoc.getBlockX() + ", " + supplyCrate.getCenterLocation().getBlockY() + ", " + endLoc.getBlockZ() + "&e."));

			for (int x = -2; x <= 2; x++) {
				for (int y = -2; y <= 2; y++) {
					for (int z = -2; z <= 2; z++) {
						materialMap.put(centerBlock.getRelative(x, y, z).getLocation(), Material.WHITE_WOOL);
					}
				}
			}
			Location lastFenceLocation = null;
			for (int y = 0; y < 7; y++) {
				materialMap.put(centerBlock.getRelative(0, -y, 0).getLocation(), Material.OAK_FENCE);
				lastFenceLocation = centerBlock.getRelative(0, -y, 0).getLocation().clone();
			}
			{
				Block block = lastFenceLocation.getBlock().getRelative(BlockFace.DOWN);
				materialMap.put(block.getLocation(), Material.OAK_PLANKS);


				materialMap.put(block.getRelative(BlockFace.NORTH).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.SOUTH).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.EAST).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.WEST).getLocation(), Material.OAK_PLANKS);

				materialMap.put(block.getRelative(BlockFace.NORTH_EAST).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.NORTH_WEST).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.SOUTH_EAST).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.SOUTH_WEST).getLocation(), Material.OAK_PLANKS);

				materialMap.put(block.getRelative(BlockFace.NORTH_EAST).getRelative(BlockFace.UP).getLocation(), Material.TARGET);
				materialMap.put(block.getRelative(BlockFace.NORTH_WEST).getRelative(BlockFace.UP).getLocation(), Material.TARGET);
				materialMap.put(block.getRelative(BlockFace.SOUTH_EAST).getRelative(BlockFace.UP).getLocation(), Material.TARGET);
				materialMap.put(block.getRelative(BlockFace.SOUTH_WEST).getRelative(BlockFace.UP).getLocation(), Material.TARGET);

				materialMap.put(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.NORTH).getRelative(BlockFace.NORTH).getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);


				materialMap.put(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH).getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);

				materialMap.put(block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);

				materialMap.put(block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.SOUTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
				materialMap.put(block.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.NORTH).getRelative(BlockFace.UP).getLocation(), Material.OAK_PLANKS);
			}

			supplyCrate.setTask(Bukkit.getScheduler().runTaskTimer(Samurai.getInstance(), () -> {

				Map<Location, Material> newMaterialMap = new HashMap<>();

				for (Map.Entry<Location, Material> entry : materialMap.entrySet()) {

					entry.getKey().getBlock().setType(Material.AIR);

					Block block = entry.getKey().getBlock().getRelative(BlockFace.DOWN);
					newMaterialMap.put(block.getLocation(), entry.getValue());

				}

				materialMap.clear();
				materialMap.putAll(newMaterialMap);

				supplyCrate.setNotOpenedMaterials(newMaterialMap);

				for (Map.Entry<Location, Material> entry : supplyCrate.getNotOpenedMaterials().entrySet()) {
					entry.getKey().getBlock().setType(entry.getValue());
				}

				if (getLowestBlock(supplyCrate.getNotOpenedMaterials()).getBlockY() <= endLoc.getBlockY()) {
					Bukkit.broadcastMessage(CC.translate(PREFIX + " &eA supply crate has just crash landed at &x&c&a&3&2&d&f" + endLoc.getBlockX() + ", " + endLoc.getBlockY() + ", " + endLoc.getBlockZ() + "&e."));
					supplyCrate.processLowestStage(true);
				}

				this.activeSupplyCrate = supplyCrate;

			}, 20 * 2, 20 * 2));

			this.activeSupplyCrate = supplyCrate;

			try {
				DiscordLogger.logSupplyCrate(supplyCrate);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public Location getLowestBlock(Map<Location, Material> locationMap) {
		List<Location> locs = new ArrayList<>(locationMap.keySet()).stream().sorted(LOWEST_Y_COMPARATOR).collect(Collectors.toList());
		Collections.reverse(locs);

		Location location = locs.get(locs.size() - 1);
		if (location == null) {
			return new Location(Bukkit.getWorlds().get(0), 350, 100, 350);
		}
		return location;
	}

	public YamlConfiguration getConfig() {
		return this.config.gc();
	}

}
