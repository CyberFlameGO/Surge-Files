package dev.lbuddyboy.hub.rank;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.rank.impl.Default;
import dev.lbuddyboy.hub.rank.impl.FlashCore;
import dev.lbuddyboy.hub.rank.impl.Vault;
import dev.lbuddyboy.hub.rank.impl.lCore;
import lombok.Getter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 08/11/2021 / 12:54 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.rank
 */
public class RankCoreHandler implements lModule {

	@Getter private RankCore rankCore;
	@Getter private Chat chat;

	@Override
	public void load(lHub plugin) {
		reload();
	}

	@Override
	public void unload(lHub plugin) {

	}

	@Override
	public void reload() {
		String configRankCore = lHub.getInstance().getConfig().getString("rank-core");
		if (configRankCore.equalsIgnoreCase("lcore")) {
			this.rankCore = new lCore();
		} else if (configRankCore.equalsIgnoreCase("flash")) {
			this.rankCore = new FlashCore();
		} else if (configRankCore.equalsIgnoreCase("none")) {
			this.rankCore = new Default();
		} else if (configRankCore.equalsIgnoreCase("vault")) {
			setupChat();
			this.rankCore = new Vault();
		} else {
			this.rankCore = new Default();
		}
	}

	private void setupChat() {
		RegisteredServiceProvider<? extends Chat> rsp = lHub.getInstance().getServer().getServicesManager().getRegistration((Class<? extends Chat>) Chat.class);
		this.chat = rsp.getProvider();
	}

}
