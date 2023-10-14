package dev.lbuddyboy.hub.config;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:40 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.config
 */

@Getter
public class DocHandler implements lModule {

	private Doc itemDoc;
	private Doc settingsDoc;
	private Doc scoreBoardDoc;
	private Doc queueDoc;

	@Override
	public void load(lHub plugin) {
		this.itemDoc = new Doc("items");
		this.settingsDoc = new Doc("settings");
		this.scoreBoardDoc = new Doc("scoreboard");
		this.queueDoc = new Doc("queue");
	}

	@Override
	public void unload(lHub plugin) {

	}

	@SneakyThrows
	@Override
	public void reload() {
		this.itemDoc.getDoc().reloadConfig();
		this.settingsDoc.getDoc().reloadConfig();
		this.scoreBoardDoc.getDoc().reloadConfig();
		this.queueDoc.getDoc().reloadConfig();
	}
}
