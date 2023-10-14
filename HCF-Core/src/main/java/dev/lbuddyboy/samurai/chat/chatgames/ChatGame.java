package dev.lbuddyboy.samurai.chat.chatgames;

import dev.lbuddyboy.flash.util.Config;
import dev.lbuddyboy.samurai.Samurai;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.io.File;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 27/08/2021 / 4:36 PM
 * HCTeams / rip.orbit.hcteams.chatgames
 */
public abstract class ChatGame implements Listener {

	public boolean started;
	public Config config;

	public ChatGame(File file, String name) {
		this.started = false;
		this.config = new Config(Samurai.getInstance(), name, file);
		Bukkit.getPluginManager().registerEvents(this, Samurai.getInstance());
	}

	public abstract String name();
	public abstract void start();
	public abstract void end();

}
