package dev.lbuddyboy.samurai.custom.redeem;

import dev.lbuddyboy.samurai.custom.redeem.map.PartnerRedeemMap;
import lombok.Getter;
import dev.lbuddyboy.samurai.custom.redeem.object.Partner;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 06/09/2021 / 7:10 PM
 * HCTeams / rip.orbit.hcteams.redeem
 */

@Getter
public class RedeemHandler {

	private final List<Partner> partners;
	private final PartnerRedeemMap redeemMap;

	public RedeemHandler() {
		partners = new ArrayList<>();

		for (Document document : Partner.getCollection().find()) {
			partners.add(new Partner(document.getString("name")));
		}
		redeemMap = new PartnerRedeemMap();
		redeemMap.loadFromRedis();
	}

	public List<Partner> getRandomizedPartners() {
		Collections.shuffle(this.partners);
		return this.partners;
	}

	public Partner partnerByName(String toSearch) {
		for (Partner partner : partners) {
			if (partner.getName().equals(toSearch)) {
				return partner;
			}
		}
		return null;
	}

}
