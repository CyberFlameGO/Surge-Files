package dev.lbuddyboy.samurai.custom.battlepass;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.ReplaceOptions;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.Challenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.*;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitActiveKOTHChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitEndChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitGlowstoneMountain;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.impl.visit.VisitNetherChallenge;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.listener.ChallengeListeners;
import dev.lbuddyboy.samurai.custom.battlepass.challenge.serializer.ChallengeSerializer;
import dev.lbuddyboy.samurai.custom.battlepass.daily.DailyChallenges;
import dev.lbuddyboy.samurai.custom.battlepass.listener.BattlePassListeners;
import lombok.Getter;
import lombok.Setter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.custom.battlepass.tier.Tier;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.YamlDoc;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BattlePassHandler {

	public static final String CHAT_PREFIX = CC.translate("&g&l[SEASON PASS] ");
	private static final JsonWriterSettings JSON_WRITER_SETTINGS = JsonWriterSettings.builder().outputMode(JsonMode.RELAXED).build();
	private static final Gson GSON = new GsonBuilder().registerTypeAdapter(Challenge.class, new ChallengeSerializer()).serializeNulls().create();

	private Map<Integer, Tier> tiers = new HashMap<>();
	private Map<String, Challenge> challenges = new HashMap<>();
	@Getter private DailyChallenges dailyChallenges;

	private MongoCollection<Document> mongoCollection = Samurai.getInstance().getMongoPool().getDatabase(Samurai.MONGO_DB_NAME).getCollection("BattlePass");
	private Map<UUID, BattlePassProgress> progress = new HashMap<>();
	private YamlDoc config;

	@Getter @Setter private boolean adminDisabled = false;

	public BattlePassHandler() {
		this.config = new YamlDoc(Samurai.getInstance().getDataFolder(), "battlepass.yml");
		loadTiers();
		loadChallenges();
		loadDailyChallenges();

		Bukkit.getServer().getPluginManager().registerEvents(new BattlePassListeners(), Samurai.getInstance());
		Bukkit.getServer().getPluginManager().registerEvents(new ChallengeListeners(), Samurai.getInstance());

		new BukkitRunnable() {
			@Override
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					useProgress(player.getUniqueId(), progress -> {
						if (progress.isRequiresSave()) {
							saveProgress(progress);
						}
					});
				}

				saveDailyChallenges();
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 20L * 60L, 20L * 60L);

		new BukkitRunnable() {
			@Override
			public void run() {
				if (System.currentTimeMillis() >= dailyChallenges.getExpiresAt()) {
					generateNewDailyChallenges();
				}
			}
		}.runTaskTimerAsynchronously(Samurai.getInstance(), 20L * 5L, 20L * 5L);
	}

	public List<Tier> getTiers() {
		return new ArrayList<>(tiers.values());
	}

	public Tier getTier(int number) {
		return tiers.get(number);
	}

	public List<Tier> getSortedTiers() {
		return tiers.values().stream().sorted(Comparator.comparingInt(Tier::getRequiredExperience)).collect(Collectors.toList());
	}

	public Tier getFirstUnreachedTier(int experience) {
		List<Tier> tiers = getSortedTiers();

		for (Tier next : tiers) {
			if (experience < next.getRequiredExperience()) {
				return next;
			}
		}

		return tiers.stream().findFirst().orElse(null);
	}

	public int getMaxTier() {
		return 14;
	}

	public Collection<Challenge> getAllChallenges() {
		List<Challenge> challenges = new ArrayList<>();
		challenges.addAll(getChallenges());
		challenges.addAll(dailyChallenges.getChallenges());
		return challenges;
	}

	public Collection<Challenge> getChallenges() {
		return challenges.values();
	}

	public Challenge getChallenge(String id) {
		return challenges.get(id.toLowerCase());
	}

	public BattlePassProgress getProgress(UUID uuid) {
		return progress.get(uuid);
	}

	public void useProgress(Player player, Consumer<BattlePassProgress> use) {
		useProgress(player.getUniqueId(), use);
	}

	public void useProgress(UUID uuid, Consumer<BattlePassProgress> use) {
		BattlePassProgress progress = this.progress.get(uuid);
		if (progress != null) {
			use.accept(progress);
		}
	}

	public void loadProgress(UUID uuid) {
		BattlePassProgress data = fetchProgress(uuid);
		progress.put(uuid, data);
	}

	public BattlePassProgress getOrLoadProgress(UUID uuid) {
		if (progress.containsKey(uuid)) {
			return getProgress(uuid);
		} else {
			return fetchProgress(uuid);
		}
	}

	public BattlePassProgress fetchProgress(UUID uuid) {
		Document document = mongoCollection.find(new Document("uuid", uuid.toString())).first();

		BattlePassProgress progress;
		if (document == null) {
			progress = new BattlePassProgress(uuid, dailyChallenges.getIdentifier());
		} else {
			progress = GSON.fromJson(document.toJson(JSON_WRITER_SETTINGS), BattlePassProgress.class);
		}

		if (!progress.getDailyChallengesId().equals(dailyChallenges.getIdentifier())) {
			progress.resetDailyChallengeData(dailyChallenges.getIdentifier());
		}

		progress.fillDefaults();

		return progress;
	}

	public void saveProgress(BattlePassProgress progress) {
		Document document = Document.parse(GSON.toJson(progress));
		mongoCollection.replaceOne(new Document("uuid", progress.getUuid().toString()), document, new ReplaceOptions().upsert(true));
	}

	public void unloadProgress(UUID uuid) {
		BattlePassProgress data = progress.remove(uuid);
		if (data.isRequiresSave()) {
			saveProgress(data);
		}
	}

	public void clearProgress(UUID uuid) {
		mongoCollection.deleteOne(new Document("uuid", uuid.toString()));

		if (progress.containsKey(uuid)) {
			progress.put(uuid, new BattlePassProgress(uuid, dailyChallenges.getIdentifier()));
		}
	}

	public void wipe() {
		mongoCollection.drop();
		progress.clear();

		for (Player player : Bukkit.getOnlinePlayers()) {
			progress.put(player.getUniqueId(), new BattlePassProgress(player.getUniqueId(), dailyChallenges.getIdentifier()));
		}
	}

	public void checkCompletionsAsync(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				checkCompletions(player);
			}
		}.runTaskAsynchronously(Samurai.getInstance());
	}

	public void checkCompletions(Player player) {
		useProgress(player.getUniqueId(), progress -> {
			if (progress.isPremium()) {
				for (Challenge challenge : getChallenges()) {
					if (progress.hasCompletedChallenge(challenge)) {
						continue;
					}

					if (challenge.meetsCompletionCriteria(player)) {
						challenge.completed(player, progress);
					}
				}
			}

			for (Challenge challenge : dailyChallenges.getChallenges()) {
				if (progress.hasCompletedChallenge(challenge)) {
					continue;
				}

				if (challenge.meetsCompletionCriteria(player)) {
					challenge.completed(player, progress);
				}
			}
		});
	}

	private void loadDailyChallenges() {
		File dailyChallengesFile = new File(Samurai.getInstance().getDataFolder(), "battle-pass-daily-challenges.json");
		if (dailyChallengesFile.exists()) {
			try (Reader reader = Files.newReader(dailyChallengesFile, Charsets.UTF_8)) {
				dailyChallenges = GSON.fromJson(reader, DailyChallenges.class);
			} catch (Exception e) {
				Samurai.getInstance().getLogger().severe("Failed to load BattlePass Daily Challenges!");
				e.printStackTrace();
			}
		} else {
			generateNewDailyChallenges();
		}
	}

	private void saveDailyChallenges() {
		try {
			Files.write(GSON.toJson(dailyChallenges), new File(Samurai.getInstance().getDataFolder(), "battle-pass-daily-challenges.json"), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateNewDailyChallenges() {
		dailyChallenges = new DailyChallenges();
		dailyChallenges.generate();

		for (Player player : Bukkit.getOnlinePlayers()) {
			useProgress(player.getUniqueId(), progress -> {
				progress.resetDailyChallengeData(dailyChallenges.getIdentifier());
			});
		}

		saveDailyChallenges();
	}

	public void loadTiers() {

		tiers.clear();

		List<Tier> tiersToAdd = new ArrayList<>();
		for (String key : this.config.gc().getConfigurationSection("tiers").getKeys(false)) {
			String abs = "tiers." + key + ".";
			Tier tier = new Tier(
					Integer.parseInt(key),
					this.config.gc().getInt(abs + "required-xp")
			);
			for (String string : this.config.gc().getStringList(abs + "rewards")) {
				String[] args = string.split(":");
				boolean premium = Boolean.parseBoolean(args[0]);
				String text = args[1];
				String command = args[2];
				tier.newReward(premium, reward -> reward.addText(CC.translate(text)).addCommand(command));
			}
			tiersToAdd.add(tier);
		}

		tiersToAdd.forEach(tier -> tiers.put(tier.getNumber(), tier));

	}

	private void loadChallenges() {
//		List<Challenge> challengesToAdd = new ArrayList<>();
//		for (String key : this.config.gc().getConfigurationSection("challenges").getKeys(false)) {
//			String abs = "challenges." + key + ".";
//			String type = this.config.gc().getString(abs + "type");
//			Challenge challenge = null;
//
//			if (type.toUpperCase().startsWith("MINE_BLOCK")) {
//				challenge = new MineBlockChallenge(key
//						, this.config.gc().getString(abs + "display")
//						, this.config.gc().getInt(abs + "reward"),
//						"",
//						"",
//						""
//
//				);
//			}
//
//			challengesToAdd.add(challenge);
//		}
		Arrays.asList(
				new OreChallenge("mine-50", "Mine 50 Diamonds", 5, false, OreChallenge.OreType.DIAMOND, 50),
				new OreChallenge("mine-100", "Mine 100 Diamonds", 10, false, OreChallenge.OreType.DIAMOND, 100),
				new OreChallenge("mine-250", "Mine 250 Diamonds", 15, false, OreChallenge.OreType.DIAMOND, 250),

				new ValuablesSoldChallenge("valuables-5000", "Sell $5,000 of Valuables", 10, false, 5000),

				new AttemptCaptureKOTHChallenge("attempt-cap-koth", "Attempt to Capture a KOTH", 10, false),

				new KillstreakChallenge("killstreak-3", "Reach a Killstreak of 3", 10, false, 3),
				new KillstreakChallenge("killstreak-5", "Reach a Killstreak of 5", 15, false, 5),

				new ArcherTagsChallenge("archer-tags-10", "Archer Tag 10 Players", 5, false, 10),
				new UsePetCandyChallenge("use-pet-candy", "Use Pet Candy", 5, false),
				new LevelUpPetChallenge("level-up-pet", "Level Up a Pet", 10, false),
				new LevelUpPetChallenge("use-pet", "Use a Pet", 10, false),

				new MakeFactionRaidableChallenge("make-fac-raidable", "Make a Team Raidable", 20, false),
//
				new UsePartnerItemChallenge("use-partner-items-3", "Use 3 Ability Items", 5, false, 3),
				new UsePartnerItemChallenge("use-partner-items-5", "Use 5 Ability Items", 10, false, 5),
				new UsePartnerItemChallenge("use-partner-items-10", "Use 10 Ability Items", 15, false, 10),

				new PlayTimeChallenge("playtime-1hr", "Reach 1 Hour of Play Time", 5, false, TimeUnit.HOURS.toMillis(1L)),
				new PlayTimeChallenge("playtime-3hr", "Reach 3 Hours of Play Time", 5, false, TimeUnit.HOURS.toMillis(3L)),
				new PlayTimeChallenge("playtime-6hr", "Reach 6 Hours of Play Time", 10, false, TimeUnit.HOURS.toMillis(6L)),
				new PlayTimeChallenge("playtime-12hr", "Reach 12 Hours of Play Time", 15, false, TimeUnit.HOURS.toMillis(12L)),

				new VisitNetherChallenge("visit-nether", "Visit the Nether", 5, false),
				new VisitEndChallenge("visit-end", "Visit the End", 5, false),
				new VisitActiveKOTHChallenge("visit-active-koth", "Visit an active KOTH", 10, false),
				new VisitGlowstoneMountain("visit-glowstone", "Visit the Glowstone Mountain", 10, false),

				new MineBlockChallenge("mine-sand-512", "Mine 512 Sand", 5, false, Material.SAND, 512),
				new MineLogChallenge("mine-logs-256", "Mine 256 Logs", 5, false, 256),
				new MineBlockChallenge("mine-glowstone-32", "Mine 32 Glowstone", 5, false, Material.GLOWSTONE, 32),

				new KillEntityChallenge("kill-players-10", "Kill 10 Players", 15, false, EntityType.PLAYER, 10),
				new KillEntityChallenge("kill-creepers-30", "Kill 30 Creepers", 10, false, EntityType.CREEPER, 30),
				new KillEntityChallenge("kill-endermen-20", "Kill 20 Endermen", 10, false, EntityType.ENDERMAN, 20),
				new KillEntityChallenge("kill-cows-30", "Kill 30 Cows", 5, false, EntityType.COW, 30),

				new MakeGemShopPurchaseChallenge("make-shard-shop-purchase", "Purchase An Item In Shard Shop", 10, false)
		).forEach(challenge -> challenges.put(challenge.getId(), challenge));
	}

}
