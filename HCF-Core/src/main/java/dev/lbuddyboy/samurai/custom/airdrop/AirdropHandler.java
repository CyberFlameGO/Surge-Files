package dev.lbuddyboy.samurai.custom.airdrop;

import dev.lbuddyboy.samurai.custom.airdrop.listener.AirdropListener;
import dev.lbuddyboy.samurai.util.ItemBuilder;
import lombok.Getter;
import dev.lbuddyboy.samurai.Samurai;
import dev.lbuddyboy.samurai.util.ItemUtils;
import dev.lbuddyboy.samurai.util.object.YamlDoc;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 24/02/2022 / 7:32 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.airdrop
 */

@Getter
public class AirdropHandler {

	public static String PREFIX = CC.translate("&3&l[AIRDROP]");

	private List<AirDropReward> lootTable;
	private ItemStack item;
	private final YamlDoc config;

	public AirdropHandler() {
		this.lootTable = new ArrayList<>();
		this.config = new YamlDoc(Samurai.getInstance().getDataFolder(), "airdrop.yml");

		reload();
		Samurai.getInstance().getServer().getPluginManager().registerEvents(new AirdropListener(), Samurai.getInstance());
	}

	public void reload() {
		this.lootTable.clear();

		for (String key : getConfig().getConfigurationSection("loot").getKeys(false)) {
			Map<String, Object> keys = new HashMap<>();

			for (String loot : getConfig().getConfigurationSection("loot." + key).getKeys(false)) {
				keys.put(loot, getConfig().get("loot." + key + "." + loot));
			}

			this.lootTable.add(AirDropReward.deserialize(keys));
		}

		List<String> lore = new ArrayList<>(getConfig().getStringList("item.lore"));

		for (AirDropReward reward : this.lootTable) {
			lore.add(" &7» " + reward.getDisplayName());
		}

		lore.add(" ");

		ItemBuilder builder = ItemBuilder.of(Material.getMaterial(getConfig().getString("item.material")))
				.name(CC.translate(getConfig().getString("item.name")))
				.setLore(CC.translate(lore));

		this.item = builder.build();
	}

	public void save() {
		int i = 1;
		this.getConfig().set("loot", null);

		for (AirDropReward reward : this.lootTable) {
			for (Map.Entry<String, Object> entry : reward.serialize().entrySet()) {
				this.getConfig().set("loot." + reward.getId() + "." + entry.getKey(), entry.getValue());
			}
		}
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void saveLoot(List<ItemStack> stacks) {
		int i = 1;
		this.getConfig().set("loot", null);
		this.lootTable.clear();

		for (ItemStack stack : stacks) {
			if (stack != null && !stack.getType().equals(Material.AIR) || stack != null) {
				this.lootTable.add(new AirDropReward(stack, i++, ItemUtils.getName(stack)));
			}
		}

		for (AirDropReward reward : this.lootTable) {
			for (Map.Entry<String, Object> entry : reward.serialize().entrySet()) {
				this.getConfig().set("loot." + reward.getId() + "." + entry.getKey(), entry.getValue());
			}
		}
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(ItemStack stack) {
		int i = this.lootTable.size();
		if (stack != null && !stack.getType().equals(Material.AIR) || stack != null) {
			this.lootTable.add(new AirDropReward(stack, ++i, ItemUtils.getName(stack)));
		}

		for (AirDropReward reward : this.lootTable) {
			for (Map.Entry<String, Object> entry : reward.serialize().entrySet()) {
				this.getConfig().set("loot." + reward.getId() + "." + entry.getKey(), entry.getValue());
			}
		}

		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addLoot(List<ItemStack> stacks) {
		int i = 1;
		for (ItemStack stack : stacks) {
			if (stack != null && !stack.getType().equals(Material.AIR) || stack != null) {
				this.lootTable.add(new AirDropReward(stack, i++, ItemUtils.getName(stack)));
			}
		}

		for (AirDropReward reward : this.lootTable) {
			for (Map.Entry<String, Object> entry : reward.serialize().entrySet()) {
				this.getConfig().set("loot." + reward.getId() + "." + entry.getKey(), entry.getValue());
			}
		}
		try {
			this.config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public YamlConfiguration getConfig() {
		return this.config.gc();
	}


}
