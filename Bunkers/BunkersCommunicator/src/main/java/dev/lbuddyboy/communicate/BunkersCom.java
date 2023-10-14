package dev.lbuddyboy.communicate;

import dev.lbuddyboy.communicate.database.mongo.MongoHandler;
import dev.lbuddyboy.communicate.database.redis.RedisHandler;
import dev.lbuddyboy.communicate.profile.ProfileHandler;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 14/04/2022 / 4:16 PM
 * BunkersCommunicator / PACKAGE_NAME
 */

@Getter
public class BunkersCom extends JavaPlugin {

	@Getter private static BunkersCom instance;
	@Setter private List<BunkersGame> bunkersGames;
	private MongoHandler mongoHandler;
	private RedisHandler redisHandler;
	private ProfileHandler profileHandler;

	@Override
	public void onEnable() {
		instance = this;

		this.saveDefaultConfig();

		this.bunkersGames = new ArrayList<>();
		this.mongoHandler = new MongoHandler();
		this.redisHandler = new RedisHandler(this);
		this.profileHandler = new ProfileHandler();

	}
}
