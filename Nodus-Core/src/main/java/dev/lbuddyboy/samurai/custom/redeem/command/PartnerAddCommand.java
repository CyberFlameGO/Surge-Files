package dev.lbuddyboy.samurai.custom.redeem.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.redeem.object.Partner;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.command.CommandSender;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 9:26 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@CommandAlias("partners")
@CommandPermission("foxtrot.admin")
public class PartnerAddCommand extends BaseCommand {

	@Subcommand("add|create")
	public static void addParty(CommandSender sender, @Name("partner") String partnerName) {
		if (Samurai.getInstance().getRedeemHandler().getPartners().contains(Samurai.getInstance().getRedeemHandler().partnerByName(partnerName))) {
			sender.sendMessage(CC.translate("&cThat partner already exists."));
			return;
		}
		Partner partner = new Partner(partnerName);
		partner.save();
		Samurai.getInstance().getRedeemHandler().getPartners().add(partner);

		sender.sendMessage(CC.translate("&aYou have just created a new redeemable partner name"));
	}

	@Subcommand("remove|delete")
	@CommandCompletion("@partners")
	public static void remove(CommandSender sender, @Name("partner") String partnerName) {
		if (!Samurai.getInstance().getRedeemHandler().getPartners().contains(Samurai.getInstance().getRedeemHandler().partnerByName(partnerName))) {
			sender.sendMessage(CC.translate("&cThat partner doesn't exist."));
			return;
		}
		Partner partner = new Partner(partnerName);

		Samurai.getInstance().getRedeemHandler().getPartners().remove(partner);

		sender.sendMessage(CC.translate("&aYou have just deleted a partner!"));
	}

}
