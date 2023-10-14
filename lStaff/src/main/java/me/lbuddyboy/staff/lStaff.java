package me.lbuddyboy.staff;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.flash.handler.SpoofHandler;
import lombok.Getter;
import me.lbuddyboy.staff.command.EditStaffCommand;
import me.lbuddyboy.staff.command.StaffModeCommand;
import me.lbuddyboy.staff.command.ToggleStaffCommand;
import me.lbuddyboy.staff.command.VanishCommand;
import me.lbuddyboy.staff.util.CC;
import me.lbuddyboy.staff.util.menu.MenuManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class lStaff extends JavaPlugin {

	@Getter private static lStaff instance;
	@Getter private StaffModeHandler staffModeHandler;
	@Getter private MongoDatabase mongoDatabase;

	@Override
	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();

		staffModeHandler = new StaffModeHandler(this);
		getCommand("togglestaff").setExecutor(new ToggleStaffCommand());
		getCommand("staffmode").setExecutor(new StaffModeCommand());
		getCommand("vanish").setExecutor(new VanishCommand());
		getCommand("editstaff").setExecutor(new EditStaffCommand());

		Bukkit.getScheduler().runTaskTimerAsynchronously(lStaff.getInstance(), (task) -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!player.hasMetadata("modmode")) {
					continue;
				}

				String finalString = "";
				int online = Bukkit.hasWhitelist() ? Bukkit.getOnlinePlayers().size() : SpoofHandler.getSpoofedCount(Bukkit.getOnlinePlayers().size());
				if (player.hasPermission("lstaff.admin")) {
					finalString += "&6Build&7: &c" + (player.hasMetadata("Build") ? "&a&l✓" : "&c&l✗") + " &f&l| ";
				}
				finalString += "&6Staff Chat&7: &c" + (player.hasMetadata("staffchat") ? "&a&l✓" : "&c&l✗");
				finalString += " &f| &6Online&7: &b" + online + "/" + Bukkit.getMaxPlayers();
				finalString += " &f| &6Vanish&7: " + (player.hasMetadata("invisible") ? "&a&l✓" : "&c&l✗");

				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(CC.chat(finalString)));

			}
		}, 40, 40);

		loadMongo();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	private void loadMongo() {

		new MenuManager();

		if (getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
			mongoDatabase = new MongoClient(
					new ServerAddress(
							getConfig().getString("MONGO.HOST"),
							getConfig().getInt("MONGO.PORT")),
					MongoCredential.createCredential(
							getConfig().getString("MONGO.AUTHENTICATION.USERNAME"),
							"admin", getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()),
					MongoClientOptions.builder().build()
			).getDatabase("lStaff");
		} else {
			mongoDatabase = new MongoClient(getConfig().getString("MONGO.HOST"), getConfig().getInt("MONGO.PORT"))
					.getDatabase("lStaff");
		}
		Bukkit.getConsoleSender().sendMessage(CC.chat("&fSuccessfully connected the &gMongo Database"));
	}
}
