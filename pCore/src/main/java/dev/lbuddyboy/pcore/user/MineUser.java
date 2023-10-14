package dev.lbuddyboy.pcore.user;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lbuddyboy.pcore.api.SamuraiAPI;
import dev.lbuddyboy.pcore.battlepass.BattlePass;
import dev.lbuddyboy.pcore.economy.BankAccount;
import dev.lbuddyboy.pcore.essential.kit.KitCooldown;
import dev.lbuddyboy.pcore.essential.offline.OfflineData;
import dev.lbuddyboy.pcore.essential.plot.PrivatePlot;
import dev.lbuddyboy.pcore.essential.rollback.PlayerDeath;
import dev.lbuddyboy.pcore.mines.PrivateMine;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.robots.pRobot;
import dev.lbuddyboy.pcore.storage.impl.MongoStorage;
import dev.lbuddyboy.pcore.user.model.CoinFlipInfo;
import dev.lbuddyboy.pcore.user.model.KeyInfo;
import dev.lbuddyboy.pcore.user.model.RankInfo;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
public class MineUser {

    private static JsonParser PARSER = new JsonParser();

    private final UUID uuid;
    private String name;

    private BankAccount bankAccount;
    private CoinFlipInfo coinFlipInfo;
    private PrivateMine privateMine;
    private PrivatePlot plot;

    private int kills, deaths, killStreak, highestKillStreak;

    private List<PlayerDeath> playerKills = new ArrayList<>();
    private List<PlayerDeath> playerDeaths = new ArrayList<>();
    private List<KitCooldown> kitCooldowns = new ArrayList<>();
    private ItemStack[] enderChest = new ItemStack[27];
    private KeyInfo keyInfo = new KeyInfo();
    private OfflineData offlineData;
    private BattlePass battlePass;
    private Map<Integer, ItemStack[]> playerVaults = new HashMap<>();

    private boolean updated;
    private long offlineTime = 0;

    public MineUser(UUID uuid) {
        this.uuid = uuid;
        this.coinFlipInfo = new CoinFlipInfo();

        System.out.println("Creating new mine profile for " + uuid + ".");
    }

    public MineUser(JsonObject object) {
        this.uuid = UUID.fromString(object.get("uuid").getAsString());

        if (object.has("name")) this.name = object.get("name").getAsString();
        if (object.has("kills")) this.kills = object.get("kills").getAsInt();
        if (object.has("deaths")) this.deaths = object.get("deaths").getAsInt();
        if (object.has("killStreak")) this.killStreak = object.get("killStreak").getAsInt();
        if (object.has("highestKillStreak")) this.highestKillStreak = object.get("highestKillStreak").getAsInt();

        if (object.has("coinFlipInfo")) this.coinFlipInfo = GSONUtils.getGSON().fromJson(object.get("coinFlipInfo").getAsString(), GSONUtils.CF_INFO);
        if (object.has("bankAccount")) this.bankAccount = BankAccount.deserialize(PARSER.parse(object.get("bankAccount").getAsString()).getAsJsonObject());
        if (object.has("keyInfo")) this.keyInfo = KeyInfo.deserialize(PARSER.parse(object.get("keyInfo").getAsString()).getAsJsonObject());
        if (object.has("privateMine")) this.privateMine = PrivateMine.deserialize(PARSER.parse(object.get("privateMine").getAsString()).getAsJsonObject());
        if (object.has("plot")) this.plot = PrivatePlot.deserialize(PARSER.parse(object.get("plot").getAsString()).getAsJsonObject());

        if (object.has("playerVaults")) {
            JsonObject vaults = object.get("playerVaults").getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : vaults.entrySet()) {
                this.playerVaults.put(Integer.parseInt(entry.getKey()), GSONUtils.getGSON().fromJson(vaults.get(entry.getKey()).getAsString(), ItemStack[].class));
            }
        }

        if (object.has("playerKills")) {
            JsonArray array = new JsonParser().parse(object.get("playerKills").getAsString()).getAsJsonArray();
            array.forEach(element -> playerKills.add(PlayerDeath.deserialize(element.getAsJsonObject())));
        }

        if (object.has("playerDeaths")) {
            JsonArray array = new JsonParser().parse(object.get("playerDeaths").getAsString()).getAsJsonArray();
            array.forEach(element -> playerDeaths.add(PlayerDeath.deserialize(element.getAsJsonObject())));
        }

        if (object.has("kitCooldowns")) {
            JsonArray array = new JsonParser().parse(object.get("kitCooldowns").getAsString()).getAsJsonArray();
            array.forEach(element -> kitCooldowns.add(KitCooldown.deserialize(element.getAsJsonObject())));
        }

        if (object.has("enderChest")) this.enderChest = GSONUtils.getGSON().fromJson(object.get("enderChest").getAsString(), ItemStack[].class);
        if (object.has("offlineData")) this.offlineData = OfflineData.deserialize(new JsonParser().parse(object.get("offlineData").getAsString()).getAsJsonObject());
        if (object.has("battlePass")) this.battlePass = BattlePass.deserialize(new JsonParser().parse(object.get("battlePass").getAsString()).getAsJsonObject());

    }

    public void save() {
        save(true);
    }

    public void save(boolean async) {
        this.updated = false;
        pCore.getInstance().getStorageHandler().getStorage().saveMineUser(this.uuid, this, async);
    }

    public ItemStack[] getPlayerVaultOrDefault(Integer page) {
        return this.playerVaults.getOrDefault(page, new ItemStack[54]);
    }

    public void updatePlayerVault(Integer page, ItemStack[] contents) {
        this.playerVaults.put(page, contents);
        this.flagUpdate();
    }

    public void flagOffline() {
        this.offlineTime = System.currentTimeMillis() + 60_000L;
    }

    public void flagUpdate() {
        if (this.updated) return;

        this.updated = true;
        System.out.println("Flagged an update for " + uuid + ".");
    }

    public boolean isRemovable() {
        return this.offlineTime - System.currentTimeMillis() <= 0 && this.offlineTime > 0;
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("uuid", uuid.toString());
        object.addProperty("name", name);
        object.addProperty("coinFlipInfo", GSONUtils.getGSON().toJson(this.coinFlipInfo, GSONUtils.CF_INFO));

        if (this.bankAccount != null) object.addProperty("bankAccount", this.bankAccount.serialize().toString());
        if (this.keyInfo != null) object.addProperty("keyInfo", this.keyInfo.serialize().toString());
        if (this.privateMine != null) object.addProperty("privateMine", this.privateMine.serialize().toString());
        if (this.plot != null) object.addProperty("plot", this.plot.serialize().toString());

        object.addProperty("name", this.name);
        object.addProperty("kills", this.kills);
        object.addProperty("deaths", this.deaths);
        object.addProperty("killStreak", this.killStreak);
        object.addProperty("highestKillStreak", this.highestKillStreak);

        if (!this.playerVaults.isEmpty()) {
            JsonObject vaults = new JsonObject();
            for (Map.Entry<Integer, ItemStack[]> entry : this.playerVaults.entrySet()) {
                vaults.addProperty(String.valueOf(entry.getKey()), GSONUtils.getGSON().toJson(entry.getValue(), ItemStack[].class));
            }
            object.add("playerVaults", vaults);
        }

        if (!this.playerKills.isEmpty()) {
            JsonArray array = new JsonArray();
            this.playerKills.forEach(kill -> array.add(kill.serialize()));
            object.addProperty("playerKills", array.toString());
        }

        if (!this.playerDeaths.isEmpty()) {
            JsonArray array = new JsonArray();
            this.playerDeaths.forEach(death -> array.add(death.serialize()));
            object.addProperty("playerDeaths", array.toString());
        }

        if (!this.kitCooldowns.isEmpty()) {
            JsonArray array = new JsonArray();
            this.kitCooldowns.forEach(cooldown -> array.add(cooldown.serialize()));
            object.addProperty("kitCooldowns", array.toString());
        }

        object.addProperty("enderChest", GSONUtils.getGSON().toJson(this.enderChest, ItemStack[].class));

        if (this.offlineData != null) object.addProperty("offlineData", this.offlineData.serialize().toString());
        if (this.battlePass != null) object.addProperty("battlePass", this.battlePass.serialize().toString());

        return object;
    }

    public static MineUser deserialize(JsonObject object) {
        return new MineUser(object);
    }

}
