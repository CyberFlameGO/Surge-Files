package dev.lbuddyboy.samurai.custom.supplydrops.listener;

import dev.lbuddyboy.samurai.custom.supplydrops.SupplyCrate;
import dev.lbuddyboy.samurai.api.HourEvent;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import dev.lbuddyboy.samurai.util.object.Reward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 3:02 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.supplydrops.listener
 */
public class SupplyCrateListener implements Listener {

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate() != null) {
			if (Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate().getNotOpenedMaterials().containsKey(event.getBlock().getLocation())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onHour(HourEvent event) {

		World world = Bukkit.getWorld("world");
		int x = 0, z = 0;

		if (Samurai.getInstance().getMapHandler().isKitMap()) {
			while (Math.abs(x) <= 300) x = Samurai.RANDOM.nextInt(1000) - 650;
			while (Math.abs(z) <= 300) z = Samurai.RANDOM.nextInt(1000) - 650;

			while (LandBoard.getInstance().getTeam(new Location(world, x, 100, z)) != null) {
				x = 0; z = 0;

				while (Math.abs(x) <= 300) x = Samurai.RANDOM.nextInt(1000) - 650;
				while (Math.abs(z) <= 300) z = Samurai.RANDOM.nextInt(1000) - 650;
			}

		} else {

			while (Math.abs(x) <= 100) x = Samurai.RANDOM.nextInt(1000) - 250;
			while (Math.abs(z) <= 100) z = Samurai.RANDOM.nextInt(1000) - 250;

			while (LandBoard.getInstance().getTeam(new Location(world, x, 100, z)) != null) {
				x = 0;
				z = 0;

				while (Math.abs(x) <= 100) x = Samurai.RANDOM.nextInt(1000) - 250;
				while (Math.abs(z) <= 100) z = Samurai.RANDOM.nextInt(1000) - 250;
			}
		}

		int y = world.getHighestBlockYAt(x, z) + 60;
		Block block = world.getBlockAt(x, y, z);

		Samurai.getInstance().getSupplyDropHandler().buildCrate(block.getLocation());
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {

		ItemStack stack = event.getItem();
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block != null) {
			SupplyCrate supplyCrate = Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate();
			if (supplyCrate != null) {
				if (supplyCrate.getOpenedMaterials() != null) {
					if (supplyCrate.getOpenedMaterials().containsKey(block.getLocation())) {
						if (block.getType() == Material.CHEST) {
							block.setType(Material.AIR);
							Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate().getOpenedMaterials().remove(block.getLocation());

							for (int i = 0; i < 2; i++) {
								ItemStack rewardItem = Samurai.getInstance().getSupplyDropHandler().getLootTable().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getSupplyDropHandler().getLootTable().size()));
								block.getWorld().dropItemNaturally(block.getLocation(), rewardItem);
							}

							Reward reward = Samurai.getInstance().getSupplyDropHandler().getRewards().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getSupplyDropHandler().getRewards().size()));
							reward.execute(player);
							return;
						}
					}
				}
			}
		}
		if (stack == null) return;
		if (stack.isSimilar(Samurai.getInstance().getSupplyDropHandler().getSupplyDropSummonerItem())) {

			event.setCancelled(true);

			if (Samurai.getInstance().getSupplyDropHandler().getActiveSupplyCrate() != null) {
				player.sendMessage(CC.translate("&cA supply drop is already being summoned. Be patient and wait, until it despawns."));
				return;
			}

			Location loc1 = new Location(player.getWorld(), 0, player.getLocation().getY(), 0);

			if (player.getLocation().distance(loc1) < 600) {
				player.sendMessage(CC.translate("&cYou need to be between 600 and 950 blocks down the road."));
				return;
			}

			if (player.getLocation().distance(loc1) > 950) {
				player.sendMessage(CC.translate("&cYou need to be between 600 and 950 blocks down the road."));
				return;
			}

			Samurai.getInstance().getSupplyDropHandler().buildCrate(player.getLocation().add(0, 35, 0));

			InventoryUtils.removeAmountFromInventory(player.getInventory(), Samurai.getInstance().getSupplyDropHandler().getSupplyDropSummonerItem(), 1);
		}
	}

}
