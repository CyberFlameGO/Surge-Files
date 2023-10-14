package dev.lbuddyboy.samurai.commands.menu.playtime;

import lombok.Getter;
import lombok.SneakyThrows;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.YamlDoc;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/12/2021 / 2:18 PM
 * SteelHCF-main / com.steelpvp.hcf.user.playtime
 */

@Getter
public class PlayTimeRewardsManager {

	private final List<PlayTimeReward> playTimeRewards;
	private final YamlDoc config;

	@SneakyThrows
	public PlayTimeRewardsManager() {
		this.playTimeRewards = new ArrayList<>();
		this.config = new YamlDoc(Samurai.getInstance().getDataFolder(), "playtime-rewards.yml");
		this.config.init();

		reload();
	}

	@SneakyThrows
	public void reload() {
		this.config.reloadConfig();
		this.playTimeRewards.clear();
		for (String key : this.config.gc().getConfigurationSection("rewards").getKeys(false)) {
			String abs = "rewards." + key + ".";
			PlayTimeReward reward = (new PlayTimeReward(key,
					this.config.gc().getString(abs + "playtime-needed"),
					this.config.gc().getString(abs + "name"),
					this.config.gc().getStringList(abs + "lore"),
					Material.valueOf(this.config.gc().getString(abs + "material")),
					(short) this.config.gc().getInt(abs + "data"),
					this.config.gc().getStringList(abs + "commands")));
			reward.loadFromRedis();
			this.playTimeRewards.add(reward);
		}

		Bukkit.getConsoleSender().sendMessage(CC.translate("&fLoaded in &b" + this.playTimeRewards.size() + " Playtime Rewards"));
	}

}
