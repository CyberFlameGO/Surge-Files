package dev.lbuddyboy.pcore.battlepass;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.challenge.Challenge;
import dev.lbuddyboy.pcore.util.gson.GSONUtils;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BattlePass {

    private int experience = 0, tier = 0;
    private List<String> claimedTiers = new ArrayList<>();
    private Map<String, Integer> progress = new HashMap<>();

    public BattlePass(int experience, int level) {
        this.experience = experience;
        this.tier = level;
    }

    public BattlePass(int experience, int level, List<String> claimedTiers, Map<String, Integer> progress) {
        this.experience = experience;
        this.tier = level;
        this.claimedTiers = claimedTiers;
        this.progress = progress;
    }

    public Tier getCurrentTier() {
        if (tier == 0) return null;

        return pCore.getInstance().getBattlePassHandler().getTier(this.tier);
    }

    public boolean isComplete(Challenge challenge) {
        return getProgress(challenge) >= challenge.getNeededProgress();
    }

    public int getProgress(Challenge challenge) {
        return this.progress.getOrDefault(challenge.getFile().getFileName(), 0);
    }

    public boolean progress(Challenge challenge, int progress) {
        this.progress.put(challenge.getFile().getFileName(), progress);

        return isComplete(challenge);
    }

    public boolean isPremium(Player player) {
        return player.hasPermission("samurai.battlepass");
    }

    public JsonObject serialize() {
        JsonObject object = new JsonObject();

        object.addProperty("experience", this.experience);
        object.addProperty("tier", this.tier);
        object.addProperty("claimedTiers", GSONUtils.getGSON().toJson(this.claimedTiers, GSONUtils.STRING));


        for (Map.Entry<String, Integer> entry : progress.entrySet()) {
            object.addProperty("PROGRESS-" + entry.getKey(), entry.getValue());
        }

        return object;
    }

    public static BattlePass deserialize(JsonObject object) {
        return new BattlePass(object.get("experience").getAsInt(),
                object.get("tier").getAsInt(),
                GSONUtils.getGSON().fromJson(object.get("claimedTiers").getAsString(), GSONUtils.STRING),
                new HashMap<String, Integer>(){{
                    for (Entry<String, JsonElement> entry : object.entrySet()) {
                        if (!(entry.getKey().startsWith("PROGRESS-"))) continue;
                        String raw = entry.getKey().replaceAll("PROGRESS-", "");

                        put(raw, entry.getValue().getAsInt());
                    }
                }}
        );
    }

}
