package dev.lbuddyboy.pcore.battlepass;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.challenge.Challenge;
import dev.lbuddyboy.pcore.battlepass.challenge.ChallengeListener;
import dev.lbuddyboy.pcore.battlepass.challenge.category.ChallengeCategory;
import dev.lbuddyboy.pcore.battlepass.listener.BattlePassListener;
import dev.lbuddyboy.pcore.user.MineUser;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.IModule;
import dev.lbuddyboy.pcore.util.ItemUtils;
import dev.lbuddyboy.pcore.util.YamlDoc;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

@Getter
public class BattlePassHandler implements IModule {

    private final List<ChallengeCategory> challengeCategories;
    private final List<Challenge> challenges;
    private final Map<Integer, Tier> tiers;
    private final YamlDoc battlePassYML;
//    private final PagedGUISettings rewardsSettings, challengeCategorySettings, challengesSettings;

    private File challengesFolder, tiersFolder;

    public BattlePassHandler() {
        this.tiers = new HashMap<>();
        this.challengeCategories = new ArrayList<>();
        this.challenges = new ArrayList<>();
        this.battlePassYML = new YamlDoc(pCore.getInstance().getDataFolder(), "battle-pass.yml");
    }

    @Override
    public void load(pCore plugin) {
        this.loadDirectories();
        this.loadCategories();
        this.loadChallenges();
        this.loadTiers();
        this.loadDefaultChallenges();
        this.loadDefaultTiers();
        this.loadListeners();
    }

    @Override
    public void unload(pCore plugin) {
        Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).forEach(uuid -> saveBattlePass(uuid, false));
    }

    @Override
    public void save() {
        Bukkit.getOnlinePlayers().stream().map(Player::getUniqueId).forEach(uuid -> saveBattlePass(uuid, false));
    }

    private void loadDirectories() {
        this.challengesFolder = new File(pCore.getInstance().getDataFolder(), "challenges");
        this.tiersFolder = new File(pCore.getInstance().getDataFolder(), "tiers");

        if (!this.challengesFolder.exists()) this.challengesFolder.mkdir();
        if (!this.tiersFolder.exists()) this.tiersFolder.mkdir();
    }

    private void loadCategories() {
        ConfigurationSection section = this.battlePassYML.gc().getConfigurationSection("categories");
        for (String key : section.getKeys(false)) {
            this.challengeCategories.add(new ChallengeCategory(
                    key,
                    section.getString(key + ".display-name"),
                    ItemUtils.itemStackFromConfigSect(key + ".display-item", section),
                    section.getBoolean(key + ".active")
            ));
        }
        Bukkit.getConsoleSender().sendMessage("[BattlePass Handler] Loaded " + this.challengeCategories.size() + " challenge categories.");
    }

    private void loadChallenges() {
        for (String key : this.challengesFolder.list()) {
            String name = key.replaceAll(".yml", "");

            this.challenges.add(new Challenge(new Config(pCore.getInstance(), name, this.challengesFolder)));
        }
        Bukkit.getConsoleSender().sendMessage("[BattlePass Handler] Loaded " + this.challenges.size() + " challenges.");
    }

    private void loadTiers() {
        for (String key : this.tiersFolder.list()) {
            String name = key.replaceAll(".yml", "");
            Tier tier = new Tier(name);

            this.tiers.put(tier.getNumber(), tier);
        }
        Bukkit.getConsoleSender().sendMessage("[BattlePass Handler] Loaded " + this.tiers.size() + " tiers.");
    }

    private void loadDefaultChallenges() {
        if (!this.challenges.isEmpty()) return;

        this.challenges.add(new Challenge(new Config(pCore.getInstance(), "kill_5_zombies", this.challengesFolder)));
        this.challenges.add(new Challenge(new Config(pCore.getInstance(), "kill_10_zombies", this.challengesFolder)));
        this.challenges.add(new Challenge(new Config(pCore.getInstance(), "mine_10_dirt", this.challengesFolder)));
        this.challenges.add(new Challenge(new Config(pCore.getInstance(), "mine_10_grass", this.challengesFolder)));
        this.challenges.add(new Challenge(new Config(pCore.getInstance(), "place_10_stone", this.challengesFolder)));
    }

    private void loadDefaultTiers() {
        if (!this.tiers.isEmpty()) return;

        this.tiers.put(1, new Tier(new Config(pCore.getInstance(), "1", this.tiersFolder)));
        this.tiers.put(2, new Tier(new Config(pCore.getInstance(), "2", this.tiersFolder)));
        this.tiers.put(3, new Tier(new Config(pCore.getInstance(), "3", this.tiersFolder)));
        this.tiers.put(4, new Tier(new Config(pCore.getInstance(), "4", this.tiersFolder)));
        this.tiers.put(5, new Tier(new Config(pCore.getInstance(), "5", this.tiersFolder)));
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new BattlePassListener(), pCore.getInstance());
        Bukkit.getPluginManager().registerEvents(new ChallengeListener(), pCore.getInstance());
    }

    @Override
    public void reload() {
        this.battlePassYML.reloadConfig();
        this.loadDirectories();
        this.tiers.clear();
        this.challenges.clear();
        this.challengeCategories.clear();

        this.loadCategories();
        this.loadChallenges();
        this.loadDefaultChallenges();
        this.loadTiers();
        this.loadDefaultTiers();
    }

    public BattlePass loadBattlePass(UUID uuid, boolean async) {
        MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);

        if (player.getBattlePass() != null) return player.getBattlePass();
        BattlePass pass = new BattlePass(0, 0);

        player.setBattlePass(pass);
        player.flagUpdate();

        return pass;
    }

    public ChallengeCategory getChallengeCategory(String name) {
        return this.challengeCategories.stream().filter(c -> c.getName().equals(name)).findFirst().get();
    }

    public BattlePass loadBattlePass(UUID uuid) {
        return loadBattlePass(uuid, false);
    }

    public void saveBattlePass(UUID uuid, boolean async) {
        saveBattlePass(uuid, loadBattlePass(uuid, async), async);
    }

    public void saveBattlePass(UUID uuid, BattlePass pass, boolean async) {
        MineUser player = pCore.getInstance().getMineUserHandler().tryMineUserAsync(uuid);
        player.setBattlePass(pass);
        player.flagUpdate();
    }

    public Tier getTier(int number) {
        return this.tiers.get(number);
    }

}
