package dev.lbuddyboy.pcore.battlepass.challenge;

import dev.lbuddyboy.pcore.pCore;
import dev.lbuddyboy.pcore.battlepass.challenge.category.ChallengeCategory;
import dev.lbuddyboy.pcore.util.Config;
import dev.lbuddyboy.pcore.util.ItemUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class Challenge {

    private final Config file;

    public Challenge(Config file) {
        this.file = file;

        load();
    }

    private String displayName;
    private ChallengeCategory category;
    private ChallengeTypes challengeType;
    private Object value;
    private int neededProgress, xp;
    private ItemStack displayItem;

    public void load() {
        FileConfiguration config = this.file;

        this.displayName = config.getString("display-name");
        this.category = pCore.getInstance().getBattlePassHandler().getChallengeCategory(config.getString("category"));
        this.challengeType = ChallengeTypes.valueOf(config.getString("challenge-type"));
        this.value = config.get("value");
        this.xp = config.getInt("xp");
        this.neededProgress = config.getInt("neededProgress");
        this.displayItem = ItemUtils.itemStackFromConfigSect("display-item", config);
    }

    public void save() {
        FileConfiguration config = this.file;

        config.set("display-name", this.displayName);
        config.set("category", this.category.getName());
        config.set("challenge-type", this.challengeType.name());
        config.set("value", this.value);
        config.set("xp", this.xp);
        config.set("neededProgress", this.neededProgress);
        ItemUtils.itemStackToConfigSect(this.displayItem, -1, "display-item", config);

        this.file.save();
    }


}
