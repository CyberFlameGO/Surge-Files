package dev.lbuddyboy.samurai.commands.menu.playtime;

import lombok.Getter;
import dev.lbuddyboy.samurai.persist.PersistMap;
import dev.lbuddyboy.samurai.util.CC;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 16/12/2021 / 2:18 PM
 * SteelHCF-main / com.steelpvp.hcf.user.playtime
 */

@Getter
public class PlayTimeReward extends PersistMap<Boolean> {

	private String keyName;
	private String playtimeRequired;
	private String displayName;
	private List<String> lore;
	private Material material;
	private short data;
	private List<String> commands;

	public ItemStack buildItem() {
		ItemStack stack = new ItemBuilder(this.material).displayName(CC.translate(this.displayName)).lore(CC.translate(this.lore)).data(this.data).build();
		ItemMeta meta = stack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS);
		stack.setItemMeta(meta);
		return stack;
	}

	public PlayTimeReward(String keyName, String playtimeRequired, String displayName, List<String> lore, Material material, short data, List<String> commands) {
		super("PlaytimeReward-" + keyName, "PlaytimeReward-" + keyName);
		this.keyName = keyName;
		this.playtimeRequired = playtimeRequired;
		this.displayName = displayName;
		this.lore = lore;
		this.material = material;
		this.data = data;
		this.commands = commands;
	}

	@Override
	public String getRedisValue(Boolean toggled){
		return (String.valueOf(toggled));
	}

	@Override
	public Boolean getJavaObject(String str){
		return (Boolean.valueOf(str));
	}

	@Override
	public Object getMongoValue(Boolean toggled) {
		return (toggled);
	}

	public void setClaimed(UUID update, boolean toggled) {
		updateValueAsync(update, toggled);
	}

	public boolean hasClaimed(UUID check) {
		return (contains(check) ? getValue(check) : false);
	}

}
