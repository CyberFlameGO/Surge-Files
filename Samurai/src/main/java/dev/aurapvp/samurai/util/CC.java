package dev.aurapvp.samurai.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class CC {

	public static final String BLUE = ChatColor.BLUE.toString();
	public static final String AQUA = ChatColor.AQUA.toString();
	public static final String YELLOW = ChatColor.YELLOW.toString();
	public static final String RED = ChatColor.RED.toString();
	public static final String GRAY = ChatColor.GRAY.toString();
	public static final String GOLD = ChatColor.GOLD.toString();
	public static final String GREEN = ChatColor.GREEN.toString();
	public static final String WHITE = ChatColor.WHITE.toString();
	public static final String BLACK = ChatColor.BLACK.toString();
	public static final String BOLD = ChatColor.BOLD.toString();
	public static final String ITALIC = ChatColor.ITALIC.toString();
	public static final String UNDER_LINE = ChatColor.UNDERLINE.toString();
	public static final String STRIKE_THROUGH = ChatColor.STRIKETHROUGH.toString();
	public static final String RESET = ChatColor.RESET.toString();
	public static final String MAGIC = ChatColor.MAGIC.toString();
	public static final String DARK_BLUE = ChatColor.DARK_BLUE.toString();
	public static final String DARK_AQUA = ChatColor.DARK_AQUA.toString();
	public static final String DARK_GRAY = ChatColor.DARK_GRAY.toString();
	public static final String DARK_GREEN = ChatColor.DARK_GREEN.toString();
	public static final String DARK_PURPLE = ChatColor.DARK_PURPLE.toString();
	public static final String DARK_RED = ChatColor.DARK_RED.toString();
	public static final String PINK = ChatColor.LIGHT_PURPLE.toString();
	public static final String UNICODE_VERTICAL_BAR = CC.GRAY + StringEscapeUtils.unescapeJava("\u2503");
	public static final String UNICODE_CAUTION = StringEscapeUtils.unescapeJava("\u26a0");
	public static final String UNICODE_ARROW_LEFT = StringEscapeUtils.unescapeJava("\u25C0");
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("▶");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
	public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 40);
	public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 40);
	public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 16);

	private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");
	public static java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<G:([0-9A-Fa-f]{6})>(.*?)</G:([0-9A-Fa-f]{6})>");

	public static String translate(String string) {
		return ChatColor.translateAlternateColorCodes('&', string.replaceAll("&g", "&x&0&6&9&2&f&f"));
	}

	private static String withoutSpecialChar(String source) {
		String workingString = source;
		for (String color : SPECIAL_COLORS) {
			if (workingString.contains(color)) {
				workingString = workingString.replace(color, "");
			}
		}
		return workingString;
	}

	public static void broadcast(String message, Object... format) {
		for (Player staff : Bukkit.getOnlinePlayers()) {
			staff.sendMessage(translate(message, format));
		}
		Bukkit.getConsoleSender().sendMessage(translate(message, format));
	}

	public static String translate(String string, Object... format) {
		return translate(format(string, format));
	}

	public static String format(String string, Object... format) {
		for (int i = 0; i < format.length; i += 2) {
			string = string.replace((String) format[i], String.valueOf(format[i + 1]));
		}
		return string;
	}

	public static List<String> format(List<String> strings, Object... format) {
		ArrayList<String> toAdd = new ArrayList<>();

		for (String string : strings) {
			for (int i = 0; i < format.length; i += 2) {
				toAdd.add(string.replace((String) format[i], String.valueOf(format[i + 1])));
			}
		}

		return toAdd;
	}

	public static List<String> translate(List<String> lore) {
		ArrayList<String> toAdd = new ArrayList();

		for (String lor : lore) {
			toAdd.add(translate(lor));
		}

		return toAdd;
	}

	public static List<String> translate(List<String> lore, Object... objects) {
		ArrayList<String> toAdd = new ArrayList();

		for (String lor : lore) {
			toAdd.add(translate(lor, objects));
		}

		return toAdd;
	}

	public static ItemStack getCustomSkull(String url) {
		ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1, (short) 3);
		SkullMeta skullMeta = (SkullMeta)head.getItemMeta();

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
		head.setItemMeta(skullMeta);

		return head;
	}

	public static final ArrayList<ChatColor> woolColors = new ArrayList<>(Arrays.asList(ChatColor.WHITE, ChatColor.GOLD, ChatColor.LIGHT_PURPLE,
			ChatColor.AQUA, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.LIGHT_PURPLE, ChatColor.DARK_GRAY,
			ChatColor.GRAY, ChatColor.DARK_AQUA, ChatColor.DARK_PURPLE, ChatColor.BLUE, ChatColor.RESET,
			ChatColor.DARK_GREEN, ChatColor.RED, ChatColor.BLACK));
	public static final Map<Character, ChatColor> CODE_TO_COLOR = new HashMap<Character, ChatColor>(){{
		for (ChatColor c : ChatColor.values()) {
			put(c.getChar(), c);
		}
	}};

	public static int toDyeColor(ChatColor color) {
		if(color == ChatColor.DARK_RED) color = ChatColor.RED;
		if (color == ChatColor.DARK_BLUE) color = ChatColor.BLUE;

		return woolColors.indexOf(color);
	}

}
