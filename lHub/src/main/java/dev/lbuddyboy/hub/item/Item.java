package dev.lbuddyboy.hub.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 9:02 PM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item
 */

@AllArgsConstructor
@Data
public class Item {

	private String sectionName;
	private ItemStack stack;
	private int slot;
	private List<Action> clicks;
	private String action;
	private String val;

}
