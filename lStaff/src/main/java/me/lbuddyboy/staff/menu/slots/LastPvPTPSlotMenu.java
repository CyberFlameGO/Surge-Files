package me.lbuddyboy.staff.menu.slots;

import me.lbuddyboy.staff.editor.EditItem;
import me.lbuddyboy.staff.lStaff;
import me.lbuddyboy.staff.menu.EditItemMainMenu;
import me.lbuddyboy.staff.util.CC;
import me.lbuddyboy.staff.util.ItemBuilder;
import me.lbuddyboy.staff.util.menu.object.Button;
import me.lbuddyboy.staff.util.menu.object.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 19/07/2021 / 12:56 AM
 * oStaff / rip.orbit.ostaff.menu.slots
 */
public class LastPvPTPSlotMenu extends Menu {
	@Override
	public List<Button> buttons(Player player) {
		List<Button> buttons = new ArrayList<>();

		for (int i = 0; i < 9; i++) {
			int finalI = i;
			EditItem loadOut = EditItem.byUUID(player.getUniqueId());
			buttons.add(new Button() {
				@Override
				public ItemStack stack(Player player) {
					if (loadOut.getLastPvPSlot() == (finalI)) {
						return new ItemBuilder(Material.LEGACY_INK_SACK).setData(10).setDisplayName(CC.chat("&aSlot #" + (finalI + 1))).create();
					}
					return new ItemBuilder(Material.LEGACY_INK_SACK).setData(7).setDisplayName(CC.chat("&7Slot #" + (finalI + 1))).create();
				}

				@Override
				public int slot() {
					return (finalI);
				}

				@Override
				public void action(Player player, int slot, InventoryClickEvent event) {
					loadOut.setLastPvPSlot((finalI));
					loadOut.save();
					player.sendMessage(CC.chat("&cSet your lastpvp tp slot to the #" + (finalI + 1) + " slot."));
					lStaff.getInstance().getStaffModeHandler().unloadStaffMode(player);
					lStaff.getInstance().getStaffModeHandler().loadStaffMode(player);
				}
			});
		}

		buttons.add(new Button() {
			@Override
			public ItemStack stack(Player player) {
				return new ItemBuilder(Material.FEATHER).setDisplayName(CC.chat("&bGo Back")).create();
			}

			@Override
			public int slot() {
				return 17;
			}

			@Override
			public void action(Player player, int slot, InventoryClickEvent event) {
				new EditItemMainMenu().openMenu(player);
			}
		});
		return buttons;
	}

	@Override
	public int size(Player player) {
		return 18;
	}

	@Override
	public boolean isAutoUpdate(Player player) {
		return true;
	}

	@Override
	public String getTitle(Player player) {
		return "LastPvP Slot Editor";
	}
}
