package dev.lbuddyboy.hub.util;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	public static final String UNICODE_ARROW_RIGHT = StringEscapeUtils.unescapeJava("\u25B6");
	public static final String UNICODE_ARROWS_LEFT = StringEscapeUtils.unescapeJava("\u00AB");
	public static final String UNICODE_ARROWS_RIGHT = StringEscapeUtils.unescapeJava("\u00BB");
	public static final String UNICODE_HEART = StringEscapeUtils.unescapeJava("\u2764");
	public static final String MENU_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------";
	public static final String CHAT_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------------";
	public static final String SB_BAR = ChatColor.GRAY.toString() + ChatColor.STRIKETHROUGH.toString() + "----------------------";

	private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";
	private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}}");

	private static final List<String> SPECIAL_COLORS = Arrays.asList("&l", "&n", "&o", "&k", "&m", "§l", "§n", "§o", "§k", "§m");

	public static String translate(String message) {
		Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
		Matcher matcher = pattern.matcher(message);

		while (matcher.find()) {
			String code = message.substring(matcher.start(), matcher.end());
			net.md_5.bungee.api.ChatColor color = net.md_5.bungee.api.ChatColor.of(code);

			message = message.replace(
					code,
					color + ""
			);

			matcher = pattern.matcher(
					message
			);
		}

		return ChatColor.translateAlternateColorCodes('&', message
				.replaceAll("&h", "&x&f&b&c&b&4&f")
				.replaceAll("&g", "&x&f&b&9&4&4&a"));
	}

	public static List<String> translate(List<String> lines) {
		List<String> toReturn = new ArrayList<>();

		for (String line : lines) {
			toReturn.add(translate(line));
		}

		return toReturn;
	}

	public static List<String> translate(String[] lines) {
		List<String> toReturn = new ArrayList<>();

		for (String line : lines) {
			if (line != null) {
				toReturn.add(translate(line));
			}
		}

		return toReturn;
	}

	public static String translate(String string, Object... format) {
		for (int i = 0; i < format.length; i += 2) {
			string = string.replace((String) format[i], String.valueOf(format[i + 1]));
		}
		return translate(string);
	}

	public static List<String> translate(List<String> lore, Object... objects) {
		ArrayList<String> toAdd = new ArrayList<>();

		for (String lor : lore) {
			toAdd.add(translate(lor, objects));
		}

		return toAdd;
	}

}
