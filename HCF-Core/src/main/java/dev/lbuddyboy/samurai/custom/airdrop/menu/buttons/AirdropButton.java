package dev.lbuddyboy.samurai.custom.airdrop.menu.buttons;

import dev.lbuddyboy.samurai.custom.airdrop.AirDropReward;
import dev.lbuddyboy.samurai.util.menu.Button;
import lombok.AllArgsConstructor;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 25/02/2022 / 10:58 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.airdrop.menu.buttons
 */

@AllArgsConstructor
public class AirdropButton extends Button {

	private AirDropReward reward;

	@Override
	public String getName(Player player) {
		return null;
	}

	@Override
	public List<String> getDescription(Player player) {
		return null;
	}

	@Override
	public Material getMaterial(Player player) {
		return null;
	}

	@Override
	public ItemStack getButtonItem(Player player) {
		ItemBuilder builder = new ItemBuilder(reward.getStack().clone());
		List<String> lore = new ArrayList<>();

		if (reward.getStack().hasItemMeta() && reward.getStack().getItemMeta().hasLore()) {
			lore.addAll(ItemUtils.getLore(reward.getStack()));
		}

		lore.add(" ");
		lore.add(" &gChance: &f" + reward.getChance());
		if (player.isOp()) {
			lore.add(" &gCommand: &f" + reward.getCommand());
			lore.add(" &gBroadcasts: &f" + (reward.isBroadcast() ? "Yes" : "No"));
		}

		builder.lore(CC.translate(lore));
		builder.displayName(CC.translate(reward.getDisplayName()));

		return builder.build();
	}

	@Override
	public void clicked(Player player, int slot, ClickType clickType) {

	}
}
