package me.lbuddyboy.staff;

import lombok.Getter;
import me.lbuddyboy.staff.editor.EditItem;
import me.lbuddyboy.staff.editor.listener.EditItemListener;
import me.lbuddyboy.staff.listener.StaffModeListener;
import me.lbuddyboy.staff.util.CC;
import me.lbuddyboy.staff.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.UUID;

@Getter
public class StaffModeHandler {

	private lStaff plugin = lStaff.getInstance();

	public HashMap<UUID, ItemStack[]> prevInventory;
	public HashMap<UUID, ItemStack[]> prevInventoryArmor;

	private final ItemStack thruCompass = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("staff-mode.thru-compass.material")))
			.setDisplayName(CC.chat(plugin.getConfig().getString("staff-mode.thru-compass.name")))
			.hideAll()
			.glow()
			.create();
	private final ItemStack randomTP = new ItemBuilder(CC.getCustomHead("&gRandom Teleport", 1, "a3826181ce9012b665865f3ac0066307b4d02da281540104e0461ffefa7459fd"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack vanishon = new ItemBuilder(CC.getCustomHead("&aVanished", 1, "81ce95dd480f17fee071f9e8f7e2e9c0542ac72ab5d2ae53ccead2bd72370c25"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack vanishoff = new ItemBuilder(CC.getCustomHead("&cVisible", 1, "b6cdcc68cb5477ba9df8497babe59289fc3c08eba7442151e74f27cca059f46e"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack betterView = new ItemBuilder(CC.getCustomHead("&gSpectator Mode &7(Click)", 1, "bdc575c8b3a195f3d9be7aa4e8849e4c25fcda4a347b6befd06dd86f988cb696"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack freezer = new ItemBuilder(CC.getCustomHead("&gFreezer", 1, "a2644071b6c7bbae7b5e45d9f82f96ffb5ee8e177a23b825a4465607f1c9c"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack inspector = new ItemBuilder(CC.getCustomHead("&gInventory Inspector", 1, "7dc985a7a68c574f683c0b859521feb3fc3d2ffa05fa09db0bae44b8ac29b385"))
			.hideAll()
			.glow()
			.create();
	private final ItemStack lastPvP = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("staff-mode.lastpvp.material")))
			.setDisplayName(CC.chat(plugin.getConfig().getString("staff-mode.lastpvp.name")))
			.hideAll()
			.glow()
			.create();
	private final ItemStack worldedit = new ItemBuilder(Material.valueOf(plugin.getConfig().getString("staff-mode.worldedit.material")))
			.setDisplayName(CC.chat(plugin.getConfig().getString("staff-mode.worldedit.name")))
			.hideAll()
			.glow()
			.create();

	public StaffModeHandler(lStaff plugin) {
		prevInventory = new HashMap<>();
		prevInventoryArmor = new HashMap<>();

		Bukkit.getPluginManager().registerEvents(new StaffModeListener(plugin), plugin);
		Bukkit.getPluginManager().registerEvents(new EditItemListener(), plugin);
	}

	public void loadStaffMode(Player player) {
		EditItem editItem = EditItem.byUUID(player.getUniqueId());
		if (player.getGameMode() != GameMode.SPECTATOR) {
			prevInventory.put(player.getUniqueId(), player.getInventory().getContents());
			prevInventoryArmor.put(player.getUniqueId(), player.getInventory().getArmorContents());
		}
		player.setGameMode(GameMode.SURVIVAL);
		if (player.hasPermission("lCrates.admin")) {
			player.setGameMode(GameMode.CREATIVE);
		}
		player.setAllowFlight(true);
		player.getInventory().clear();
		player.setMetadata("modmode", new FixedMetadataValue(plugin, player));
		if (player.isOp()) {
			if (editItem.isWorldEditEnabled()) {
				player.getInventory().setItem(editItem.getWorldEditSlot(), worldedit);
			}
		}

		if (editItem.isThruCompassEnabled()) {
			player.getInventory().setItem(editItem.getCompassSlot(), thruCompass);
		}
		if (editItem.isRandomTPEnabled()) {
			player.getInventory().setItem(editItem.getRandomTPSlot(), randomTP);
		}
		if (editItem.isVanishEnabled()) {
			player.getInventory().setItem(editItem.getVanishSlot(), vanishon);
		}
		if (editItem.isBetterViewEnabled()) {
			player.getInventory().setItem(editItem.getBetterViewSlot(), betterView);
		}

		if (editItem.isFreezerEnabled()) {
			player.getInventory().setItem(editItem.getFreezerSlot(), freezer);
		}
		if (editItem.isInspectorEnabled()) {
			player.getInventory().setItem(editItem.getInspectorSlot(), inspector);
		}
		if (editItem.isLastPvPEnabled()) {
			player.getInventory().setItem(editItem.getLastPvPSlot(), lastPvP);
		}
		loadVanish(player);
		player.updateInventory();
	}

	public void unloadStaffMode(Player player) {
		player.getInventory().clear();
		player.getInventory().setArmorContents(prevInventoryArmor.get(player.getUniqueId()));
		prevInventoryArmor.remove(player.getUniqueId());
		player.getInventory().setContents(prevInventory.get(player.getUniqueId()));
		prevInventory.remove(player.getUniqueId());
		player.removeMetadata("modmode", plugin);
		unloadVanish(player);
		player.setGameMode(GameMode.SURVIVAL);
		player.setAllowFlight(false);
		player.updateInventory();
	}

	public void loadVanish(Player player) {
		player.setMetadata("invisible", new FixedMetadataValue(plugin, player));
		for (Player on : Bukkit.getOnlinePlayers()) {
			if (!on.hasPermission("ostaff.staff")) {
				on.hidePlayer(player);
			}
		}
	}

	public void unloadVanish(Player player) {
		player.removeMetadata("invisible", plugin);
		for (Player on : Bukkit.getOnlinePlayers()) {
			if (!on.hasPermission("ostaff.staff")) {
				on.showPlayer(player);
			}
		}
	}

	public boolean inStaffMode(Player player) {
		return player.hasMetadata("modmode");
	}

	public boolean isVanished(Player player) {
		return player.hasMetadata("invisible");
	}
}
