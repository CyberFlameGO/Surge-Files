package dev.aurapvp.samurai.player;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import dev.aurapvp.samurai.Samurai;
import dev.aurapvp.samurai.battlepass.BattlePass;
import dev.aurapvp.samurai.essential.kit.KitCooldown;
import dev.aurapvp.samurai.essential.offline.OfflineData;
import dev.aurapvp.samurai.essential.rollback.PlayerDeath;
import dev.aurapvp.samurai.util.gson.GSONUtils;
import lombok.Data;
import org.bson.Document;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class SamuraiPlayer {

    private final UUID uniqueId;
    private String name;

    private int kills, deaths, killStreak, highestKillStreak, coinFlipWins, coinFlipLosses;

    private List<PlayerDeath> playerKills = new ArrayList<>();
    private List<PlayerDeath> playerDeaths = new ArrayList<>();
    private List<KitCooldown> kitCooldowns = new ArrayList<>();
    private ItemStack[] enderChest = new ItemStack[27];
    private OfflineData offlineData;
    private BattlePass battlePass;

    private transient boolean updated;

    public SamuraiPlayer(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public SamuraiPlayer(Document document) {
        this.uniqueId = UUID.fromString(document.getString("uniqueId"));
        this.name = document.getString("name");

        if (document.containsKey("playerKills")) {
            JsonArray array = JsonParser.parseString(document.getString("playerKills")).getAsJsonArray();
            array.forEach(element -> playerKills.add(PlayerDeath.deserialize(element.getAsJsonObject())));
        }

        if (document.containsKey("playerDeaths")) {
            JsonArray array = JsonParser.parseString(document.getString("playerDeaths")).getAsJsonArray();
            array.forEach(element -> playerDeaths.add(PlayerDeath.deserialize(element.getAsJsonObject())));
        }

        if (document.containsKey("kitCooldowns")) this.kitCooldowns = GSONUtils.getGSON().fromJson(document.getString("kitCooldowns"), GSONUtils.KIT_COOLDOWNS);
        if (document.containsKey("enderChest")) this.enderChest = GSONUtils.getGSON().fromJson(document.getString("enderChest"), ItemStack[].class);
        if (document.containsKey("offlineData")) this.offlineData = OfflineData.deserialize(JsonParser.parseString(document.getString("offlineData")).getAsJsonObject());
        if (document.containsKey("battlePass")) this.battlePass = BattlePass.deserialize(JsonParser.parseString(document.getString("battlePass")).getAsJsonObject());
    }

    public void save(boolean async) {
        Samurai.getInstance().getStorageHandler().getStorage().updatePlayer(this.uniqueId, this, async);
        this.updated = false;
    }

    public void updated() {
        this.updated = true;
    }

    public boolean wasUpdated() {
        return this.updated;
    }

    public Document serialize() {
        Document document = new Document();

        document.put("uniqueId", this.uniqueId.toString());
        document.put("name", this.name);
        document.put("kills", this.kills);
        document.put("deaths", this.deaths);
        document.put("killStreak", this.killStreak);
        document.put("highestKillStreak", this.highestKillStreak);
        document.put("coinFlipWins", this.coinFlipWins);
        document.put("coinFlipLosses", this.coinFlipLosses);

        if (!this.playerKills.isEmpty()) {
            JsonArray array = new JsonArray();
            this.playerKills.forEach(kill -> array.add(kill.serialize()));
            document.put("playerKills", array.toString());
        }

        if (!this.playerDeaths.isEmpty()) {
            JsonArray array = new JsonArray();
            this.playerDeaths.forEach(death -> array.add(death.serialize()));
            document.put("playerDeaths", array.toString());
        }

        document.put("kitCooldowns", GSONUtils.getGSON().toJson(this.kitCooldowns, GSONUtils.KIT_COOLDOWNS));
        document.put("enderChest", GSONUtils.getGSON().toJson(this.enderChest, ItemStack[].class));
        document.put("offlineData", this.offlineData.serialize().toString());
        document.put("battlePass", this.battlePass.serialize().toString());

        return document;
    }

    public static SamuraiPlayer deserialize(Document document) {
        return new SamuraiPlayer(document);
    }

}
