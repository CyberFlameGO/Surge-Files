package dev.lbuddyboy.hub.menu;

import dev.lbuddyboy.hub.lHub;
import dev.lbuddyboy.hub.placeholder.Placeholder;
import dev.lbuddyboy.hub.placeholder.PlaceholderImpl;
import dev.lbuddyboy.hub.util.CC;
import dev.lbuddyboy.hub.util.CompMaterial;
import dev.lbuddyboy.hub.util.ItemBuilder;
import dev.lbuddyboy.hub.util.YMLBase;
import dev.lbuddyboy.hub.util.menu.Button;
import dev.lbuddyboy.hub.util.menu.Menu;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 22/09/2021 / 1:47 AM
 * LBuddyBoy-Development / me.lbuddyboy.hub.menu
 */

@AllArgsConstructor
@Data
public class CustomMenu {

	private String title = "Default Title";
	private int size = 27;
	private boolean autofill = true;
	private YMLBase file;

	public CustomMenu(YMLBase file) {
		this.file = file;
	}
	
	public Menu createMenu() {
		return new Menu() {

			@Override
			public String getTitle(Player player) {
				return CC.translate(title
						.replaceAll("%player%", player.getName())
				);
			}

			@Override
			public boolean autoUpdate() {
				return true;
			}

			@Override
			public boolean autoFill() {
				return autofill;
			}

			@Override
			public int getSize(Player player) {
				return size;
			}

			@Override
			public List<Button> getButtons(Player var1) {
				List<Button> buttons = new ArrayList<>();
				FileConfiguration config = file.getConfiguration();

				ConfigurationSection buttonSection = config.getConfigurationSection("buttons");
				if (config.contains("filler-buttons")) {
					for (String s : config.getStringList("filler-buttons")) {
						String[] parts = s.split(";");
						ItemBuilder builder = new ItemBuilder(CompMaterial.fromString(parts[0]).toItem());
						int slot = Integer.parseInt(parts[1]);
						String name = parts[2];
						boolean glowing = Boolean.parseBoolean(parts[3]);
						boolean hideAll = Boolean.parseBoolean(parts[4]);

						builder.setDisplayName(name);
						if (glowing) builder.addEnchant(Enchantment.DURABILITY, 1);
						if (hideAll) builder.setItemFlags(ItemFlag.values());

						buttons.add(new FillerButton(slot, builder.getStack()));
					}
				}

				for (String key : buttonSection.getKeys(false)) {
					String material = buttonSection.getString(key + ".material");
					String name = buttonSection.getString(key + ".name");
					List<String> lore = new ArrayList<>();
					int amount = buttonSection.getInt(key + ".amount");
					int data = buttonSection.getInt(key + ".data");
					int slot = buttonSection.getInt(key + ".slot");
					Map<String, String> actions = new HashMap<>();
					ItemBuilder builder = new ItemBuilder(Material.getMaterial(material));

					if (buttonSection.contains(key + ".item-flags")) {
						builder.setItemFlags(buttonSection.getStringList(key + ".item-flags").stream().map(ItemFlag::valueOf).toArray(ItemFlag[]::new));
					}

					if (buttonSection.contains(key + ".glowing")) {
						if (buttonSection.getBoolean(key + ".glowing")) builder.addEnchant(Enchantment.DURABILITY, 1);
					}

					for (String s : buttonSection.getStringList(key + ".lore")) {
						for (PlaceholderImpl impl : lHub.getInstance().getPlaceholderHandler().getPlaceholderImpls()) {
							s = impl.applyPlaceholders(s, var1);
							s = impl.applyPlaceholders(s);
						}
						for (Placeholder placeholder : lHub.getInstance().getSettingsHandler().getPlaceholders()) {
							s = s.replaceAll(placeholder.getHolder(), placeholder.getReplacement());
						}
						lore.add(s);
					}

					for (PlaceholderImpl impl : lHub.getInstance().getPlaceholderHandler().getPlaceholderImpls()) {
						name = impl.applyPlaceholders(name, var1);
						name = impl.applyPlaceholders(name);
					}
					for (Placeholder placeholder : lHub.getInstance().getSettingsHandler().getPlaceholders()) {
						name = name.replaceAll(placeholder.getHolder(), placeholder.getReplacement());
					}
					
					builder.setAmount(amount);
					builder.setDisplayName(CC.translate(name));
					builder.setLore(lore);
					builder.setData(data);


					for (String action : buttonSection.getConfigurationSection(key + ".actions").getKeys(false)) {
						actions.put(action, buttonSection.getString(key + ".actions." + action));
					}

					buttons.add(new CustomButton(slot, key, builder.create(), actions));
				}
				return buttons;
			}
		};
	}

	public void save() {
		FileConfiguration config = this.file.getConfiguration();

		config.set("title", this.title);
		config.set("size", this.size);
		config.set("auto-fill", this.autofill);

		config.set("buttons.Test-Server.material", Material.REDSTONE_BLOCK.name());
		config.set("buttons.Test-Server.name", "&6&lTest Server &7(%server-status-Test&7)");
		config.set("buttons.Test-Server.lore", Arrays.asList(
				"&71.8-1.19 & Bedrock+",
				"",
				"&fA test server used for testing",
				"&fthe test plugins.",
				"",
				"&7Click to play with &6%server-online-Test%&7 others."
		));
		config.set("buttons.Test-Server.amount", 1);
		config.set("buttons.Test-Server.data", 0);
		config.set("buttons.Test-Server.slot", 14);
		config.set("buttons.Test-Server.actions.ADD_QUEUE", "Test");
		config.set("buttons.Test-Server.actions.SEND_MESSAGE", "&aYou have been added to the Test queue.");


		this.file.save();
	}

	@AllArgsConstructor
	public class FillerButton extends Button {

		private int slot;
		private ItemStack stack;

		@Override
		public int getSlot() {
			return this.slot;
		}

		@Override
		public ItemStack getItem() {
			return this.stack;
		}

	}

}
