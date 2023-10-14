package dev.lbuddyboy.hub.item.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Name;
import co.aikar.commands.annotation.Subcommand;
import dev.lbuddyboy.hub.item.Item;
import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.util.CC;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 9:11 PM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item.command
 */

@CommandAlias("hubitem|customitem|items")
@CommandPermission("lhub.admin")
public class ItemCommand extends BaseCommand {

	@Subcommand("createitem")
	public void createItem(Player sender, @Name("name") String name) {
		ItemStack stack = sender.getItemInHand();
		if (stack == null) {
			sender.sendMessage(CC.translate("&cYou need to have an item in your hand to do this."));
			return;
		}
		lHub.getInstance().getItemHandler().getItems().add(new Item(name, stack, sender.getInventory().getHeldItemSlot() + 1, Arrays.asList(
				Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK
		), "SEND_MESSAGE", "test"));
		lHub.getInstance().getItemHandler().save(name, sender.getInventory().getHeldItemSlot() + 1, stack);
		sender.sendMessage(CC.translate("&aCreated a new hub item."));
	}

}
