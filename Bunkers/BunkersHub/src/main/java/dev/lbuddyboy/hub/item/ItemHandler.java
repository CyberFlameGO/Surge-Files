package dev.lbuddyboy.hub.item;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.lModule;
import dev.lbuddyboy.hub.util.ItemBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 9:02 PM
 * LBuddyBoy-Development / me.lbuddyboy.hub.item
 */

@Getter
public class ItemHandler implements lModule {

	private final List<Item> items;

	public ItemHandler() {
		items = new ArrayList<>();
	}

	@SneakyThrows
	public void save(String key, int slot, ItemStack stack) {
		YamlConfiguration config = lHub.getInstance().getDocHandler().getItemDoc().getConfig();
		String abs = "items." + key + ".";
		config.set(abs + "material", stack.getType().name());
		if (stack.hasItemMeta()) {
			if (stack.getItemMeta().hasDisplayName() && stack.getItemMeta().getDisplayName() != null) {
				config.set(abs + "name", stack.getItemMeta().getDisplayName());
			}
			if (stack.getItemMeta().hasCustomModelData() && stack.getItemMeta().getCustomModelData() != 0) {
				config.set(abs + "model-data", stack.getItemMeta().getCustomModelData());
			}
			if (stack.getItemMeta().hasLore() && stack.getItemMeta().getLore() != null) {
				config.set(abs + "lore", stack.getItemMeta().getLore());
			}
		}
		if (!stack.getEnchantments().isEmpty()) {
			int i = 0;
			for (Map.Entry<Enchantment, Integer> entry : stack.getEnchantments().entrySet()) {
				config.set(abs + "enchants." + i + ".type", entry.getKey().getName());
				config.set(abs + "enchants." + i + ".power", entry.getValue());
				++i;
			}
		}
		config.set(abs + "slot", slot);
		config.set(abs + "amount", stack.getAmount());
		config.set(abs + "data", stack.getDurability());
		config.set(abs + "clicks", Arrays.asList(
				"RIGHT_CLICK_BLOCK",
				"RIGHT_CLICK_AIR"
		));
		config.set(abs + "action.type", "NONE");
		config.set(abs + "action.value", "");
		lHub.getInstance().getDocHandler().getItemDoc().getDoc().save();
	}

	@Override
	public void load(lHub plugin) {
		reload();
	}

	@Override
	public void unload(lHub plugin) {

	}

	@Override
	public void reload() {
		items.clear();
		try {
			lHub.getInstance().getDocHandler().getItemDoc().getDoc().reloadConfig();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		load();
		Bukkit.getOnlinePlayers().forEach(this::setItems);
	}

	public void load() {
		YamlConfiguration config = lHub.getInstance().getDocHandler().getItemDoc().getConfig();
		for (String key : config.getConfigurationSection("items").getKeys(false)) {

			ItemBuilder builder = new ItemBuilder(Material.valueOf(config.getString("items." + key + ".material")));

			builder.setDisplayName(config.getString("items." + key + ".name"));
			builder.setLore(config.getStringList("items." + key + ".lore"));
			builder.setAmount(config.getInt("items." + key + ".amount"));
			builder.setData(config.getInt("items." + key + ".data"));
			builder.setModelData(config.getInt("items." + key + ".model-data"));

			if (config.contains("items." + key + ".enchants")) {
				for (String eKey : config.getConfigurationSection("items." + key + ".enchants").getKeys(false)) {
					builder.addEnchant(Enchantment.getByName(config.getString("items." + key + ".enchants." + eKey + ".type")), config.getInt("items." + key + ".enchants." + eKey + ".power"));
				}
			}

			List<Action> clickTypes = config.getStringList("items." + key + ".clicks").stream().map(org.bukkit.event.block.Action::valueOf).collect(Collectors.toList());

			Item item = new Item(key, builder.create(), config.getInt("items." + key + ".slot"), clickTypes, config.getString("items." + key + ".action.type"), config.getString("items." + key + ".action.value"));
			items.add(item);
		}
	}

	public void setItems(Player player) {
		player.getInventory().clear();
		player.setHealth(20);
		player.setFoodLevel(20);
		for (Item item : lHub.getInstance().getItemHandler().getItems()) {
			player.getInventory().setItem(item.getSlot() - 1, item.getStack());
		}
	}

}
