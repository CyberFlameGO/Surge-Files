package me.lbuddyboy.staff.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class CC {

	public static String chat(String msg) {
		return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', msg.replaceAll("&g", "&x&0&6&9&2&f&f"));
	}


	public static BaseComponent[] translate(String s) {
		return TextComponent.fromLegacyText(s.replaceAll("&g", "&#0e49fb"));
	}

	public static ItemStack getCustomHead(String name, int amount, String url) {
		ItemStack skull = new ItemStack(Material.PLAYER_HEAD, amount);
		SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

		assert skullMeta != null;

		if (url.length() < 16) {
			skullMeta.setOwner(url);
		} else {
			StringBuilder s_url = new StringBuilder();
			s_url.append("https://textures.minecraft.net/texture/").append(url);
			GameProfile gameProfile = new GameProfile(UUID.randomUUID(), (String) null);
			byte[] data = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", s_url.toString()).getBytes());
			gameProfile.getProperties().put("textures", new Property("textures", new String(data)));

			try {
				Field field = skullMeta.getClass().getDeclaredField("profile");
				field.setAccessible(true);
				field.set(skullMeta, gameProfile);
			} catch (Exception var9) {
				var9.printStackTrace();
			}

		}
		skullMeta.setDisplayName(chat(name));
		skull.setItemMeta(skullMeta);
		return skull;
	}


	public static List<String> chat(List<String> lore) {
		List<String> lines = new ArrayList<>();
		for (String s : lore) {
			lines.add(ChatColor.translateAlternateColorCodes('&', s));
		}
		return lines;
	}

}
