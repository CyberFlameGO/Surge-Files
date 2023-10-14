package dev.lbuddyboy.samurai.custom.supplydrops;

import lombok.Data;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 10:17 AM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops
 */

@Data
public class SupplyCrate {

	private final Location centerLocation;
	private Map<Location, Material> notOpenedMaterials;
	private Map<Location, Material> openedMaterials;
	private Location spawnLocation;
	private BukkitTask task;

	public SupplyCrate(Location centerLocation, Map<Location, Material> notOpenedMaterials) {
		this.centerLocation = centerLocation;
		this.notOpenedMaterials = notOpenedMaterials;
	}

	public Location getEndLocation() {
		Location endLoc = null;
		for (int i = spawnLocation.getBlockY(); i >= 0; i--) {
			Location toCloned = spawnLocation.clone();
			toCloned.setY(i);
			if (toCloned.getBlock().getType().isSolid()) {
				endLoc = toCloned;
				break;
			}
		}

		return endLoc.add(0, 1, 0);
	}

	public void processLowestStage(boolean stopTask) {
		if (stopTask) this.task.cancel();

		for (Map.Entry<Location, Material> entry : this.notOpenedMaterials.entrySet()) {
			entry.getKey().getBlock().setType(Material.AIR);
		}

		this.openedMaterials = new HashMap<>();
		Block endBlock = getEndLocation().getBlock();

		for (int x = -2; x < 2; x++) {
			for (int z = -2; z < 2; z++) {
				openedMaterials.put(endBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.WEST_NORTH_WEST).getRelative(x, 0, z).getLocation(), Material.WHITE_WOOL);
				openedMaterials.put(endBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.EAST).getRelative(BlockFace.UP).getRelative(x, 0, z).getLocation(), Material.FIRE);
			}
		}
		for (int x = -2; x < 2; x++) {
			for (int z = -2; z < 2; z++) {
				openedMaterials.put(endBlock.getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.WEST).getRelative(BlockFace.EAST_NORTH_EAST).getRelative(x, 0, z).getLocation(), Material.OAK_PLANKS);
			}
		}

		openedMaterials.put(endBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.SOUTH_EAST).getLocation(), Material.CHEST);
		openedMaterials.put(endBlock.getRelative(BlockFace.SOUTH).getRelative(BlockFace.NORTH_WEST).getLocation(), Material.CHEST);
		openedMaterials.put(endBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH_WEST).getLocation(), Material.CHEST);
		openedMaterials.put(endBlock.getRelative(BlockFace.EAST).getRelative(BlockFace.NORTH_EAST).getLocation(), Material.CHEST);

		for (Map.Entry<Location, Material> entry : this.openedMaterials.entrySet()) {
			entry.getKey().getBlock().setType(entry.getValue());

			BlockState blockState = entry.getKey().getBlock().getState();
			if (blockState instanceof Chest chest) {
				for (int i = 0; i < 4; i++) {
					ItemStack stack = Samurai.getInstance().getSupplyDropHandler().getLootTable().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getSupplyDropHandler().getLootTable().size()));
					chest.getBlockInventory().addItem(stack);
				}

			}
		}

		this.task = Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> this.despawn(true), 20 * 60);
	}

	public void despawn(boolean cancelTask) {
		if (cancelTask) {
			this.task.cancel();
		}

		if (this.openedMaterials != null) {
			for (Map.Entry<Location, Material> entry : this.openedMaterials.entrySet()) {
				entry.getKey().getBlock().setType(Material.AIR);
			}
		}

		Samurai.getInstance().getSupplyDropHandler().setActiveSupplyCrate(null);
	}

}
