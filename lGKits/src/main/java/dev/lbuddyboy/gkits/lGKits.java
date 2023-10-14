package dev.lbuddyboy.gkits;

import co.aikar.commands.PaperCommandManager;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import dev.lbuddyboy.gkits.api.FoxtrotAPI;
import dev.lbuddyboy.gkits.api.GKitAPI;
import dev.lbuddyboy.gkits.armorsets.ArmorSet;
import dev.lbuddyboy.gkits.armorsets.impl.DistanceArcherArmorSet;
import dev.lbuddyboy.gkits.armorsets.impl.SamuraiArmorSet;
import dev.lbuddyboy.gkits.armorsets.impl.ShogunArmorSet;
import dev.lbuddyboy.gkits.command.*;
import dev.lbuddyboy.gkits.command.completions.KitCompletion;
import dev.lbuddyboy.gkits.command.contexts.KitContext;
import dev.lbuddyboy.gkits.enchanter.EnchanterManager;
import dev.lbuddyboy.gkits.enchanter.command.EnchanterCommand;
import dev.lbuddyboy.gkits.enchants.CustomEnchantListener;
import dev.lbuddyboy.gkits.enchants.CustomEnchantManager;
import dev.lbuddyboy.gkits.enchants.event.ArmorListener;
import dev.lbuddyboy.gkits.listener.UserListener;
import dev.lbuddyboy.gkits.object.User;
import dev.lbuddyboy.gkits.object.kit.GKit;
import dev.lbuddyboy.gkits.thread.ArmorUpdateThread;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.Config;
import dev.lbuddyboy.gkits.util.YamlDoc;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 1:01 PM
 * GKits / me.lbuddyboy.gkits
 */

@Getter
public class lGKits extends JavaPlugin {

	@Getter
	private static lGKits instance;

	private YamlDoc usersYML;
	private YamlDoc enchantsYML;
	private Map<String, GKit> gKits;
	private MongoDatabase mongoDatabase;
	private CustomEnchantManager customEnchantManager;
	private EnchanterManager enchanterManager;
	private PaperCommandManager commandManager;
	private File kitsFolder;
	private final List<ArmorSet> armorSets = Arrays.asList(
			new DistanceArcherArmorSet(),
			new SamuraiArmorSet(),
			new ShogunArmorSet()
	);
	private API api;
	private ExecutorService executorService = Executors.newFixedThreadPool(15);
	private Config messageConfig;

	@SneakyThrows
	@Override
	public void onEnable() {
		instance = this;
		this.gKits = new HashMap<>();
		this.saveDefaultConfig();

		this.usersYML = new YamlDoc(this.getDataFolder(), "users.yml");
		this.enchantsYML = new YamlDoc(this.getDataFolder(), "enchants.yml");
		this.messageConfig = new Config(this, "messages");

		this.commandManager = new PaperCommandManager(this);
		this.customEnchantManager = new CustomEnchantManager();
		this.enchanterManager = new EnchanterManager();

		this.loadCommands();
		this.loadListeners();
		this.loadGKits();
		this.loadMongo();

		armorSets.forEach(set -> getServer().getPluginManager().registerEvents(set, this));

		if (this.getServer().getPluginManager().getPlugin("Samurai") != null) {
			this.api = new FoxtrotAPI();
		} else {
			this.api = new GKitAPI();
		}

		new ArmorUpdateThread().start();
	}

	@Override
	public void onDisable() {
		User.getUsers().values().forEach(user -> user.save(false));
	}

	private void loadCommands() {
		this.commandManager.getCommandCompletions().registerCompletion("kits", new KitCompletion());
		this.commandManager.getCommandContexts().registerContext(GKit.class, new KitContext());
		this.commandManager.registerCommand(new GKitCommand());
		this.getCommand("armorsets").setExecutor(new ArmorSetsCommand());
		this.getCommand("givegem").setExecutor(new GiveGemCommand());
		this.getCommand("enchanter").setExecutor(new EnchanterCommand());
		this.getCommand("enchants").setExecutor(new EnchantsCommand());
		this.getCommand("combinedce").setExecutor(new CombinedCECommand());
	}

	private void loadListeners() {
		this.getServer().getPluginManager().registerEvents(new ArmorListener(Collections.emptyList()), this);
		this.getServer().getPluginManager().registerEvents(new UserListener(), this);
		this.getServer().getPluginManager().registerEvents(new CustomEnchantListener(), this);
	}

	public void loadGKits() {
		gKits.clear();

		File folder = new File(getDataFolder(), "kits");
		if (!folder.exists()) {
			folder.mkdirs();
		}

		this.kitsFolder = folder;

		for (String s : this.kitsFolder.list()) {
			this.gKits.put(s.replaceAll(".yml", ""), new GKit(new Config(this, s.replaceAll(".yml", ""), this.kitsFolder)));
		}
	}

	public GKit gkitByName(String name) {
		return this.gKits.get(name);
	}

	private void loadMongo() {
		if (lGKits.getInstance().getConfig().getBoolean("MONGO.USE")) {
			if (getConfig().getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
				mongoDatabase = new MongoClient(
						new ServerAddress(
								getConfig().getString("MONGO.HOST"),
								getConfig().getInt("MONGO.PORT")),
						MongoCredential.createCredential(
								getConfig().getString("MONGO.AUTHENTICATION.USERNAME"),
								"admin", getConfig().getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()),
						MongoClientOptions.builder().build()
				).getDatabase(getConfig().getString("MONGO.DATABASE"));
			} else {
				mongoDatabase = new MongoClient(getConfig().getString("MONGO.HOST"), getConfig().getInt("MONGO.PORT"))
						.getDatabase(getConfig().getString("MONGO.DATABASE"));
			}
			Bukkit.getConsoleSender().sendMessage(CC.translate("&aConnected to the Mongo Database (Not using YAML storage for gkit cooldowns)"));
		} else {
			Bukkit.getConsoleSender().sendMessage(CC.translate("&aUsing YAML Storage for gkit cooldowns."));
		}
	}

}
