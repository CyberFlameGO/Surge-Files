package dev.lbuddyboy.samurai.custom.shop.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.commands.staff.SOTWCommand;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.custom.shop.ShopCategory;
import dev.lbuddyboy.samurai.custom.shop.menu.ShopCategoryMenu;
import dev.lbuddyboy.samurai.team.Team;
import dev.lbuddyboy.samurai.team.claims.LandBoard;
import dev.lbuddyboy.samurai.team.dtr.DTRBitmask;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.entity.Player;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 05/12/2021 / 10:07 AM
 * SteelHCF-main / com.steelpvp.hcf.shop.command
 */

@CommandAlias("sellshop|sell")
public class SellShopCommand extends BaseCommand {

	@Default
	public static void shop(Player player) {
		if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.MAIN_SHOP)) {
			player.sendMessage(CC.translate("&cThis feature is currently disabled."));
			return;
		}

		boolean additionalChecks = false;
		if (SOTWCommand.isSOTWTimer()) {
			if (SOTWCommand.hasSOTWEnabled(player.getUniqueId())) {
				additionalChecks = true;
			}
		} else {
			additionalChecks = true;
		}

		if (additionalChecks) {
			if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
				Team teamAt = LandBoard.getInstance().getTeam(player.getLocation());
				Team team = Samurai.getInstance().getTeamHandler().getTeam(player.getUniqueId());

				if (!Samurai.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
					player.sendMessage(CC.translate("&cYou need to be in a safe zone to open the shop."));
					return;
				}

				if (teamAt == null) {
					player.sendMessage(CC.translate("&cYou need to be in a safe zone or your teams claim to open the shop."));
					return;
				}

				if (team != null && teamAt != team) {
					player.sendMessage(CC.translate("&cYou need to be in a safe zone or your teams claim to open the shop."));
					return;
				}

				if (team == null) {
					player.sendMessage(CC.translate("&cYou need to be in a safe zone or your teams claim to open the shop."));
					return;
				}
			}
		}

		new ShopCategoryMenu(ShopCategory.SELL).openMenu(player);

	}

}
