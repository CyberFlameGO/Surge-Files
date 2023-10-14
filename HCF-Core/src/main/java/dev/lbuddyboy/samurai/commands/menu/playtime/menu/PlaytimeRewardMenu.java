package dev.lbuddyboy.samurai.commands.menu.playtime.menu;

import dev.lbuddyboy.flash.util.JavaUtils;
import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.pagination.PaginatedMenu;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.menu.playtime.PlayTimeReward;
import dev.lbuddyboy.samurai.persist.maps.PlaytimeMap;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/12/2021 / 3:05 PM
 * SteelHCF-main / com.steelpvp.hcf.user.playtime.menu
 */
public class PlaytimeRewardMenu extends PaginatedMenu {
	@Override
	public String getPrePaginatedTitle(Player var1) {
		return CC.translate("&gPlaytime Rewards");
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		int i = -1;
		for (PlayTimeReward reward : Samurai.getInstance().getPlayTimeRewardsManager().getPlayTimeRewards()) {
			buttons.put(++i, new Button() {
				@Override
				public String getName(Player var1) {
					return null;
				}

				@Override
				public List<String> getDescription(Player var1) {
					return null;
				}

				@Override
				public Material getMaterial(Player var1) {
					return null;
				}

				@Override
				public ItemStack getButtonItem(Player player) {

					ItemStack stack = reward.buildItem();
					ItemMeta meta = stack.getItemMeta();
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
					meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
					stack.setItemMeta(meta);

					return stack;
				}

				@Override
				public void clicked(Player player, int slot, ClickType clickType) {

					PlaytimeMap playtime = Samurai.getInstance().getPlaytimeMap();
					int playtimeTime = (int) (playtime.getPlaytime(player.getUniqueId()) + playtime.getCurrentSession(player.getUniqueId()) / 1000);

					if (player.hasPermission("ranks.copper") || player.hasPermission("ranks.tin") || player.hasPermission("ranks.quartz") || player.hasPermission("ranks.diamond") || player.hasPermission("ranks.steel")) {
						playtimeTime = playtimeTime * 2;
					}

					if (playtimeTime < JavaUtils.parse(reward.getPlaytimeRequired()) / 1000) {
						player.sendMessage(CC.translate("&cYou are not eligible for this reward. You need " + reward.getPlaytimeRequired() + " of playtime to redeem this reward."));
						return;
					}
					if (reward.hasClaimed(player.getUniqueId())) {
						player.sendMessage(CC.translate("&cYou already have this playtime reward claimed."));
						return;
					}
					for (String command : reward.getCommands()) {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player%", player.getName()));
					}

					reward.setClaimed(player.getUniqueId(), true);

					player.sendMessage(CC.translate("&aYou have just claimed that playtime reward!"));

				}
			});
		}

		return buttons;
	}

	@Override
	public boolean isUpdateAfterClick() {
		return true;
	}

}
