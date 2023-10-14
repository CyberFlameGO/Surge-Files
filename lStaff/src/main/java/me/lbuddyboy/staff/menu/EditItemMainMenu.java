package me.lbuddyboy.staff.menu;

import me.lbuddyboy.staff.editor.EditItem;
import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.menu.slots.*;
import me.lbuddyboy.staff.util.CC;
import me.lbuddyboy.staff.util.ItemBuilder;
import me.lbuddyboy.staff.util.menu.object.Button;
import me.lbuddyboy.staff.util.menu.object.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/07/2021 / 12:43 AM
 * oStaff / rip.orbit.ostaff.menu
 */
public class EditItemMainMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "Edit Your Item's Slots";
	}

	@Override
	public List<Button> buttons(Player player) {
		List<Button> buttons = new ArrayList<>();

		EditItem item = EditItem.byUUID(player.getUniqueId());

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isThruCompassEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(Material.COMPASS).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gThru Compass Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 9;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new ThruCompassSlotMenu().openMenu(player);
				} else {
					item.setThruCompassEnabled(!item.isThruCompassEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isRandomTPEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(lStaff.getInstance().getStaffModeHandler().getRandomTP()).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gRandom TP Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 10;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new RandomTPSlotMenu().openMenu(player);
				} else {
					item.setRandomTPEnabled(!item.isRandomTPEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isVanishEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(lStaff.getInstance().getStaffModeHandler().getVanishon()).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setData((player.hasMetadata("modmode") ? 10 : 7)).setDisplayName(CC.chat("&gVanish Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 11;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new VanishSlotMenu().openMenu(player);
				} else {
					item.setVanishEnabled(!item.isVanishEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isBetterViewEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(lStaff.getInstance().getStaffModeHandler().getBetterView()).setLore(CC.chat(Arrays.asList("" +
						"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gBetter View Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 13;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new BetterViewSlotMenu().openMenu(player);
				} else {
					item.setBetterViewEnabled(!item.isBetterViewEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isLastPvPEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(Material.DIAMOND).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gLast PvP TP Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 14;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new LastPvPTPSlotMenu().openMenu(player);
				} else {
					item.setLastPvPEnabled(!item.isLastPvPEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isFreezerEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(lStaff.getInstance().getStaffModeHandler().getFreezer()).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gFreezer Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 15;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new FreezerSlotMenu().openMenu(player);
				} else {
					item.setFreezerEnabled(!item.isFreezerEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isInspectorEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(lStaff.getInstance().getStaffModeHandler().getInspector()).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gInspector Slot Editor " + status)).create();
			}

			@Override
			public int slot() {
				return 16;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new InspectorSlotMenu().openMenu(player);
				} else {
					item.setInspectorEnabled(!item.isInspectorEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isWorldEditEnabled() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(Material.LEGACY_WOOD_AXE).setLore(CC.chat(Arrays.asList("" +
								"&7Left Click to modify the slot",
						"&7Right Click to toggle/enable it"
				))).setDisplayName(CC.chat("&gWorld Edit Slot Editor " + status
				)).create();
			}

			@Override
			public int slot() {
				return 17;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				if (event.getClick().isLeftClick()) {
					new WorldEditSlotMenu().openMenu(player);
				} else {
					item.setWorldEditEnabled(!item.isWorldEditEnabled());
					item.save();

					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			}
		});

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				String status = (item.isStaffModeOnJoin() ? CC.chat("&a(Enabled)") : CC.chat("&c(Disabled)"));
				return new ItemBuilder(Material.LEGACY_SIGN).setLore(CC.chat(Collections.singletonList("" +
						"&7Click to toggle on/off your staff mode on join"
				))).setDisplayName(CC.chat("&gStaff Mode On Join Toggler " + status
				)).create();
			}

			@Override
			public int slot() {
				return 31;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				item.setStaffModeOnJoin(!item.isStaffModeOnJoin());
				item.save();
			}
		});

		return buttons;
	}

	@Override
	public int size(Player player) {
		return 45;
	}

	@Override
	public boolean isAutoUpdate(Player player) {
		return true;
	}

	@Override
	public boolean isAutoFill(Player player) {
		return true;
	}
}
