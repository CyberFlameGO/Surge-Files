package dev.lbuddyboy.samurai.custom.airdrop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import dev.lbuddyboy.samurai.util.ItemStackSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 24/02/2022 / 7:58 PM
 * SteelHCF-master / dev.lbuddyboy.samurai.custom.airdrop
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AirDropReward {

	private int id;
	private String displayName;
	private ItemStack stack;
	private double chance;
	private String command;
	private boolean broadcast;

	public AirDropReward(ItemStack stack, int id, String displayName) {
		this.stack = stack;
		this.id = id;
		this.displayName = displayName;
		this.chance = 50.0;
		this.command = "";
		this.broadcast = false;
	}

	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<>();

		map.put("name", this.displayName);
		map.put("item", ItemStackSerializer.itemStackArrayToBase64(new ItemStack[]{this.stack}));
		map.put("chance", this.chance);
		map.put("command", this.command);
		map.put("broadcast", this.broadcast);

		return map;
	}

	public static AirDropReward deserialize(Map<String, Object> map) {

		AirDropReward reward = new AirDropReward();
		reward.setDisplayName((String) map.get("name"));
		reward.setStack(ItemStackSerializer.itemStackArrayFromBase64((String) map.get("item"))[0]);
		reward.setChance((Double) map.get("chance"));
		reward.setCommand((String) map.get("command"));
		reward.setBroadcast((Boolean) map.get("broadcast"));

		return reward;
	}

}
