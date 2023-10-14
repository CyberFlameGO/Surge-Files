package dev.lbuddyboy.samurai.custom.vaults.listener;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.events.events.EventCapturedEvent;
import dev.lbuddyboy.samurai.events.koth.KOTH;
import dev.lbuddyboy.samurai.events.koth.events.EventControlTickEvent;
import dev.lbuddyboy.samurai.events.koth.events.KOTHControlLostEvent;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.Claim;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.InventoryUtils;
import dev.lbuddyboy.samurai.util.object.CuboidRegion;
import dev.lbuddyboy.samurai.util.object.Reward;
import dev.lbuddyboy.samurai.custom.vaults.VaultHandler;
import dev.lbuddyboy.samurai.custom.vaults.VaultStage;
import dev.lbuddyboy.samurai.custom.vaults.menu.VaultMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/01/2022 / 5:59 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.vaults.listener
 */
public class VaultListener implements Listener {

	private static com.sk89q.worldedit.world.World worldEditWorld = new BukkitWorld(Bukkit.getWorld("world"));

	@EventHandler
	public void onControlTick(EventControlTickEvent event) {
		if (event.getKOTH().getName().equals(VaultHandler.TEAM_NAME)) {
			Team capper = Samurai.getInstance().getTeamHandler().getTeam(FrozenUUIDCache.uuid(event.getKOTH().getCurrentCapper()));
			if (capper == null) {
				event.setCancelled(true);
				return;
			}

			if (Samurai.getInstance().getVaultHandler().getCapping() == null) {
				Samurai.getInstance().getVaultHandler().setCapping(capper);
			}

			if (Samurai.getInstance().getVaultHandler().isContested()) {
				event.setCancelled(true);
				return;
			}

			String schematicToPaste = null;

			for (VaultStage stage : VaultStage.values()) {
				if (stage == VaultStage.CLOSED || stage == VaultStage.LOOT_1 || stage == VaultStage.LOOT_2 || stage == VaultStage.LOOT_3) {
					continue;
				}
				if (event.getKOTH().getRemainingCapTime() == stage.getSeconds()) {
					Samurai.getInstance().getVaultHandler().setVaultStage(stage);
					schematicToPaste = stage.getSchematicName();
				}
			}

			if (schematicToPaste != null) {
				paste(schematicToPaste, event.getKOTH().getCapLocation().toLocation(Bukkit.getWorld("world")));
			}
		}
	}

	@EventHandler
	public void onKnock(KOTHControlLostEvent event) {
		if (event.getKOTH().getName().equals(VaultHandler.TEAM_NAME)) {
			Samurai.getInstance().getVaultHandler().setVaultStage(VaultStage.CLOSED);
			Samurai.getInstance().getVaultHandler().setCapping(null);
			paste(VaultStage.CLOSED.getSchematicName(), event.getKOTH().getCapLocation().toLocation(Bukkit.getWorld("world")));
		}
	}

	@EventHandler
	public void onCapture(EventCapturedEvent event) {
		if (event.getEvent().getName().equals(VaultHandler.TEAM_NAME)) {

			Location location = ((KOTH) event.getEvent()).getCapLocation().toLocation(Bukkit.getWorld("world"));

			paste(VaultStage.LOOT_1.getSchematicName(), location);

			Team capper = Samurai.getInstance().getTeamHandler().getTeam(event.getPlayer());
			Samurai.getInstance().getVaultHandler().setCapping(capper);

			capper.sendMessage(CC.translate(VaultHandler.PREFIX + " &fYou have just captured the &x&7&a&9&9&c&1&lVault Post&f. It will be loot-able in 10 seconds!"));
			for (Player member : capper.getOnlineMembers()) {
				member.sendTitle(CC.translate(VaultHandler.PREFIX), CC.translate("&fYou have just captured the &x&7&a&9&9&c&1&lVault Post&f. It will be loot-able in 10 seconds!"));
			}

			Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {

				paste(VaultStage.LOOT_2.getSchematicName(), location);
				capper.sendMessage(CC.translate(VaultHandler.PREFIX + " &fYou have captured the &x&7&a&9&9&c&1&lVault Post&f. It will be loot-able in 5 seconds!"));
				for (Player member : capper.getOnlineMembers()) {
					member.sendTitle(CC.translate(VaultHandler.PREFIX), CC.translate("&fYou have captured the &x&7&a&9&9&c&1&lVault Post&f. It will be loot-able in 5 seconds!"));
				}

				Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {

					paste(VaultStage.LOOT_3.getSchematicName(), location);
					capper.sendMessage(CC.translate(VaultHandler.PREFIX + " &fYou have just captured the &x&7&a&9&9&c&1&lVault Post&f. It is now loot-able! &c&lYOU HAVE 2 MINUTES TO LOOT IT!"));

					Team vaultTeam = Samurai.getInstance().getTeamHandler().getTeam(VaultHandler.TEAM_NAME);
					for (Claim claim : vaultTeam.getClaims()) {
						for (Location loc : new CuboidRegion(VaultHandler.TEAM_NAME, claim.getMinimumPoint(), claim.getMaximumPoint())) {
							if (loc.getBlock().getType() == Material.LIME_SHULKER_BOX) {
								loc.getBlock().setType(Material.CHEST);
							}
							if (loc.getBlock().getType() == Material.CHEST) {
								Chest chest = (Chest) loc.getBlock().getState();
								for (int i = 0; i < 4; i++) {
									ItemStack chosen = Samurai.getInstance().getVaultHandler().getLoottable().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getVaultHandler().getLoottable().size()));
									chest.getBlockInventory().addItem(chosen);
								}
							}
						}
					}

					Bukkit.getScheduler().runTaskLater(Samurai.getInstance(), () -> {
						Samurai.getInstance().getVaultHandler().setCapping(null);
						Samurai.getInstance().getVaultHandler().setVaultStage(VaultStage.CLOSED);
						paste(VaultStage.CLOSED.getSchematicName(), location);
						capper.sendMessage(CC.translate(VaultHandler.PREFIX + " &cThe Vault Post is no longer able to be looted."));

					}, 20 * 60 * 2);

				}, 20 * 5);

			}, 20 * 10);
		}
	}

	public static final File WORLD_EDIT_SCHEMATICS_FOLDER = new File(JavaPlugin.getPlugin(WorldEditPlugin.class).getDataFolder(), "schematics");

	public static void paste(String schematicToPaste, Location location) {
		File file = new File(WORLD_EDIT_SCHEMATICS_FOLDER, schematicToPaste + ".schem");

		Clipboard clipboard = null;

		ClipboardFormat format = ClipboardFormats.findByFile(file);
		try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
			clipboard = reader.read();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (clipboard != null) {
			try (EditSession editSession = WorldEdit.getInstance().newEditSession(worldEditWorld)) {
				Operation operation = new ClipboardHolder(clipboard)
						.createPaste(editSession)
						.to(BlockVector3.at(location.getX(), location.getY(), location.getZ()))
						.build();
				Operations.complete(operation);
			} catch (WorldEditException e) {
				e.printStackTrace();
			}
		}


	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		ItemStack stack = event.getItem();

		if (block == null) return;

		if (!player.getWorld().getName().equals("world")) {
			return;
		}
		if (Samurai.getInstance().getVaultHandler().getCrateLocation() == null) return;
		if (Samurai.getInstance().getVaultHandler().getCrateLocation().distance(block.getLocation()) <= 0.5) {
			if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
				new VaultMenu().openMenu(player);
			} else {
				if (stack == null) return;
				if (Samurai.getInstance().getVaultHandler().getKey().isSimilar(stack)) {
					for (int i = 0; i < 1; i++) {
						Reward reward = Samurai.getInstance().getVaultHandler().getRewards().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getVaultHandler().getRewards().size()));
						reward.execute(player);
					}
					for (int i = 0; i < 2; i++) {
						ItemStack reward = Samurai.getInstance().getVaultHandler().getLoottable().get(ThreadLocalRandom.current().nextInt(0, Samurai.getInstance().getVaultHandler().getLoottable().size()));
						InventoryUtils.addAmountToInventory(player.getInventory(), reward);
						InventoryUtils.removeAmountFromInventory(player.getInventory(), Samurai.getInstance().getVaultHandler().getKey(), 1);
					}
				}
			}
		}
	}

}
