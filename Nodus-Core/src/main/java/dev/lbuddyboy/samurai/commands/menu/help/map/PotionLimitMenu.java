package dev.lbuddyboy.samurai.commands.menu.help.map;

import dev.lbuddyboy.samurai.util.menu.Button;
import dev.lbuddyboy.samurai.util.menu.Menu;
import dev.lbuddyboy.samurai.util.menu.buttons.BackButton;
import dev.lbuddyboy.samurai.commands.menu.help.MapHelpMenu;
import dev.lbuddyboy.samurai.util.object.ItemBuilder;
import dev.lbuddyboy.samurai.util.CC;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LBuddyBoy (lbuddyboy.me)
 * 18/12/2021 / 4:36 AM
 * SteelHCF-main / com.steelpvp.hcf.command.menu.help.map
 */
public class PotionLimitMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return CC.translate("&aPotion Limitations");
	}

	@Override
	public Map<Integer, Button> getButtons(Player var1) {
		Map<Integer, Button> buttons = new HashMap<>();

		buttons.put(13, new BackButton(new MapHelpMenu()));

		int i = 26;
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aPoison&7: &c1 &a(Enabled)")).build(), new PotionEffect(PotionEffectType.POISON, 20 * 45, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aSlowness&7: &c1 &a(Enabled)")).build(), new PotionEffect(PotionEffectType.SLOW, 20 * 90, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aHarmness&7: &cDisabled")).build(), new PotionEffect(PotionEffectType.HARM, 0, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aStrength&7: &c1 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 8, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aStrength&7: &c1 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 3, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.SPLASH_POTION).displayName(CC.translate("&aStrength&7: &c2 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 90, 1)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.POTION).displayName(CC.translate("&aStrength&7: &c1 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 8, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.POTION).displayName(CC.translate("&aStrength&7: &c1 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 60 * 3, 0)));
		buttons.put(++i, fromPotion(new ItemBuilder(Material.POTION).displayName(CC.translate("&aStrength&7: &c2 &c(Disabled)")).build(), new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 90, 1)));

		return buttons;
	}

	@Override
	public int size(Map<Integer, Button> buttons) {
		int size = super.size(buttons) + 9;
		return (Math.min(size, 54));
	}

	public static Button fromPotion(final ItemStack item, PotionEffect effect) {
		return new Button() {

			@Override
			public ItemStack getButtonItem(Player player) {
				ItemStack stack = item.clone();
				PotionMeta meta = (PotionMeta) stack.getItemMeta();
				boolean extended = (effect.getDuration() > effect.getType().getDurationModifier()) && (effect.getAmplifier() < 0);
				boolean upgrade = (effect.getAmplifier() > 0) && (effect.getDuration() < effect.getType().getDurationModifier());
//                meta.setBasePotionData(new PotionData(PotionType.getByEffect(effect.getType()), upgrade, extended));
				meta.setMainEffect(effect.getType());
				meta.addCustomEffect(effect, false);
				stack.setItemMeta(meta);

				return stack;
			}

			@Override
			public String getName(Player player) {
				return null;
			}

			@Override
			public List<String> getDescription(Player player) {
				return null;
			}

			@Override
			public Material getMaterial(Player player) {
				return null;
			}

			@Override
			public void clicked(Player player, int slot, ClickType clickType) {

			}
		};
	}
	
}
