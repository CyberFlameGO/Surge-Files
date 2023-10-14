package dev.lbuddyboy.gkits.enchanter;

import dev.lbuddyboy.gkits.enchanter.listener.EnchanterListener;
import dev.lbuddyboy.gkits.lGKits;
import dev.lbuddyboy.gkits.util.CC;
import dev.lbuddyboy.gkits.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 09/07/2021 / 10:59 PM
 * GKits / me.lbuddyboy.gkits.enchanter
 */
public class EnchanterManager {

	@Getter private final ItemStack item;

	public EnchanterManager() {
		Bukkit.getPluginManager().registerEvents(new EnchanterListener(), lGKits.getInstance());

		item = new ItemBuilder(Material.NETHER_STAR)
				.setName(CC.translate("&g&lEnchantment Crystal"))
				.setLore(CC.translate(Arrays.asList(
						"",
						"&fClick to receive a &grandom enchantment&f gem that you can",
						"&gdrag&f and &gdrop&f on a piece of armor to add the custom",
						"&fenchantment on the rune to that piece of &ggear&f.",
						""
				)))
				.create();

	}

}
