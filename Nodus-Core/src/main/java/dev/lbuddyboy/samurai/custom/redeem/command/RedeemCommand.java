package dev.lbuddyboy.samurai.custom.redeem.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import dev.lbuddyboy.samurai.custom.ability.command.PartnerPackageCommand;
import dev.lbuddyboy.samurai.custom.feature.Feature;
import dev.lbuddyboy.samurai.economy.uuid.FrozenUUIDCache;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.redeem.object.Partner;
import dev.lbuddyboy.samurai.util.CC;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 9:40 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@CommandAlias("redeem|claim|claimpartner")
public class RedeemCommand extends BaseCommand {

	@Subcommand("stats")
	@CommandPermission("foxtrot.redeemstats")
	public static void redeemstats(CommandSender sender) {
		sender.sendMessage(CC.translate(""));
		sender.sendMessage(CC.translate("&g&lPartner Redeem Statistics"));
		sender.sendMessage(CC.translate(""));
		for (Partner partner : Samurai.getInstance().getRedeemHandler().getRandomizedPartners()) {
			sender.sendMessage(CC.translate("&7┃ &f" + partner.getName() + "&7: &g" + partner.getRedeemedAmount()));
		}
		sender.sendMessage(CC.translate(""));
	}

	@Subcommand("reset")
	@CommandPermission("foxtrot.admin")
	@CommandCompletion("@players")
	public static void resetredeem(CommandSender sender, @Name("player") UUID target) {
		sender.sendMessage(CC.translate("&aReset " + FrozenUUIDCache.name(target) + "'s Partner Redeem"));
		Samurai.getInstance().getRedeemHandler().getRedeemMap().setToggled(target, false);
	}

	@Default
	public static void redeem(Player player, @Name("partner") String pa) {
		if (Samurai.getInstance().getFeatureHandler().isDisabled(Feature.REDEEM)) {
			player.sendMessage(CC.translate("&cThis feature is currently disabled."));
			return;
		}
		Partner partner = Samurai.getInstance().getRedeemHandler().partnerByName(pa);
		if (partner == null) {
			player.sendMessage(CC.translate(" "));
			player.sendMessage(CC.translate("&g&lRedeemable Names"));
			player.sendMessage(CC.translate(" "));
			player.sendMessage(CC.translate("&f" + StringUtils.join(Samurai.getInstance().getRedeemHandler().getPartners().stream().map(Partner::getName).collect(Collectors.toList()), ", ")));
			return;
		}

		if (Samurai.getInstance().getRedeemHandler().getRedeemMap().isToggled(player.getUniqueId())) {
			player.sendMessage(CC.translate("&cYou have already redeemed a partner."));
			return;
		}

		partner.setRedeemedAmount(partner.getRedeemedAmount() + 1);
		partner.save();

		PartnerPackageCommand.giveppnomulti(Bukkit.getConsoleSender(), new OnlinePlayer(player), 2);

		for (Player p : Bukkit.getOnlinePlayers()) {
			if (Samurai.getInstance().getRedeemBCToggleMap().isToggled(p.getUniqueId())) {
				p.sendMessage(CC.translate("&g&l[REDEEM] &g" + player.getName() + "&f has just redeemed &g" + partner.getName() + "&f's rewards! &7(/redeem)"));
			}
		}

		Samurai.getInstance().getRedeemHandler().getRedeemMap().setToggled(player.getUniqueId(), true);
	}

}
